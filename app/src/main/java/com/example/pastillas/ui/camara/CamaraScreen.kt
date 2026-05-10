package com.example.pastillas.ui.camara

import Box
import aiModel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import android.view.Surface
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pastillas.ui.components.botones.BotonDialogo
import com.example.pastillas.ui.components.botones.BotonHacerFoto
import com.example.pastillas.ui.viewmodel.TomaViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

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
    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }



    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var photoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var listaDeBoxes by remember { mutableStateOf<List<Box>>(emptyList()) }
    var cameraErrorMessage by remember { mutableStateOf<String?>(null) }


    // PERMISO CÁMARA
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }


    DisposableEffect(lifecycleOwner, previewView, cameraPermissionState.status.isGranted) {
        if (!cameraPermissionState.status.isGranted) {
            onDispose { }
        } else {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            val executor = ContextCompat.getMainExecutor(context)

            val listener = Runnable {
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageCaptureUseCase = ImageCapture.Builder()
                        .setTargetRotation(previewView.display?.rotation ?: Surface.ROTATION_0)
                        .build()

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCaptureUseCase
                    )

                    imageCapture = imageCaptureUseCase
                    cameraErrorMessage = null
                } catch (exception: Exception) {
                    imageCapture = null
                    cameraErrorMessage = "No se pudo iniciar la camara"
                    Log.e("CAMARA", "Error al iniciar CameraX", exception)
                }
            }

            cameraProviderFuture.addListener(listener, executor)

            onDispose {
                runCatching {
                    cameraProviderFuture.get().unbindAll()
                }
            }
        }
    }




    if (!cameraPermissionState.status.isGranted) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Se necesita permiso de camara para hacer la foto",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                BotonDialogo(
                    text = "CONCEDER PERMISO",
                    onClick = { cameraPermissionState.launchPermissionRequest() }
                )
            }
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
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Toma: ${toma.nombre}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )


        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )

            if (cameraErrorMessage != null) {
                Text(
                    text = cameraErrorMessage!!,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        BotonHacerFoto(
            onClick = {
                val capture = imageCapture
                if (capture == null) {
                    cameraErrorMessage = "La camara no esta lista todavia"
                    return@BotonHacerFoto
                }

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
                            } catch (exception: Exception) {
                                Log.e("YOLO_DEBUG", "Error procesando imagen", exception)
                                cameraErrorMessage = "No se pudo procesar la foto"
                                image.close()
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e("YOLO_DEBUG", "Error al capturar imagen", exception)
                            cameraErrorMessage = "Error al capturar imagen"
                        }
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

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
                            Image(
                                bitmap = photoBitmap!!.asImageBitmap(),
                                contentDescription = "Foto capturada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )

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

fun ImageProxy.toBitmap(): Bitmap {
    val bitmap = when (format) {
        ImageFormat.JPEG -> planes[0].buffer.decodeJpegBitmap()
        ImageFormat.YUV_420_888 -> yuv420888ToBitmap()
        else -> error("Formato de imagen no soportado: $format")
    }

    return bitmap.rotate(imageInfo.rotationDegrees)
}

private fun ImageProxy.yuv420888ToBitmap(): Bitmap {
    val nv21 = yuv420888ToNv21()
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
    val imageBytes = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        ?: error("No se pudo decodificar la imagen YUV")
}

private fun ImageProxy.yuv420888ToNv21(): ByteArray {
    val ySize = width * height
    val chromaSize = width * height / 4
    val y = ByteArray(ySize)
    val u = ByteArray(chromaSize)
    val v = ByteArray(chromaSize)

    planes[0].copyPlane(width, height, y)
    planes[1].copyPlane(width / 2, height / 2, u)
    planes[2].copyPlane(width / 2, height / 2, v)

    return ByteArray(ySize + (chromaSize * 2)).apply {
        System.arraycopy(y, 0, this, 0, y.size)

        var outputIndex = ySize
        for (index in 0 until chromaSize) {
            this[outputIndex++] = v[index]
            this[outputIndex++] = u[index]
        }
    }
}

private fun ImageProxy.PlaneProxy.copyPlane(
    planeWidth: Int,
    planeHeight: Int,
    output: ByteArray
) {
    val bufferOffset = buffer.position()
    var outputIndex = 0

    for (row in 0 until planeHeight) {
        val rowOffset = bufferOffset + (row * rowStride)
        for (column in 0 until planeWidth) {
            output[outputIndex++] = buffer.get(rowOffset + (column * pixelStride))
        }
    }
}

private fun ByteBuffer.decodeJpegBitmap(): Bitmap {
    val bytes = ByteArray(remaining())
    get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        ?: error("No se pudo decodificar la imagen JPEG")
}

private fun Bitmap.rotate(rotationDegrees: Int): Bitmap {
    if (rotationDegrees == 0) {
        return this
    }

    val matrix = Matrix().apply {
        postRotate(rotationDegrees.toFloat())
    }

    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}
