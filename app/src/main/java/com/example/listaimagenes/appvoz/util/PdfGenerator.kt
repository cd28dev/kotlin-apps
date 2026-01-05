package com.example.listaimagenes.appvoz.util

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfGenerator(private val context: Context) {

    fun saveTranscriptAsPdf(text: String) {
        if (text.isBlank()) {
            Toast.makeText(context, "No hay transcripción para guardar", Toast.LENGTH_SHORT).show()
            return
        }

        val docsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (docsDir == null) {
            Toast.makeText(context, "No se puede acceder al directorio de documentos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!docsDir.exists()) docsDir.mkdirs()

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "transcripcion_$timestamp.pdf"
        val file = File(docsDir, filename)

        val pageWidth = 595
        val pageHeight = 842
        val document = PdfDocument()
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 12f * context.resources.displayMetrics.density
        }

        val x = 20f
        val yStart = 40f
        var y = yStart
        val maxWidth = pageWidth - 40
        val words = text.split(Regex("\\s+"))
        var line = StringBuilder()

        var pageNumber = 1
        var page: PdfDocument.Page = createPage(document, pageWidth, pageHeight, pageNumber)
        var canvas = page.canvas

        fun finishCurrentPageWithFooter(p: PdfDocument.Page) {
            // Dibujar footer en la página actual antes de finalizarla
            val footerPaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 10f * context.resources.displayMetrics.density
            }
            val footerX = 20f
            var footerY = (pageHeight - 50).toFloat()
            canvas.drawText("UNIVERSIDAD NACIONAL DE PIURA", footerX, footerY, footerPaint)
            footerY += footerPaint.textSize + 4f
            canvas.drawText("2025-ALBURQUEQUE ANTON SEGIO", footerX, footerY, footerPaint)
            footerY += footerPaint.textSize + 4f
            canvas.drawText("ARANDA ZAPATA MADELEY", footerX, footerY, footerPaint)
            footerY += footerPaint.textSize + 4f
            canvas.drawText("MORAN PALACIOS NICK", footerX, footerY, footerPaint)

            document.finishPage(p)
        }

        for (w in words) {
            val testLine = if (line.isEmpty()) w else line.toString() + " " + w
            val textWidth = paint.measureText(testLine)
            if (textWidth > maxWidth) {
                canvas.drawText(line.toString(), x, y, paint)
                y += paint.textSize + 6f
                line = StringBuilder(w)
            } else {
                if (line.isEmpty()) line.append(w) else line.append(" ").append(w)
            }

            // si se acerca al final de la página, finalizar y crear nueva página
            if (y > pageHeight - 120) { // dejar espacio para footer
                if (line.isNotEmpty()) {
                    canvas.drawText(line.toString(), x, y, paint)
                    line = StringBuilder()
                }
                finishCurrentPageWithFooter(page)
                pageNumber += 1
                page = createPage(document, pageWidth, pageHeight, pageNumber)
                canvas = page.canvas
                y = yStart
            }
        }

        // dibujar la última línea y finalizar la página
        if (line.isNotEmpty()) {
            canvas.drawText(line.toString(), x, y, paint)
        }
        finishCurrentPageWithFooter(page)

        try {
            FileOutputStream(file).use { out ->
                document.writeTo(out)
            }
            document.close()

            // Preparar compartir con FileProvider
            val authority = "${context.packageName}.fileprovider"
            val uri: Uri = FileProvider.getUriForFile(context, authority, file)
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) 
            }
            
            val chooserIntent = Intent.createChooser(shareIntent, "Compartir PDF").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            
            context.startActivity(chooserIntent)

            Toast.makeText(context, "PDF guardado en Documentos.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            val errorMsg = when (e) {
                 is IllegalArgumentException -> "Error interno: Configuración de Provider incorrecta."
                 is android.content.ActivityNotFoundException -> "No tienes una app para ver PDFs instalada."
                 else -> "Error al exportar: ${e.message}"
            }
            Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
        }
    }

    private fun createPage(document: PdfDocument, width: Int, height: Int, pageNumber: Int): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()
        return document.startPage(pageInfo)
    }
}
