package com.example.listaimagenes.presentation.pantallas

import android.os.Environment
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listaimagenes.presentation.viewmodel.PersonaViewModel
import java.io.File

@Composable
fun PantallaCamara(
    viewModel: PersonaViewModel = viewModel(),
    alVolver: () -> Unit)
{
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {

        CameraPreview(lifecycleOwner = lifecycleOwner, imageCaptureState = imageCapture)

        CameraActions(
            onCancelar = alVolver,
            onTomarFoto = {
                val capture = imageCapture.value ?: return@CameraActions

                val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                val file = File(picturesDir, "foto_${System.currentTimeMillis()}.jpg")

                val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

                capture.takePicture(outputOptions, ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            viewModel.establecerFoto(file.absolutePath)
                            viewModel.mostrarCamara(false)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            exception.printStackTrace()
                            Toast.makeText(context, "Error al capturar la foto", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        )
    }
}

@Composable
fun CameraPreview(lifecycleOwner: LifecycleOwner, imageCaptureState: MutableState<ImageCapture?>) {
    AndroidView(factory = { ctx ->
        val previewView = PreviewView(ctx)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageCapture = ImageCapture.Builder().build()
            imageCaptureState.value = imageCapture

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(ctx))

        previewView
    }, modifier = Modifier.fillMaxSize())
}


@Composable
fun CameraActions(onCancelar: () -> Unit, onTomarFoto: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = onCancelar) { Text("Cancelar") }
            Button(onClick = onTomarFoto) { Text("Tomar Foto") }
        }
    }
}
