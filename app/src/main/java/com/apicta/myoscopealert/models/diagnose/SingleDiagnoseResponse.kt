package com.apicta.myoscopealert.models.diagnose

data class SingleDiagnoseResponse(
    val detection: Detection? = null,
    val message: String? = null,
    val status: Int? = null,
    val success: Boolean? = null
) {
    data class Detection(
        val condition: String? = null,
        val created_at: String? = null,
        val heartwave: String? = null,
        val id: String? = null,
        val notes: Any? = null,
        val patient_id: String? = null,
        val updated_at: String? = null,
        val verified: Any? = null
    )
}