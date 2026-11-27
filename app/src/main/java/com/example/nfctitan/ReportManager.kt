
package com.example.nfctitan

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri

class ReportManager {

    fun createPdf(
        context: Context,
        destination: Uri,
        scans: List<NfcScanResult>
    ): Boolean {
        val document = PdfDocument()

        return try {
            val pageWidth = 595
            val pageHeight = 842
            val margin = 40f
            val lineHeight = 19f

            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(190, 145, 35)
                textSize = 22f
                typeface = Typeface.create(
                    Typeface.DEFAULT,
                    Typeface.BOLD
                )
            }

            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.BLACK
                textSize = 11f
            }

            val lines = mutableListOf<String>()

            lines.add("NFC TITAN V2")
            lines.add("NFC Scan Report")
            lines.add("Total scans: ${scans.size}")
            lines.add("")

            scans.forEachIndexed { index, scan ->
                lines.add("Scan ${index + 1}")
                lines.add("Tag ID: ${scan.tagId}")
                lines.add("NDEF available: ${scan.isNdef}")
                lines.add("Technologies: ${scan.technologies.joinToString(", ")}")
                lines.add("Status: ${scan.message}")

                scan.records.forEach { record ->
                    lines.add("Record: $record")
                }

                lines.add("--------------------------------")
                lines.add("")
            }

            val wrappedLines = lines.flatMap { line ->
                wrapText(line, textPaint, pageWidth - 2 * margin)
            }

            val linesPerPage = (
                (pageHeight - 100) / lineHeight
            ).toInt()

            val pages = wrappedLines.chunked(linesPerPage)

            pages.forEachIndexed { pageIndex, pageLines ->
                val pageInfo = PdfDocument.PageInfo.Builder(
                    pageWidth,
                    pageHeight,
                    pageIndex + 1
                ).create()

                val page = document.startPage(pageInfo)
                val canvas: Canvas = page.canvas

                if (pageIndex == 0) {
                    canvas.drawText(
                        "NFC TITAN V2",
                        margin,
                        45f,
                        titlePaint
                    )
                } else {
                    canvas.drawText(
                        "NFC TITAN V2 - Report",
                        margin,
                        45f,
                        titlePaint
                    )
                }

                var y = 80f

                pageLines.forEach { line ->
                    canvas.drawText(
                        line,
                        margin,
                        y,
                        textPaint
                    )
                    y += lineHeight
                }

                document.finishPage(page)
            }

            val output = context.contentResolver
                .openOutputStream(destination)
                ?: return false

            output.use {
                document.writeTo(it)
            }

            true
        } catch (_: Exception) {
            false
        } finally {
            document.close()
        }
    }

    private fun wrapText(
        text: String,
        paint: Paint,
        maxWidth: Float
    ): List<String> {
        if (text.isEmpty()) return listOf("")

        val words = text.split(" ")
        val result = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val candidate = if (currentLine.isEmpty()) {
                word
            } else {
                "$currentLine $word"
            }

            if (paint.measureText(candidate) <= maxWidth) {
                currentLine = candidate
            } else {
                if (currentLine.isNotEmpty()) {
                    result.add(currentLine)
                }

                currentLine = word
            }
        }

        if (currentLine.isNotEmpty()) {
            result.add(currentLine)
        }

        return result
    }
}
