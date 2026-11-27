
package com.example.nfctitan

import org.json.JSONArray
import org.json.JSONObject

class ExportManager {

    fun exportScans(scans: List<NfcScanResult>): String {
        val scanArray = JSONArray()

        scans.forEach { scan ->
            scanArray.put(
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
            put("app", "NFC TITAN V2")
            put("format", "NFC_TITAN_EXPORT")
            put("version", 1)
            put("totalScans", scans.size)
            put("scans", scanArray)
        }.toString(2)
    }

    fun importScans(json: String): List<NfcScanResult>? {
        return try {
            val root = JSONObject(json)

            require(root.optString("format") == "NFC_TITAN_EXPORT")
            require(root.optInt("version") == 1)

            val array = root.getJSONArray("scans")
            require(array.length() <= 500)

            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                val technologies = item.getJSONArray("technologies")
                val records = item.getJSONArray("records")

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
            }
        } catch (_: Exception) {
            null
        }
    }
}
