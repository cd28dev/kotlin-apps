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
import com.example.listaimagenes.presentation.pantallas.PantallaReconocimiento
import com.example.listaimagenes.presentation.viewmodel.PersonaViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.listaimagenes.presentation.theme.ColoresApp

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
    var pantallaActual by remember { mutableStateOf("registro") }
    val viewModel: PersonaViewModel = viewModel()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                pantallaActual = pantallaActual,
                onNavegacionCambiada = { nuevaPantalla -> pantallaActual = nuevaPantalla }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (pantallaActual) {
                "registro" -> PantallaFormularioPersona(
                    viewModel = viewModel,
                    alIrAVisualizacion = { pantallaActual = "lista" }
                )
                "lista" -> PantallaVisualizacionPersonas(
                    viewModel = viewModel,
                    alVolverFormulario = { pantallaActual = "registro" }
                )
                "reconocimiento" -> PantallaReconocimiento()
            }
        }
    }
}

@Composable
fun MenuPrincipal(
    alIrFormulario: () -> Unit,
    alIrVisualizacion: () -> Unit,
    alIrReconocimiento: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Título de la aplicación
        Text(
            text = "Sistema de Reconocimiento Facial",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = ColoresApp.Primario,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Seleccione una opción",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = ColoresApp.TextoSecundario
            ),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        
        // Botones del menú
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón Registrar Persona
            Button(
                onClick = alIrFormulario,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColoresApp.Primario
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Registrar",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Registrar Persona",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            
            // Botón Ver Personas
            Button(
                onClick = alIrVisualizacion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColoresApp.Secundario
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Ver lista",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Ver Personas Registradas",
                    style = MaterialTheme.typography.labelLarge
                )
            }
            
            // Botón Reconocimiento Facial
            Button(
                onClick = alIrReconocimiento,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColoresApp.Terciario
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Reconocer",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Reconocimiento Facial",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    pantallaActual: String,
    onNavegacionCambiada: (String) -> Unit
) {
    NavigationBar(
        containerColor = ColoresApp.SuperficieClaro,
        contentColor = ColoresApp.Primario
    ) {
        // Pestaña Registro
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Registrar"
                )
            },
            label = { Text("Registrar") },
            selected = pantallaActual == "registro",
            onClick = { onNavegacionCambiada("registro") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColoresApp.Primario,
                selectedTextColor = ColoresApp.Primario,
                indicatorColor = ColoresApp.Primario.copy(alpha = 0.1f),
                unselectedIconColor = ColoresApp.TextoSecundario,
                unselectedTextColor = ColoresApp.TextoSecundario
            )
        )
        
        // Pestaña Lista
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Lista"
                )
            },
            label = { Text("Lista") },
            selected = pantallaActual == "lista",
            onClick = { onNavegacionCambiada("lista") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColoresApp.Secundario,
                selectedTextColor = ColoresApp.Secundario,
                indicatorColor = ColoresApp.Secundario.copy(alpha = 0.1f),
                unselectedIconColor = ColoresApp.TextoSecundario,
                unselectedTextColor = ColoresApp.TextoSecundario
            )
        )
        
        // Pestaña Reconocimiento
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Reconocer"
                )
            },
            label = { Text("Reconocer") },
            selected = pantallaActual == "reconocimiento",
            onClick = { onNavegacionCambiada("reconocimiento") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColoresApp.Terciario,
                selectedTextColor = ColoresApp.Terciario,
                indicatorColor = ColoresApp.Terciario.copy(alpha = 0.1f),
                unselectedIconColor = ColoresApp.TextoSecundario,
                unselectedTextColor = ColoresApp.TextoSecundario
            )
        )
    }
}