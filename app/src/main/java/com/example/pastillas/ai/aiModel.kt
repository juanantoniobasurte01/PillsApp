import android.content.Context
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream
import android.graphics.Bitmap
import android.util.Log
import kotlin.math.max
import kotlin.math.min
import android.graphics.Canvas
import android.graphics.Paint
import ai.onnxruntime.*
import android.graphics.Color
import java.nio.FloatBuffer
import kotlin.math.roundToInt


data class Box(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val score: Float
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

    private data class PreprocessResult(
        val bitmap: Bitmap,
        val scale: Float,
        val padX: Float,
        val padY: Float
    )

    private fun letterbox(bitmap: Bitmap, size: Int): PreprocessResult {
        val srcW = bitmap.width.toFloat()
        val srcH = bitmap.height.toFloat()
        val scale = min(size / srcW, size / srcH)
        val newW = (srcW * scale).roundToInt()
        val newH = (srcH * scale).roundToInt()
        val padX = ((size - newW) / 2f)
        val padY = ((size - newH) / 2f)

        val resized = Bitmap.createScaledBitmap(bitmap, newW, newH, true)
        val out = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(Color.BLACK)
        canvas.drawBitmap(resized, padX, padY, null)
        return PreprocessResult(out, scale, padX, padY)
    }

    fun detectWithBoxes(bitmap: Bitmap): List<Box> {
        val size = 640
        val pre = letterbox(bitmap, size)
        val inputBitmap = pre.bitmap

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


        val outputValue = results[0].value
        val raw = when (outputValue) {
            is Array<*> -> {
                if (outputValue.isNotEmpty() && outputValue[0] is Array<*>) {
                    @Suppress("UNCHECKED_CAST")
                    (outputValue as Array<Array<FloatArray>>)[0]
                } else {
                    @Suppress("UNCHECKED_CAST")
                    outputValue as Array<FloatArray>
                }
            }
            else -> throw IllegalStateException("Salida ONNX no soportada: ${outputValue::class.java}")
        }

        val data = if (raw.size > 0 && raw.size > raw[0].size) {
            // Forma [num_boxes, num_attrs] -> trasponer a [num_attrs, num_boxes]
            val numBoxes = raw.size
            val numAttrs = raw[0].size
            val t = Array(numAttrs) { FloatArray(numBoxes) }
            for (i in 0 until numBoxes) {
                for (j in 0 until numAttrs) {
                    t[j][i] = raw[i][j]
                }
            }
            t
        } else {
            raw
        }

        val numAttrs = data.size
        val numBoxes = data[0].size

        val boxes = mutableListOf<Box>()

        val confThreshold = 0.25f
        val iouThreshold = 0.70f

        for (i in 0 until numBoxes) {
            val xCenter = data[0][i]
            val yCenter = data[1][i]
            val w = data[2][i]
            val h = data[3][i]

            val score = if (numAttrs > 5) {
                var maxScore = -1f
                for (c in 4 until numAttrs) {
                    val s = data[c][i]
                    if (s > maxScore) maxScore = s
                }
                maxScore
            } else {
                data[4][i]
            }

            if (score >= confThreshold) {
                val left = xCenter - (w / 2f)
                val top = yCenter - (h / 2f)
                val right = xCenter + (w / 2f)
                val bottom = yCenter + (h / 2f)
                boxes.add(Box(left, top, right, bottom, score))
            }
        }

        val nmsBoxes = nms(boxes, iouThreshold)

        val scaled = nmsBoxes.map { b ->
            val left = ((b.left - pre.padX) / pre.scale).coerceIn(0f, bitmap.width.toFloat())
            val top = ((b.top - pre.padY) / pre.scale).coerceIn(0f, bitmap.height.toFloat())
            val right = ((b.right - pre.padX) / pre.scale).coerceIn(0f, bitmap.width.toFloat())
            val bottom = ((b.bottom - pre.padY) / pre.scale).coerceIn(0f, bitmap.height.toFloat())
            Box(left, top, right, bottom, b.score)
        }

        results.close()
        tensor.close()
        return scaled
    }



    private fun nms(boxes: List<Box>, iouThreshold: Float): List<Box> {
        val sorted = boxes.sortedByDescending { it.score }
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
        val interX1 = max(a.left, b.left)
        val interY1 = max(a.top, b.top)
        val interX2 = min(a.right, b.right)
        val interY2 = min(a.bottom, b.bottom)

        val interArea = max(0f, interX2 - interX1) * max(0f, interY2 - interY1)
        val areaA = max(0f, a.right - a.left) * max(0f, a.bottom - a.top)
        val areaB = max(0f, b.right - b.left) * max(0f, b.bottom - b.top)
        val unionArea = areaA + areaB - interArea
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
            canvas.drawRect(b.left, b.top, b.right, b.bottom, paint)
        }

        return mutable
    }
}
