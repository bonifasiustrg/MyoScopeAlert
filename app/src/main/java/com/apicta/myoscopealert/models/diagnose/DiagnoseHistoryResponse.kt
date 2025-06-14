package com.apicta.myoscopealert.models.diagnose

data class DiagnoseHistoryResponse(
    val `data`: List<Data?>? = null,
    val message: String? = null,
    val status: Int? = null,
    val success: Boolean? = null
) {
    data class Data(
        val condition: String? = null,
        val created_at: String? = null,
        val heartwave: String? = null,
        val id: Int? = null,
        val notes: String? = null,
        val patient_id: String? = null,
        val updated_at: String? = null,
        val verified: String? = null
    )
}