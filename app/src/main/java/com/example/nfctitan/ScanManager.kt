
package com.example.nfctitan

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import java.nio.charset.Charset

data class NfcScanResult(
    val tagId: String,
    val technologies: List<String>,
    val isNdef: Boolean,
    val records: List<String>,
    val message: String
)

class ScanManager {

    fun analyzeTag(tag: Tag): NfcScanResult {
        val technologies = tag.techList.map { tech ->
            tech.substringAfterLast(".")
        }.distinct()

        val ndef = Ndef.get(tag)

        val records = try {
            ndef?.cachedNdefMessage
                ?.records
                ?.map { decodeRecord(it) }
                ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        val supportedTechnologies = buildList {
            if (NfcA.get(tag) != null) add("NFC-A")
            if (NfcB.get(tag) != null) add("NFC-B")
            if (NfcF.get(tag) != null) add("NFC-F")
            if (NfcV.get(tag) != null) add("NFC-V")
        }

        return NfcScanResult(
            tagId = tag.id.joinToString("") {
                "%02X".format(it)
            },
            technologies = supportedTechnologies,
            isNdef = ndef != null,
            records = records,
            message = if (ndef != null) {
                "NDEF tag detected"
            } else {
                "No readable NDEF data available"
            }
        )
    }

    private fun decodeRecord(record: NdefRecord): String {
        return try {
            when (record.tnf) {
                NdefRecord.TNF_WELL_KNOWN -> {
                    if (record.type.contentEquals(
                            NdefRecord.RTD_TEXT
                        )
                    ) {
                        decodeText(record.payload)
                    } else {
                        "Well-known record: ${
                            record.type.toString(Charset.forName("UTF-8"))
                        }"
                    }
                }

                NdefRecord.TNF_MIME_MEDIA -> {
                    "MIME: ${
                        record.type.toString(Charset.forName("UTF-8"))
                    }"
                }

                NdefRecord.TNF_ABSOLUTE_URI -> {
                    "URI record"
                }

                else -> "NDEF record"
            }
        } catch (_: Exception) {
            "Unreadable NDEF record"
        }
    }

    private fun decodeText(payload: ByteArray): String {
        if (payload.isEmpty()) return ""

        val status = payload[0].toInt() and 0xFF
        val languageLength = status and 0x3F

        if (payload.size <= languageLength + 1) return ""

        val encoding = if ((status and 0x80) != 0) {
            Charsets.UTF_16
        } else {
            Charsets.UTF_8
        }

        return String(
            payload,
            languageLength + 1,
            payload.size - languageLength - 1,
            encoding
        )
    }
}
