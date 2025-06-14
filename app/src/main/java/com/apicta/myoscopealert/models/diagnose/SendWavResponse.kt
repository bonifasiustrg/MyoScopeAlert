package com.apicta.myoscopealert.models.diagnose

data class SendWavResponse(
    val data: Data? = null,
    val message: String? = null,
    val status: Int? = null,
    val success: Boolean? = null
) {
    data class Data(
        val condition: String? = null,
        val created_at: String? = null,
        val heartwave: String? = null,
        val id: Int? = null,
        val patient_id: Int? = null,
        val updated_at: String? = null
    )
}