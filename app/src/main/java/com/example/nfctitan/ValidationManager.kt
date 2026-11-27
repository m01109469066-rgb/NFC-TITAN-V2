
package com.example.nfctitan

import org.json.JSONObject

data class ValidationResult(
    val isValid: Boolean,
    val message: String
)

class ValidationManager {

    fun validateBackup(json: String): ValidationResult {
        return try {
            if (json.isBlank()) {
                return ValidationResult(
                    false,
                    "Backup file is empty"
                )
            }

            val root = JSONObject(json)

            if (root.optString("format") != "NFC_TITAN_BACKUP") {
                return ValidationResult(
                    false,
                    "Unknown backup format"
                )
            }

            if (root.optInt("version") != 1) {
                return ValidationResult(
                    false,
                    "Unsupported backup version"
                )
            }

            val scans = root.optJSONArray("scans")
                ?: return ValidationResult(
                    false,
                    "Scan data is missing"
                )

            if (scans.length() > 500) {
                return ValidationResult(
                    false,
                    "Backup contains too many scans"
                )
            }

            for (index in 0 until scans.length()) {
                val scan = scans.optJSONObject(index)
                    ?: return ValidationResult(
                        false,
                        "Invalid scan at position ${index + 1}"
                    )

                if (!scan.has("tagId") ||
                    !scan.has("technologies") ||
                    !scan.has("isNdef") ||
                    !scan.has("records") ||
                    !scan.has("message")
                ) {
                    return ValidationResult(
                        false,
                        "Incomplete scan data at position ${index + 1}"
                    )
                }

                if (scan.optJSONArray("technologies") == null ||
                    scan.optJSONArray("records") == null
                ) {
                    return ValidationResult(
                        false,
                        "Invalid scan lists at position ${index + 1}"
                    )
                }
            }

            ValidationResult(
                true,
                "Backup data is valid"
            )

        } catch (_: Exception) {
            ValidationResult(
                false,
                "Invalid or corrupted backup file"
            )
        }
    }
}
