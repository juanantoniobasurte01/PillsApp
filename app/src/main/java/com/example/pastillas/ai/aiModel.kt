import android.content.Context
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.max
import kotlin.math.min
import android.graphics.Canvas
import android.graphics.Paint
import ai.onnxruntime.*
import android.graphics.Color
import java.nio.FloatBuffer


data class Box(
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float
)

class aiModel(context: Context) {

    private val session: OrtSession
    private val env: OrtEnvironment

    init {
        try {
            env = OrtEnvironment.getEnvironment()
            val modelBytes = context.assets.open("best.onnx").readBytes()
            session = env.createSession(modelBytes)
        } catch (e: Exception) {
            Log.e("AI_MODEL", "Error cargando modelo ONNX: ${e.message}")
            throw e
        }
    }

    fun detectWithBoxes(bitmap: Bitmap): List<Box> {
        val size = 640
        val inputBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true)

        val floatValues = FloatArray(3 * size * size)
        val intValues = IntArray(size * size)
        inputBitmap.getPixels(intValues, 0, size, 0, 0, size, size)


        for (i in 0 until size * size) {
            val px = intValues[i]

            floatValues[i] = ((px shr 16 and 0xFF) / 255.0f)
            floatValues[i + size * size] = ((px shr 8 and 0xFF) / 255.0f)
            floatValues[i + 2 * size * size] = ((px and 0xFF) / 255.0f)
        }

        val buffer = FloatBuffer.wrap(floatValues)
        val tensor = OnnxTensor.createTensor(env, buffer, longArrayOf(1, 3, 640, 640))

        val results = session.run(mapOf(session.inputNames.iterator().next() to tensor))


        // La salida es [1][5][8400]
        val output = results[0].value as Array<Array<FloatArray>>
        val data = output[0]
        val rows = data.size
        val columns = data[0].size

        val boxes = mutableListOf<Box>()

        for (c in 0 until columns) {
            val confidence = data[4][c]

            if (confidence > 0.40f) {
                val xCenter = data[0][c]
                val yCenter = data[1][c]
                val w = data[2][c]
                val h = data[3][c]


                val left = xCenter - (w / 2)
                val top = yCenter - (h / 2)
                val right = xCenter + (w / 2)
                val bottom = yCenter + (h / 2)

                boxes.add(Box(left, top, right, bottom))
            }
        }

        tensor.close()
        return nms(boxes, 0.45f)
    }



    private fun nms(boxes: List<Box>, iouThreshold: Float): List<Box> {
        val sorted = boxes.sortedByDescending { it.h * it.w }
        val kept = mutableListOf<Box>()
        val removed = BooleanArray(sorted.size) { false }

        for (i in sorted.indices) {
            if (removed[i]) continue
            kept.add(sorted[i])
            for (j in i + 1 until sorted.size) {
                if (removed[j]) continue
                if (iou(sorted[i], sorted[j]) > iouThreshold) removed[j] = true
            }
        }
        return kept
    }

    private fun iou(a: Box, b: Box): Float {
        val ax1 = a.x - a.w / 2
        val ay1 = a.y - a.h / 2
        val ax2 = a.x + a.w / 2
        val ay2 = a.y + a.h / 2

        val bx1 = b.x - b.w / 2
        val by1 = b.y - b.h / 2
        val bx2 = b.x + b.w / 2
        val by2 = b.y + b.h / 2

        val interX1 = max(ax1, bx1)
        val interY1 = max(ay1, by1)
        val interX2 = min(ax2, bx2)
        val interY2 = min(ay2, by2)

        val interArea = max(0f, interX2 - interX1) * max(0f, interY2 - interY1)
        val unionArea = (ax2 - ax1) * (ay2 - ay1) + (bx2 - bx1) * (by2 - by1) - interArea
        return if (unionArea == 0f) 0f else interArea / unionArea
    }


    fun drawDetections(bitmap: Bitmap, boxes: List<Box>): Bitmap {
        val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutable)
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        for (b in boxes) {
            val left = (b.x - b.w / 2) * bitmap.width
            val top = (b.y - b.h / 2) * bitmap.height
            val right = (b.x + b.w / 2) * bitmap.width
            val bottom = (b.y + b.h / 2) * bitmap.height
            canvas.drawRect(left, top, right, bottom, paint)
        }

        return mutable
    }
}
