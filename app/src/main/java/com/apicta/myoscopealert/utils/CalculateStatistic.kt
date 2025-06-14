package com.apicta.myoscopealert.utils

import com.apicta.myoscopealert.models.diagnose.DiagnoseHistoryResponse
import com.apicta.myoscopealert.models.diagnose.StatisticData

fun calculateStatisctic(response: DiagnoseHistoryResponse, total:Int): StatisticData {
    // Menggunakan count untuk menghitung berapa banyak id yang bernilai 1
    val verified = response.data?.count { it?.verified == "yes" } ?: 0
    val unverified = total-verified
    val normal = response.data?.count { it?.condition?.lowercase() == "normal" } ?: 0
    val mi = total - normal
    return  StatisticData(verified, unverified, normal, mi)
}
