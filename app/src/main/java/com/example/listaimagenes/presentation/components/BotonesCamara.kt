package com.example.listaimagenes.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.sp

@Composable
fun BotonesCamara(
    alCancelar: () -> Unit,
    alTomarFoto: () -> Unit,
    alCambiarCamara: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón Cancelar - Rojo
            Box(
                modifier = Modifier
                    .height(56.dp)
                    .width(56.dp) // Botón cuadrado solo con ícono
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFF5757), // Rojo vibrante
                                Color(0xFFFF7B7B)  // Rojo claro
                            )
                        )
                    )
                    .clickable { alCancelar() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cancelar",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

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

            // Botón Tomar Foto - Verde (más grande, acción principal)
            Box(
                modifier = Modifier
                    .height(56.dp)
                    .width(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF56CA00), // Verde vibrante
                                Color(0xFF6BCF7F)  // Verde claro
                            )
                        )
                    )
                    .clickable { alTomarFoto() },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.Face,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        "Tomar Foto",
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

            // Botón Cambiar Cámara - Azul
            Box(
                modifier = Modifier
                    .height(56.dp)
                    .width(56.dp) // Botón cuadrado solo con ícono
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4A90E2), // Azul principal
                                Color(0xFF5BA3F5)  // Azul claro
                            )
                        )
                    )
                    .clickable { alCambiarCamara() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Cambiar cámara",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

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
    }
}