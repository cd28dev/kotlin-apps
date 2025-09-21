package com.example.listaimagenes.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.listaimagenes.R
import com.example.listaimagenes.presentation.theme.AppTypography
import com.example.listaimagenes.presentation.theme.ColoresApp
import com.example.listaimagenes.presentation.theme.Tamaños
import com.example.listaimagenes.presentation.ui.components.BarraSuperior
import com.example.listaimagenes.presentation.ui.components.MenuFacultadesDisponibles
import com.example.listaimagenes.presentation.ui.components.PiePagina
import com.example.listaimagenes.presentation.viewmodel.ViewModelFormulario
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun PantallaFormulario(
    alIrAVisualizacion: () -> Unit
) {
    val viewModel: ViewModelFormulario = viewModel()
    val estado by viewModel.estado.collectAsState()
    var mostrarCamara by remember { mutableStateOf(false) }

    LaunchedEffect(estado.mensajeExito) {
        if (estado.mensajeExito.isNotEmpty()) {
            delay(1500)
            viewModel.limpiarMensajes()
            alIrAVisualizacion()
        }
    }

    if (mostrarCamara) {
        PantallaCamara(
            alVolverConFoto = { rutaFoto ->
                viewModel.establecerFotoPersonalizada(rutaFoto)
                mostrarCamara = false
            },
            alVolver = { mostrarCamara = false }
        )
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            BarraSuperior(titulo = "Agregar Facultad")

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(Tamaños.EspacioGrande),
                verticalArrangement = Arrangement.spacedBy(Tamaños.EspacioChico)
            ) {
                if (estado.facultadesDisponibles.isNotEmpty()) {
                    MenuFacultadesDisponibles(
                        facultadSeleccionada = estado.facultadSeleccionadaFormulario,
                        facultadesDisponibles = estado.facultadesDisponibles,
                        alSeleccionar = viewModel::seleccionarFacultadFormulario
                    )

                    OutlinedTextField(
                        value = estado.descripcion,
                        onValueChange = viewModel::actualizarDescripcion,
                        label = { Text("Descripción") },
                        placeholder = { Text("Ingresa la descripción de la facultad...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 5,
                        singleLine = false
                    )

                    OutlinedTextField(
                        value = estado.año,
                        onValueChange = viewModel::actualizarAño,
                        label = { Text("Año de creación") },
                        placeholder = { Text("Ej: 1985") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    // 🆕 Sección de foto personalizada
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(Tamaños.EspacioGrande),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Foto Personalizada (Opcional)",
                                style = AppTypography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(Tamaños.EspacioChico))

                            if (estado.fotoPersonalizada != null) {
                                Box {
                                    AsyncImage(
                                        model = File(estado.fotoPersonalizada),
                                        contentDescription = "Foto personalizada",
                                        modifier = Modifier
                                            .size(150.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    
                                    IconButton(
                                        onClick = viewModel::limpiarFotoPersonalizada,
                                        modifier = Modifier.align(Alignment.TopEnd)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar foto",
                                            tint = ColoresApp.Error
                                        )
                                    }
                                }
                            } else {
                                OutlinedButton(
                                    onClick = { mostrarCamara = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // 🔧 USAR TU ICONO PERSONALIZADO
                                    Image(
                                        painter = painterResource(id = R.drawable.camera_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                                        contentDescription = "Tomar foto",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(Tamaños.EspacioChico))
                                    Text("Tomar Foto")
                                }
                            }
                        }
                    }

                    Button(
                        onClick = viewModel::enviarFormulario,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Agregar Facultad",
                            style = AppTypography.labelLarge
                        )
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "No hay facultades disponibles para agregar.\nVe a visualización para eliminar algunas.",
                            modifier = Modifier.padding(Tamaños.EspacioGrande),
                            textAlign = TextAlign.Center,
                            style = AppTypography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = alIrAVisualizacion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ir a Visualización", style = AppTypography.labelLarge)
                }

                // Mensajes de error y éxito
                if (estado.mensajeError.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = estado.mensajeError,
                            modifier = Modifier.padding(Tamaños.EspacioChico),
                            style = AppTypography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                if (estado.mensajeExito.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = ColoresApp.Confirmacion.copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = estado.mensajeExito,
                            modifier = Modifier.padding(Tamaños.EspacioChico),
                            style = AppTypography.bodyMedium,
                            color = ColoresApp.Confirmacion
                        )
                    }
                }
            }

            PiePagina()
        }
    }
}