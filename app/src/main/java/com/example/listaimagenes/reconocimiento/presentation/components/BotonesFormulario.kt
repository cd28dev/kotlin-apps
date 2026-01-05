package com.example.listaimagenes.reconocimiento.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
@Composable
fun BotonesFormularioPersona(
    esEdicion: Boolean,
    camposLlenos: Boolean,
    procesandoRegistro: Boolean = false,
    onRegistrar: () -> Unit,
    onActualizar: () -> Unit,
    onVerPersonas: () -> Unit,
    onCancelar: () -> Unit
) {
    // 🔍 DEBUG: Log para verificar estado de botones
    android.util.Log.d("BotonesFormulario", "esEdicion=$esEdicion, camposLlenos=$camposLlenos, procesando=$procesandoRegistro")
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (esEdicion) {
                // Botón Actualizar - Colores naranjas/amarillos
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = if (camposLlenos) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFFF9A56), // Naranja suave
                                        Color(0xFFFFAD56)  // Naranja amarillento
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.Gray.copy(alpha = 0.3f),
                                        Color.Gray.copy(alpha = 0.2f)
                                    )
                                )
                            }
                        )
                        .clickable(enabled = camposLlenos) { onActualizar() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = null,
                            tint = if (camposLlenos) Color.White else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Actualizar",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (camposLlenos) Color.White else Color.Gray
                            )
                        )
                    }

                    if (camposLlenos) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.2f),
                                            Color.Transparent,
                                            Color.Transparent
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(200f, 200f)
                                    )
                                )
                        )
                    }
                }

                // Botón Cancelar - Colores rojos
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFFF5757), // Rojo vibrante
                                    Color(0xFFFF7B7B)  // Rojo claro
                                )
                            )
                        )
                        .clickable { onCancelar() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Cancelar",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.2f),
                                        Color.Transparent,
                                        Color.Transparent
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(200f, 200f)
                                )
                            )
                    )
                }
            } else {
                // Botón Registrar - Colores verdes con estado de procesando
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = if (camposLlenos && !procesandoRegistro) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF56CA00), // Verde vibrante
                                        Color(0xFF6BCF7F)  // Verde claro
                                    )
                                )
                            } else if (procesandoRegistro) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF4CAF50), // Verde más oscuro para procesando
                                        Color(0xFF66BB6A)
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.Gray.copy(alpha = 0.3f),
                                        Color.Gray.copy(alpha = 0.2f)
                                    )
                                )
                            }
                        )
                        .clickable(enabled = camposLlenos && !procesandoRegistro) { onRegistrar() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (procesandoRegistro) {
                            // Indicador de carga cuando está procesando
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = if (camposLlenos) Color.White else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            if (procesandoRegistro) "Registrando..." else "Registrar",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (camposLlenos || procesandoRegistro) Color.White else Color.Gray
                            )
                        )
                    }

                    if ((camposLlenos && !procesandoRegistro) || procesandoRegistro) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.2f),
                                            Color.Transparent,
                                            Color.Transparent
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(200f, 200f)
                                    )
                                )
                        )
                    }
                }

                // El botón "Ver Personas" se eliminó porque está en el navbar
            }
        }
    }
}