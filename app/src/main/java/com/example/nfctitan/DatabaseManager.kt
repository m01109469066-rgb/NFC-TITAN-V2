
package com.example.nfctitan

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class DatabaseManager(context: Context) {

    private val preferences = context.getSharedPreferences(
        "nfc_titan_database",
        Context.MODE_PRIVATE
    )

    fun saveScan(result: NfcScanResult) {
        val history = getHistory().toMutableList()

        history.add(0, result)

        val jsonArray = JSONArray()

        history.take(500).forEach { scan ->
            val item = JSONObject().apply {
                put("tagId", scan.tagId)
                put("technologies", JSONArray(scan.technologies))
                put("isNdef", scan.isNdef)
                put("records", JSONArray(scan.records))
                put("message", scan.message)
            }

            jsonArray.put(item)
        }

        preferences.edit()
            .putString("scan_history", jsonArray.toString())
            .apply()
    }

    fun getHistory(): List<NfcScanResult> {
        val json = preferences.getString("scan_history", "[]")
            ?: "[]"

        return try {
            val array = JSONArray(json)
            val history = mutableListOf<NfcScanResult>()

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)

                history.add(
                    NfcScanResult(
                        tagId = item.optString("tagId"),
                        technologies = item
                            .optJSONArray("technologies")
                            .toStringList(),
                        isNdef = item.optBoolean("isNdef"),
                        records = item
                            .optJSONArray("records")
                            .toStringList(),
                        message = item.optString("message")
                    )
                )
            }

            history
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun clearHistory() {
        preferences.edit()
            .remove("scan_history")
            .apply()
    }

    private fun JSONArray?.toStringList(): List<String> {
        if (this == null) return emptyList()

        return List(length()) { index ->
            optString(index)
        }
    }
}
