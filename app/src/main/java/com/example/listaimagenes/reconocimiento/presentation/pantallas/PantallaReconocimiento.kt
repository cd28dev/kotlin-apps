package com.example.listaimagenes.reconocimiento.presentation.pantallas

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.listaimagenes.reconocimiento.presentation.components.BarraSuperior
import com.example.listaimagenes.reconocimiento.presentation.components.PiePagina
import com.example.listaimagenes.reconocimiento.presentation.viewmodel.PersonaViewModel
import com.example.listaimagenes.reconocimiento.presentation.viewmodel.ReconocimientoViewModel
import com.example.listaimagenes.reconocimiento.presentation.pantallas.PantallaCamara
import com.example.listaimagenes.presentation.theme.ColoresApp
import com.example.listaimagenes.reconocimiento.presentation.theme.Tamaños
import java.io.File

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PantallaReconocimiento(
    reconocimientoViewModel: ReconocimientoViewModel = viewModel()
) {
    val contexto = LocalContext.current
    val estado by reconocimientoViewModel.estado.collectAsState()

    Scaffold(
        topBar = {
            BarraSuperior(
                titulo = "Reconocimiento Facial",
                activity = contexto as Activity
            )
        },
        bottomBar = { PiePagina() }
    ) { paddingValues ->
        
        if (estado.mostrarCamara) {
            PantallaCamara(
                vistaModelo = viewModel(),
                alVolver = {
                    reconocimientoViewModel.ocultarCamara()
                },
                onFotoCapturada = { fotoPath ->
                    reconocimientoViewModel.establecerFotoTomada(fotoPath)
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = ColoresApp.SuperficieClaro
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Reconocimiento",
                            modifier = Modifier.size(48.dp),
                            tint = ColoresApp.Primario
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Reconocimiento Facial",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = ColoresApp.TextoPrincipal,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Capture una foto para identificar a la persona registrada",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = ColoresApp.TextoSecundario
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Button(
                    onClick = { reconocimientoViewModel.mostrarCamara() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColoresApp.Primario
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Cámara",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Capturar Foto para Reconocer",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                estado.fotoTomada?.let { fotoPath ->
                    ResultadoReconocimiento(
                        fotoPath = fotoPath,
                        estado = estado,
                        onReintentarReconocimiento = {
                            reconocimientoViewModel.reiniciarReconocimiento()
                        }
                    )
                }

                if (estado.procesandoReconocimiento) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = ColoresApp.SuperficieClaro
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = ColoresApp.Primario
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Procesando reconocimiento facial...",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = ColoresApp.TextoSecundario
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                if (estado.fotoTomada != null) {
                    Button(
                        onClick = { reconocimientoViewModel.reiniciarReconocimiento() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColoresApp.Secundario
                        )
                    ) {
                        Text("Nuevo Reconocimiento")
                    }
                }
            }
        }
    }
}

@Composable
fun ResultadoReconocimiento(
    fotoPath: String,
    estado: com.example.listaimagenes.reconocimiento.presentation.viewmodel.EstadoReconocimiento,
    onReintentarReconocimiento: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ColoresApp.SuperficieClaro
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = File(fotoPath),
                contentDescription = "Foto capturada",
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when {
                estado.personaReconocida != null -> {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Persona encontrada",
                        modifier = Modifier.size(32.dp),
                        tint = ColoresApp.Confirmacion
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "¡Persona Identificada!",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ColoresApp.Confirmacion,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${estado.personaReconocida.nombre} ${estado.personaReconocida.apellido}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = ColoresApp.TextoPrincipal,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "DNI: ${estado.personaReconocida.dni}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ColoresApp.TextoSecundario
                        )
                    )
                    Text(
                        text = "Correo: ${estado.personaReconocida.correo}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ColoresApp.TextoSecundario
                        )
                    )
                    estado.similitudReconocimiento?.let { similitud ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Similitud: ${String.format("%.1f", similitud * 100)}%",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = ColoresApp.TextoSecundario
                            )
                        )
                    }
                }
                estado.mensajeError != null -> {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "No encontrado",
                        modifier = Modifier.size(32.dp),
                        tint = ColoresApp.Error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Persona No Identificada",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = ColoresApp.Error,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = estado.mensajeError,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ColoresApp.TextoSecundario
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}