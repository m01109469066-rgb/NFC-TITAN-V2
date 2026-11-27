
package com.example.nfctitan

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef

class NfcReaderManager(
    private val activity: Activity,
    private val onScanCompleted: (NfcScanResult) -> Unit,
    private val onError: (String) -> Unit
) {

    private val adapter: NfcAdapter? =
        NfcAdapter.getDefaultAdapter(activity)

    private val scanManager = ScanManager()

    private val readerCallback = NfcAdapter.ReaderCallback { tag ->
        try {
            val result = readTag(tag)
            activity.runOnUiThread {
                onScanCompleted(result)
            }
        } catch (exception: Exception) {
            activity.runOnUiThread {
                onError(
                    exception.message ?: "Unable to read NFC tag"
                )
            }
        }
    }

    fun isSupported(): Boolean {
        return adapter != null
    }

    fun isEnabled(): Boolean {
        return adapter?.isEnabled == true
    }

    fun startScanning(): Boolean {
        val nfcAdapter = adapter ?: return false

        if (!nfcAdapter.isEnabled) return false

        return try {
            nfcAdapter.enableReaderMode(
                activity,
                readerCallback,
                NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V,
                null
            )
            true
        } catch (_: Exception) {
            false
        }
    }

    fun stopScanning() {
        try {
            adapter?.disableReaderMode(activity)
        } catch (_: Exception) {
            onError("Could not stop NFC scanning")
        }
    }

    private fun readTag(tag: Tag): NfcScanResult {
        val ndef = Ndef.get(tag)

        if (ndef != null) {
            try {
                ndef.connect()
                ndef.close()
            } catch (_: Exception) {
                try {
                    ndef.close()
                } catch (_: Exception) {
                    // Ignore close errors.
                }
            }
        }

        return scanManager.analyzeTag(tag)
    }
}
