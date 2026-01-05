package com.example.listaimagenes.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listaimagenes.presentation.theme.ColoresApp

@Composable
fun MenuPrincipalScreen(
    onNavigateToReconocimiento: () -> Unit,
    onNavigateToElTiempo: () -> Unit,
    onNavigateToAppVoz: () -> Unit
) {
    var horaActual by remember { mutableStateOf(obtenerHoraActual()) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            horaActual = obtenerHoraActual()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF5F7FA),
                        Color(0xFFE8EAF6)
                    )
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Encabezado Universidad
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "UNIVERSIDAD NACIONAL DE PIURA",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 0.5.sp
                ),
                color = ColoresApp.Primario,
                textAlign = TextAlign.Center
            )
            
            Divider(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(100.dp),
                thickness = 2.dp,
                color = ColoresApp.Primario
            )
        }

        // Contenido principal
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {
            // Título principal
            Text(
                text = "Multi-App Android",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                ),
                color = ColoresApp.Primario,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Selecciona una aplicación",
                style = MaterialTheme.typography.bodyLarge,
                color = ColoresApp.TextoSecundario,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Cards de las apps
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AppCard(
                    title = "Reconocimiento Facial",
                    description = "Sistema de identificación con ML Kit",
                    icon = Icons.Default.Face,
                    gradient = listOf(
                        Color(0xFF667EEA),
                        Color(0xFF764BA2)
                    ),
                    onClick = onNavigateToReconocimiento
                )
                
                AppCard(
                    title = "El Tiempo",
                    description = "Pronóstico del clima en tiempo real",
                    icon = Icons.Default.Info,
                    gradient = listOf(
                        Color(0xFF56CCF2),
                        Color(0xFF2F80ED)
                    ),
                    onClick = onNavigateToElTiempo
                )
                
                AppCard(
                    title = "AppVoz",
                    description = "Reconocimiento de voz y traducción",
                    icon = Icons.Default.Person,
                    gradient = listOf(
                        Color(0xFFF093FB),
                        Color(0xFFF5576C)
                    ),
                    onClick = onNavigateToAppVoz
                )
            }
        }

        // Footer con autores
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "© 2025 - Adrianzen, Huanca, Pasache",
                style = MaterialTheme.typography.labelMedium,
                color = ColoresApp.TextoSecundario,
                textAlign = TextAlign.Center
            )
            Text(
                text = horaActual,
                style = MaterialTheme.typography.labelSmall,
                color = ColoresApp.TextoSecundario.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun AppCard(
    title: String,
    description: String,
    icon: ImageVector,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .scale(scale)
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(gradient)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(48.dp),
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
    
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

private fun obtenerHoraActual(): String {
    val formato = java.text.SimpleDateFormat("hh:mm:ss a", java.util.Locale("es", "PE"))
    return formato.format(java.util.Date())
}
