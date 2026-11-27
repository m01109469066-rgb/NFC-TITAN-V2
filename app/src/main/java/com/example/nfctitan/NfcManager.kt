
package com.example.nfctitan

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.provider.Settings

data class NfcStatus(
    val supported: Boolean,
    val enabled: Boolean
)

class NfcManager(context: Context) {

    private val adapter: NfcAdapter? =
        NfcAdapter.getDefaultAdapter(context)

    fun getStatus(): NfcStatus {
        return NfcStatus(
            supported = adapter != null,
            enabled = adapter?.isEnabled == true
        )
    }

    fun openNfcSettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_NFC_SETTINGS)
        )
    }

    fun isAvailable(): Boolean {
        return adapter != null
    }

    fun isEnabled(): Boolean {
        return adapter?.isEnabled == true
    }
}
