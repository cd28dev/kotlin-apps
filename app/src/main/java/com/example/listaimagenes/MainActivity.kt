package com.example.listaimagenes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listaimagenes.domain.usecase.PersonaManager
import com.example.listaimagenes.presentation.theme.TemaApp
import com.example.listaimagenes.presentation.pantallas.PantallaFormularioPersona
import com.example.listaimagenes.presentation.pantallas.PantallaVisualizacionPersonas
import com.example.listaimagenes.presentation.viewmodel.PersonaViewModel

class MainActivity : ComponentActivity() {

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Se necesita permiso de cámara", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PersonaManager.init(applicationContext)

        checkAndRequestCameraPermission()

        setContent {
            TemaApp {
                AppPrincipal()
            }
        }
    }

    private fun checkAndRequestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}

@Composable
fun AppPrincipal() {
    var pantallaActual by remember { mutableStateOf("visualizacion") }
    val viewModel: PersonaViewModel = viewModel()

    when (pantallaActual) {
        "formulario" -> PantallaFormularioPersona(
            viewModel = viewModel,
            alIrAVisualizacion = { pantallaActual = "visualizacion" }
        )
        "visualizacion" -> PantallaVisualizacionPersonas(
            viewModel = viewModel,
            alVolverFormulario = { pantallaActual = "formulario" }
        )
    }
}