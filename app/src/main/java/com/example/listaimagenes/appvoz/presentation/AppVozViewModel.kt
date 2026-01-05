package com.example.listaimagenes.appvoz.presentation

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.listaimagenes.appvoz.util.PdfGenerator
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AppVozUiState(
    val transcript: String = "",
    val isListening: Boolean = false,
    val detectedLanguage: String = "",
    val translatedText: String = "",
    val statusMessage: String = "Listo",
    val requestVoiceIntent: Boolean = false // Trigger para lanzar intent
)

class AppVozViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext
    // Eliminamos SpeechRecognizer directo para evitar "No disponible"
    // private var speechRecognizer: SpeechRecognizer? = null 
    private var translator: Translator? = null
    private val pdfGenerator = PdfGenerator(context)

    // State
    private val _uiState = MutableStateFlow(AppVozUiState())
    val uiState: StateFlow<AppVozUiState> = _uiState.asStateFlow()

    init {
        // Ya no inicializamos nada complejo de voz aquí
        _uiState.update { it.copy(statusMessage = "Listo para dictar") }
    }

    // Método simplificado: Solo dispara el Intent
    fun startListening() {
        _uiState.update { it.copy(requestVoiceIntent = true, statusMessage = "Iniciando dictado...") }
    }

    fun stopListening() {
        // No-op en modo intent
    }

    // Método para resetear el flag del intent una vez lanzado
    fun consumeVoiceIntent() {
        _uiState.update { it.copy(requestVoiceIntent = false) }
    }
    
    // Recibe el texto del Intent de Google
    fun onIntentResult(matches: List<String>?) {
        if (!matches.isNullOrEmpty()) {
            val bestMatch = matches[0]
            _uiState.update { it.copy(transcript = bestMatch, statusMessage = "Texto capturado") }
        } else {
             _uiState.update { it.copy(statusMessage = "No se escuchó nada") }
        }
    }
    
    fun updateTranscript(text: String) {
        _uiState.update { it.copy(transcript = text) }
    }

    fun identifyLanguage() {
        val text = _uiState.value.transcript
        if (text.isBlank()) return

        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                val lang = if (languageCode == "und") "Desconocido" else languageCode
                _uiState.update { it.copy(detectedLanguage = lang) }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(detectedLanguage = "Error") }
            }
    }

    fun translateToSpanish() {
        val text = _uiState.value.transcript
        val detectedLang = _uiState.value.detectedLanguage

        if (text.isBlank()) return
        if (detectedLang == "es" || detectedLang == "es-ES") {
             _uiState.update { it.copy(translatedText = text) }
             return
        }

        val sourceLang = mapLanguageCode(detectedLang) ?: TranslateLanguage.ENGLISH 

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLang)
            .setTargetLanguage(TranslateLanguage.SPANISH)
            .build()
            
        translator?.close()
        translator = Translation.getClient(options)
        
        // Permitir descarga en redes móviles si es necesario
        val conditions = DownloadConditions.Builder()
            .build() 
        
        _uiState.update { it.copy(statusMessage = "Buscando/Descargando idioma...") }
        
        translator?.downloadModelIfNeeded(conditions)
            ?.addOnSuccessListener {
                _uiState.update { it.copy(statusMessage = "Traduciendo...") }
                translator?.translate(text)
                    ?.addOnSuccessListener { translated ->
                         _uiState.update { it.copy(translatedText = translated, statusMessage = "Traducido con éxito") }
                    }
                    ?.addOnFailureListener { e ->
                         _uiState.update { it.copy(translatedText = "Error traduciendo: ${e.message}") }
                    }
            }
            ?.addOnFailureListener { e ->
                 _uiState.update { it.copy(statusMessage = "Error descarga: ${e.message}. Revisa tu internet.") }
            }
    }

    fun savePdf() {
        pdfGenerator.saveTranscriptAsPdf(_uiState.value.transcript)
    }

    private fun mapLanguageCode(code: String): String? {
        return when (code) {
            "en" -> TranslateLanguage.ENGLISH
            "es" -> TranslateLanguage.SPANISH
            "fr" -> TranslateLanguage.FRENCH
            "pt" -> TranslateLanguage.PORTUGUESE
            "it" -> TranslateLanguage.ITALIAN
            "de" -> TranslateLanguage.GERMAN
            "ru" -> TranslateLanguage.RUSSIAN
            "zh" -> TranslateLanguage.CHINESE
            else -> null
        }
    }

    override fun onCleared() {
        super.onCleared()
        // speechRecognizer?.destroy()
        translator?.close()
    }
}
