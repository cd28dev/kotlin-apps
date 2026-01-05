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
import com.example.listaimagenes.reconocimiento.domain.usecase.PersonaManager
import com.example.listaimagenes.presentation.theme.TemaApp
import com.example.listaimagenes.presentation.MenuPrincipalScreen
import com.example.listaimagenes.reconocimiento.presentation.pantallas.PantallaFormularioPersona
import com.example.listaimagenes.reconocimiento.presentation.pantallas.PantallaVisualizacionPersonas
import com.example.listaimagenes.reconocimiento.presentation.pantallas.PantallaReconocimiento
import com.example.listaimagenes.reconocimiento.presentation.viewmodel.PersonaViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ArrowBack
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
    var pantallaActual by remember { mutableStateOf("menu") }
    val viewModel: PersonaViewModel = viewModel()
    
    // Manejar el botón de atrás del dispositivo
    androidx.activity.compose.BackHandler(enabled = pantallaActual != "menu") {
        pantallaActual = "menu"
    }

    Scaffold(
        bottomBar = {
            // Solo mostrar barra de navegación en las pantallas de reconocimiento facial
            if (pantallaActual.startsWith("reconocimiento_")) {
                BottomNavigationBar(
                    pantallaActual = pantallaActual,
                    onNavegacionCambiada = { nuevaPantalla -> pantallaActual = nuevaPantalla },
                    onVolverMenu = { pantallaActual = "menu" }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (pantallaActual) {
                "menu" -> MenuPrincipalScreen(
                    onNavigateToReconocimiento = { pantallaActual = "reconocimiento_registro" },
                    onNavigateToElTiempo = { pantallaActual = "eltiempo" },
                    onNavigateToAppVoz = { pantallaActual = "appvoz" }
                )
                
                // App 1: Reconocimiento Facial (app actual)
                "reconocimiento_registro" -> PantallaFormularioPersona(
                    viewModel = viewModel,
                    alIrAVisualizacion = { pantallaActual = "reconocimiento_lista" }
                )
                "reconocimiento_lista" -> PantallaVisualizacionPersonas(
                    viewModel = viewModel,
                    alVolverFormulario = { pantallaActual = "reconocimiento_registro" }
                )
                "reconocimiento_reconocer" -> PantallaReconocimiento()
                
                // App 2: El Tiempo
                "eltiempo" -> com.example.listaimagenes.eltiempo.ElTiempoScreen()
                
                
                // App 3: AppVoz
                "appvoz" -> com.example.listaimagenes.appvoz.presentation.AppVozScreen(
                    onBack = { pantallaActual = "menu" }
                )
            }
        }
    }
}

// Pantalla temporal para apps no implementadas aún
@Composable
fun PlaceholderScreen(
    appName: String,
    description: String,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Build,
            contentDescription = "En construcción",
            modifier = Modifier.size(80.dp),
            tint = ColoresApp.Primario
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = appName,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = ColoresApp.Primario
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = ColoresApp.TextoSecundario,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "🚧 En construcción 🚧",
            style = MaterialTheme.typography.titleMedium,
            color = ColoresApp.Secundario
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = ColoresApp.Primario
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Volver"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver al Menú")
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
    onNavegacionCambiada: (String) -> Unit,
    onVolverMenu: () -> Unit
) {
    NavigationBar(
        containerColor = ColoresApp.SuperficieClaro,
        contentColor = ColoresApp.Primario
    ) {
        // Pestaña Menú
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Menú"
                )
            },
            label = { Text("Menú") },
            selected = false,
            onClick = onVolverMenu,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = ColoresApp.Primario,
                selectedTextColor = ColoresApp.Primario,
                indicatorColor = ColoresApp.Primario.copy(alpha = 0.1f),
                unselectedIconColor = ColoresApp.TextoSecundario,
                unselectedTextColor = ColoresApp.TextoSecundario
            )
        )
        // Pestaña Registro
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Registrar"
                )
            },
            label = { Text("Registrar") },
            selected = pantallaActual == "reconocimiento_registro",
            onClick = { onNavegacionCambiada("reconocimiento_registro") },
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
            selected = pantallaActual == "reconocimiento_lista",
            onClick = { onNavegacionCambiada("reconocimiento_lista") },
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
            selected = pantallaActual == "reconocimiento_reconocer",
            onClick = { onNavegacionCambiada("reconocimiento_reconocer") },
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