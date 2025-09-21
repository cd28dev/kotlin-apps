package com.example.listaimagenes.presentation.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.listaimagenes.R
import com.example.listaimagenes.presentation.theme.AppTypography
import com.example.listaimagenes.presentation.theme.ColoresApp
import com.example.listaimagenes.presentation.theme.Tamaños
import com.example.listaimagenes.presentation.utils.ManejadorArchivos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCamara(
    alVolverConFoto: (String) -> Unit,
    alVolver: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var tienePermiso by remember { mutableStateOf(false) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var previsualizacion by remember { mutableStateOf<Preview?>(null) }
    
    // Launcher para solicitar permisos
    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        tienePermiso = concedido
    }
    
    // Verificar permisos al cargar
    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                tienePermiso = true
            }
            else -> {
                permisoLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Tomar Foto", color = ColoresApp.TextoInverso) },
            navigationIcon = {
                IconButton(onClick = alVolver) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = ColoresApp.TextoInverso
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = ColoresApp.Primario
            )
        )
        
        if (tienePermiso) {
            Box(modifier = Modifier.weight(1f)) {
                // Vista previa de la cámara
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            
                            previsualizacion = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            
                            imageCapture = ImageCapture.Builder().build()
                            
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    previsualizacion,
                                    imageCapture
                                )
                            } catch (exc: Exception) {
                                Log.e("CameraPreview", "Error al vincular casos de uso", exc)
                            }
                        }, ContextCompat.getMainExecutor(ctx))
                        
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
                
                // Botón de captura flotante
                FloatingActionButton(
                    onClick = {
                        val archivo = ManejadorArchivos.crearArchivoFoto(context)
                        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(archivo).build()
                        
                        imageCapture?.takePicture(
                            outputFileOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    alVolverConFoto(archivo.absolutePath)
                                }
                                
                                override fun onError(exception: ImageCaptureException) {
                                    Log.e("Camera", "Error al capturar imagen", exception)
                                }
                            }
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(Tamaños.EspacioGrande)
                        .size(70.dp),
                    shape = CircleShape,
                    containerColor = ColoresApp.Primario
                ) {
                    // 🔧 USAR TU ICONO PERSONALIZADO
                    Image(
                        painter = painterResource(id = R.drawable.camera_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                        contentDescription = "Tomar foto",
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        } else {
            // Pantalla de permisos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Tamaños.EspacioGrande),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFF3CD)
                )
            ) {
                Column(
                    modifier = Modifier.padding(Tamaños.EspacioGrande),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Se necesita permiso de cámara para tomar fotos",
                        style = AppTypography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    
                    Button(
                        onClick = { permisoLauncher.launch(Manifest.permission.CAMERA) },
                        modifier = Modifier.padding(top = Tamaños.EspacioChico),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColoresApp.Primario
                        )
                    ) {
                        Text("Conceder Permiso", color = ColoresApp.TextoInverso)
                    }
                }
            }
        }
    }
}