package com.example.listaimagenes.presentation.pantallas

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.listaimagenes.presentation.components.BotonesFormularioPersona
import com.example.listaimagenes.presentation.components.BarraSuperior
import com.example.listaimagenes.presentation.components.MostrarMensaje
import com.example.listaimagenes.presentation.components.PiePagina
import com.example.listaimagenes.presentation.viewmodel.PersonaViewModel
import java.io.File
import androidx.core.net.toUri

@SuppressLint("ContextCastToActivity")
@Composable
fun PantallaFormularioPersona(
    viewModel: PersonaViewModel = viewModel(),
    alIrAVisualizacion: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val activity = LocalContext.current as Activity
    val context = LocalContext.current
    
    // 🐛 DEBUG: Verificar estado de campos para diagnóstico
    val camposLlenos = estado.nombre.isNotBlank() && 
                       estado.apellido.isNotBlank() && 
                       estado.dni.isNotBlank() && 
                       estado.correo.isNotBlank() && 
                       !estado.foto.isNullOrBlank()
    
    // 🔍 DEBUG: Log para depuración (eliminar en producción)
    android.util.Log.d("PantallaFormulario", 
        "Estado: nombre=${estado.nombre.isNotBlank()}, " +
        "apellido=${estado.apellido.isNotBlank()}, " +
        "dni=${estado.dni.isNotBlank()}, " +
        "correo=${estado.correo.isNotBlank()}, " +
        "foto=${!estado.foto.isNullOrBlank()} (${estado.foto}), " +
        "esEdicion=${estado.esEdicion}, " +
        "camposLlenos=$camposLlenos"
    )

    if(estado.mostrarCamara) {
        PantallaCamara(alVolver={viewModel.mostrarCamara(false)})
        return
    }

    Column(
        modifier = Modifier.fillMaxSize().
        background(MaterialTheme.colorScheme.background)
    ) {
        BarraSuperior("Registro de Persona",activity)

        Column(
            modifier = Modifier.weight(1f).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = estado.nombre,
                    onValueChange = viewModel::actualizarNombre,
                    label = { Text("Nombre", style = MaterialTheme.typography.labelMedium) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = estado.apellido,
                    onValueChange = viewModel::actualizarApellido,
                    label = { Text("Apellido", style = MaterialTheme.typography.labelMedium) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = estado.dni,
                    onValueChange = { input ->
                        val soloNumeros = input.filter { it.isDigit() }
                        viewModel.actualizarDni(soloNumeros)
                    },
                    label = { Text("DNI", style = MaterialTheme.typography.labelMedium) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                OutlinedTextField(
                    value = estado.correo,
                    onValueChange = viewModel::actualizarCorreo,
                    label = { Text("Correo Electrónico", style = MaterialTheme.typography.labelMedium) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp)
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                )
                {
                    estado.foto?.let {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            AsyncImage(
                                model = if (it.startsWith("content://")) {
                                    it.toUri()
                                } else {
                                    File(it)
                                },
                                contentDescription = "Foto",
                                modifier = Modifier
                                    .size(160.dp)
                                    .clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        TextButton(
                            onClick = { viewModel.limpiarFoto() },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Eliminar foto",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    } ?: Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                )
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.AccountBox,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = { viewModel.mostrarCamara(true) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            elevation = ButtonDefaults.buttonElevation(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Face,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Tomar Foto",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            BotonesFormularioPersona(
                esEdicion = estado.esEdicion,
                camposLlenos = camposLlenos,
                procesandoRegistro = estado.procesandoRegistro,
                onRegistrar = {
                    viewModel.crear(context) { exito ->
                        if (exito) {
                            // No navegar automáticamente, el usuario puede usar el navbar
                        }
                    }
                },
                onActualizar = {
                    viewModel.actualizar(context) { exito ->
                        if (exito) {
                            // No navegar automáticamente, el usuario puede usar el navbar
                        }
                    }
                },
                onVerPersonas = { /* Función vacía - botón eliminado */ },
                onCancelar = { viewModel.cancelarEdicion() }
            )


            MostrarMensaje(estado.mensaje) { viewModel.limpiarMensaje() }
        }
        PiePagina()
    }
}


