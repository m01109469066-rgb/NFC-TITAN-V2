
package com.example.nfctitan

data class ScanComparison(
    val sameTagId: Boolean,
    val sameTechnologies: Boolean,
    val sameNdefStatus: Boolean,
    val firstOnlyRecords: List<String>,
    val secondOnlyRecords: List<String>,
    val commonRecords: List<String>
)

class ComparisonManager {

    fun compare(
        first: NfcScanResult,
        second: NfcScanResult
    ): ScanComparison {

        val firstRecords = first.records.toSet()
        val secondRecords = second.records.toSet()

        return ScanComparison(
            sameTagId = first.tagId == second.tagId,

            sameTechnologies =
                first.technologies.toSet() ==
                    second.technologies.toSet(),

            sameNdefStatus =
                first.isNdef == second.isNdef,

            firstOnlyRecords =
                (firstRecords - secondRecords).toList(),

            secondOnlyRecords =
                (secondRecords - firstRecords).toList(),

            commonRecords =
                (firstRecords intersect secondRecords).toList()
        )
    }
}
