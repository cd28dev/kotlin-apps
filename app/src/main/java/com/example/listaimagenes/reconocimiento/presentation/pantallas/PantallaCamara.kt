package com.example.listaimagenes.reconocimiento.presentation.pantallas

import android.os.Environment
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listaimagenes.reconocimiento.presentation.components.BotonesCamara
import com.example.listaimagenes.reconocimiento.presentation.viewmodel.PersonaViewModel
import java.io.File

@Composable
fun PantallaCamara(
    vistaModelo: PersonaViewModel = viewModel(),
    alVolver: () -> Unit,
    onFotoCapturada: ((String) -> Unit)? = null
) {
    val propietarioCiclo = LocalLifecycleOwner.current
    val contexto = LocalContext.current
    val capturaImagen = remember { mutableStateOf<ImageCapture?>(null) }
    val camaraSel = remember { mutableStateOf(CameraSelector.DEFAULT_FRONT_CAMERA) }

    Box(modifier = Modifier.fillMaxSize()) {

        VistaCamara(
            propietarioCiclo = propietarioCiclo,
            capturaImagen = capturaImagen,
            camaraSel = camaraSel
        )

        BotonesCamara(
            alCancelar = alVolver,
            alTomarFoto = {
                val captura = capturaImagen.value ?: return@BotonesCamara

                val photoFile = File(
                    contexto.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "temp_foto_${System.currentTimeMillis()}.jpg"
                )

                val opcionesSalida = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                captura.takePicture(
                    opcionesSalida,
                    ContextCompat.getMainExecutor(contexto),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(resultado: ImageCapture.OutputFileResults) {
                            val fotoPath = photoFile.absolutePath
                            if (onFotoCapturada != null) {
                                onFotoCapturada(fotoPath)
                            } else {
                                android.util.Log.d("PantallaCamara", "📸 Foto capturada: $fotoPath")
                                val bitmap = android.graphics.BitmapFactory.decodeFile(fotoPath)
                                vistaModelo.establecerImagenFacial(bitmap)
                                vistaModelo.mostrarCamara(false)
                                android.util.Log.d("PantallaCamara", "🔄 Ocultando cámara y volviendo al formulario")
                            }
                            Toast.makeText(contexto, "Foto capturada", Toast.LENGTH_SHORT).show()
                            alVolver()
                        }
                        override fun onError(error: ImageCaptureException) {
                            Toast.makeText(contexto, "Error al capturar la foto", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            },
            alCambiarCamara = {
                camaraSel.value =
                    if (camaraSel.value == CameraSelector.DEFAULT_FRONT_CAMERA)
                        CameraSelector.DEFAULT_BACK_CAMERA
                    else
                        CameraSelector.DEFAULT_FRONT_CAMERA
            }
        )
    }
}

@Composable
fun VistaCamara(
    propietarioCiclo: LifecycleOwner,
    capturaImagen: MutableState<ImageCapture?>,
    camaraSel: MutableState<CameraSelector>
) {
    val contexto = LocalContext.current
    val vistaPreview = remember { PreviewView(contexto) }

    LaunchedEffect(camaraSel.value) {
        try {
            val provCamaraFuturo = ProcessCameraProvider.getInstance(contexto)
            val provCamara = provCamaraFuturo.get()

            val preview = Preview.Builder()
                .setTargetRotation(android.view.Surface.ROTATION_0)
                .build()
                .also { it.setSurfaceProvider(vistaPreview.surfaceProvider) }

            val captura = ImageCapture.Builder()
                .setTargetRotation(android.view.Surface.ROTATION_0)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            
            capturaImagen.value = captura
            provCamara.unbindAll()
            provCamara.bindToLifecycle(
                propietarioCiclo,
                camaraSel.value,
                preview,
                captura
            )
            
        } catch (e: Exception) {
            android.util.Log.e("PantallaCamara", "Error configurando cámara: ${e.message}", e)
        }
    }

    AndroidView(
        factory = { vistaPreview },
        modifier = Modifier.fillMaxSize()
    )
}
