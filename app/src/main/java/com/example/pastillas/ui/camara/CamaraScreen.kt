package com.example.pastillas.ui.camara


import Box
import aiModel
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pastillas.ui.viewmodel.TomaViewModel
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.pastillas.ui.components.botones.BotonDialogo
import com.example.pastillas.ui.components.botones.BotonHacerFoto
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import java.io.ByteArrayOutputStream


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CamaraScreen(
    navController: NavController,
    indexToma: Int,
    viewModel: TomaViewModel = viewModel()
) {
    val tomas = viewModel.tomasDisponibles
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val aiModel = remember { aiModel(context) }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var listaDeBoxes by remember { mutableStateOf<List<Box>>(emptyList()) }

    // PERMISO CÁMARA
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (!cameraPermissionState.status.isGranted) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Esperando permiso de cámara…")
        }
        return
    }

    if (tomas.isEmpty() || indexToma !in tomas.indices) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay toma disponible")
        }
        return
    }

    val toma = tomas[indexToma]

    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Toma: ${toma.nombre}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ZONA CÁMARA
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageCaptureUseCase = ImageCapture.Builder()
                        .setTargetRotation(previewView.display.rotation)
                        .build()
                    imageCapture = imageCaptureUseCase

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCaptureUseCase
                    )
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            },
            modifier = Modifier.fillMaxWidth().weight(1f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // BOTÓN TOMAR FOTO
        BotonHacerFoto(
            onClick = {
                val capture = imageCapture ?: return@BotonHacerFoto

                capture.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback() {

                        override fun onCaptureSuccess(image: ImageProxy) {
                            try {
                                val bitmap = image.toBitmap()
                                image.close()
                                val detections = aiModel.detectWithBoxes(bitmap)

                                Log.d("YOLO_DEBUG", "Detecciones encontradas: ${detections.size}")

                                photoBitmap = bitmap
                                listaDeBoxes = detections
                                showDialog = true

                            } catch (e: Exception) {
                                Log.e("YOLO_DEBUG", "Error: ${e.message}")
                                image.close()
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e("YOLO_DEBUG", "Error al capturar imagen: ${exception.message}")
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        // DIALOG FOTO
        if (showDialog && photoBitmap != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                text = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            // Imagen original
                            Image(
                                bitmap = photoBitmap!!.asImageBitmap(),
                                contentDescription = "Foto capturada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )

                            //Canvas para dibujar los hitboxes encima
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val bmp = photoBitmap!!
                                val scale = minOf(size.width / bmp.width, size.height / bmp.height)
                                val offsetX = (size.width - (bmp.width * scale)) / 2f
                                val offsetY = (size.height - (bmp.height * scale)) / 2f

                                listaDeBoxes.forEach { box ->
                                    drawRect(
                                        color = Color.Green,
                                        topLeft = Offset(
                                            (box.left * scale) + offsetX,
                                            (box.top * scale) + offsetY
                                        ),
                                        size = Size(
                                            (box.right - box.left) * scale,
                                            (box.bottom - box.top) * scale
                                        ),
                                        style = Stroke(width = 6f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Pastillas detectadas: ${listaDeBoxes.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                confirmButton = {
                    BotonDialogo(
                        modifier = Modifier.fillMaxWidth(),
                        text = "ANALIZAR",



                        onClick = {
                            navController.navigate("confirmacion/$indexToma/${listaDeBoxes.size}")
                            showDialog = false
                        }
                    )
                },
                dismissButton = {
                    BotonDialogo(
                        modifier = Modifier.fillMaxWidth(),
                        text = "REPETIR",
                        onClick = {
                            photoBitmap = null
                            listaDeBoxes = emptyList()
                            showDialog = false
                        }
                    )
                }
            )
        }
    }
}

// Convertir ImageProxy a Bitmap
fun ImageProxy.toBitmap(): Bitmap {
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
    val imageBytes = out.toByteArray()
    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

    val matrix = Matrix()
    matrix.postRotate(imageInfo.rotationDegrees.toFloat())
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
