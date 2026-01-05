package com.example.listaimagenes.appvoz.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppVozScreen(
    viewModel: AppVozViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.startListening()
            }
        }
    )

    val voiceIntentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(android.speech.RecognizerIntent.EXTRA_RESULTS)
            viewModel.onIntentResult(matches)
        }
    }

    val context = LocalContext.current
    
    LaunchedEffect(uiState.pdfFileToShare) {
        uiState.pdfFileToShare?.let { file ->
            try {
                val authority = "${context.packageName}.fileprovider"
                val uri = androidx.core.content.FileProvider.getUriForFile(context, authority, file)
                val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(android.content.Intent.EXTRA_STREAM, uri)
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(android.content.Intent.createChooser(shareIntent, "Compartir PDF"))
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
            viewModel.consumePdfShare()
        }
    }

    LaunchedEffect(uiState.requestVoiceIntent) {
        if (uiState.requestVoiceIntent) {
            val intent = android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            }
            try {
                voiceIntentLauncher.launch(intent)
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Error de voz", android.widget.Toast.LENGTH_SHORT).show()
            }
            viewModel.consumeVoiceIntent()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "VoiceNote",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    if (uiState.transcript.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearAll() }) {
                            Icon(Icons.Default.Delete, "Limpiar", tint = Color(0xFFE57373))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF5F7FA),
                            Color(0xFFE8EAF6),
                            Color(0xFFC5CAE9)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // SECCIÓN 1: Zona de Grabación
                Spacer(modifier = Modifier.height(20.dp))
                
                RecordingSection(
                    isListening = uiState.isListening,
                    onMicClick = {
                        if (uiState.isListening) {
                            viewModel.stopListening()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Status con badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusChip(uiState.statusMessage)
                    
                    if (uiState.transcript.isNotEmpty()) {
                        WordCountChip(uiState.transcript.split(" ").size)
                    }
                    
                    if (uiState.detectedLanguage.isNotEmpty()) {
                        LanguageChip(uiState.detectedLanguage)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // SECCIÓN 2: Zona de Texto (estilo bloc de notas)
                TextNoteSection(
                    transcript = uiState.transcript,
                    onTextChange = { viewModel.updateTranscript(it) }
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Traducción (si existe)
                AnimatedVisibility(
                    visible = uiState.translatedText.isNotEmpty(),
                    enter = slideInVertically() + fadeIn()
                ) {
                    TranslationBubble(uiState.translatedText)
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // SECCIÓN 3: Acciones horizontales con scroll
                ActionsRow(
                    onDetect = { viewModel.identifyLanguage() },
                    onTranslate = { viewModel.translateToSpanish() },
                    onSavePdf = { viewModel.savePdf() },
                    hasText = uiState.transcript.isNotEmpty()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun RecordingSection(
    isListening: Boolean,
    onMicClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(
                    if (isListening) {
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFFEF5350),
                                Color(0xFFE53935),
                                Color(0xFFC62828)
                            )
                        )
                    } else {
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF5C6BC0),
                                Color(0xFF3F51B5),
                                Color(0xFF303F9F)
                            )
                        )
                    }
                )
                .border(
                    width = if (isListening) 4.dp else 0.dp,
                    color = Color.White.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .clickable(onClick = onMicClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isListening) "Detener" else "Grabar",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = if (isListening) "Grabando..." else "Toca para grabar",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = if (isListening) Color(0xFFE53935) else Color(0xFF303F9F)
        )
    }
}

@Composable
private fun StatusChip(status: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF7986CB).copy(alpha = 0.2f)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 13.sp,
            color = Color(0xFF303F9F),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun WordCountChip(count: Int) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF81C784).copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color(0xFF388E3C)
            )
            Text(
                text = "$count palabras",
                fontSize = 13.sp,
                color = Color(0xFF388E3C),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LanguageChip(language: String) {
    val color = if (language == "es") Color(0xFF66BB6A) else Color(0xFF42A5F5)
    
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                Icons.Default.Language,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = color
            )
            Text(
                text = language.uppercase(),
                fontSize = 13.sp,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TextNoteSection(
    transcript: String,
    onTextChange: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp, max = 300.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 8.dp
    ) {
        OutlinedTextField(
            value = transcript,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxSize(),
            placeholder = { 
                Text(
                    "Tu transcripción aparecerá aquí...\nTambién puedes escribir directamente.",
                    color = Color.Gray.copy(alpha = 0.5f),
                    fontSize = 15.sp
                ) 
            },
            textStyle = LocalTextStyle.current.copy(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = Color.Black
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            )
        )
    }
}

@Composable
private fun TranslationBubble(translation: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFE3F2FD),
                            Color(0xFFF3E5F5)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    "Traducción al Español:",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0),
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = translation,
                fontSize = 16.sp,
                color = Color(0xFF212121),
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ActionsRow(
    onDetect: () -> Unit,
    onTranslate: () -> Unit,
    onSavePdf: () -> Unit,
    hasText: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ActionChip(
            icon = Icons.Default.Language,
            label = "Detectar",
            color = Color(0xFF42A5F5),
            onClick = onDetect,
            enabled = hasText,
            modifier = Modifier.weight(1f)
        )
        
        ActionChip(
            icon = Icons.Default.Translate,
            label = "Traducir",
            color = Color(0xFF66BB6A),
            onClick = onTranslate,
            enabled = hasText,
            modifier = Modifier.weight(1f)
        )
        
        ActionChip(
            icon = Icons.Default.PictureAsPdf,
            label = "PDF",
            color = Color(0xFFFF7043),
            onClick = onSavePdf,
            enabled = hasText,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(68.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) color else Color.Gray.copy(alpha = 0.3f),
            contentColor = Color.White,
            disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
            disabledContentColor = Color.Gray
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (enabled) 6.dp else 0.dp,
            pressedElevation = 2.dp
        ),
        contentPadding = PaddingValues(vertical = 10.dp, horizontal = 8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}

