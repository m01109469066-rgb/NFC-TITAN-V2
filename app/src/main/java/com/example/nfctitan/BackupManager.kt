
package com.example.nfctitan

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class BackupManager(context: Context) {

    private val database = DatabaseManager(context)

    fun createBackup(): String {
        val scans = database.getHistory()
        val array = JSONArray()

        scans.forEach { scan ->
            array.put(
                JSONObject().apply {
                    put("tagId", scan.tagId)
                    put("technologies", JSONArray(scan.technologies))
                    put("isNdef", scan.isNdef)
                    put("records", JSONArray(scan.records))
                    put("message", scan.message)
                }
            )
        }

        return JSONObject().apply {
            put("format", "NFC_TITAN_BACKUP")
            put("version", 1)
            put("scans", array)
        }.toString(2)
    }

    fun restoreBackup(json: String): Boolean {
        return try {
            val root = JSONObject(json)

            require(root.optString("format") == "NFC_TITAN_BACKUP")
            require(root.optInt("version") == 1)

            val array = root.getJSONArray("scans")
            require(array.length() <= 500)

            val restored = mutableListOf<NfcScanResult>()

            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)

                val technologies = item.getJSONArray("technologies")
                val records = item.getJSONArray("records")

                restored.add(
                    NfcScanResult(
                        tagId = item.getString("tagId"),
                        technologies = List(technologies.length()) {
                            technologies.getString(it)
                        },
                        isNdef = item.getBoolean("isNdef"),
                        records = List(records.length()) {
                            records.getString(it)
                        },
                        message = item.getString("message")
                    )
                )
            }

            database.clearHistory()
            restored.asReversed().forEach(database::saveScan)

            true
        } catch (_: Exception) {
            false
        }
    }
}
