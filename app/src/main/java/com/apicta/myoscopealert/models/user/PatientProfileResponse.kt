package com.apicta.myoscopealert.models.user

data class PatientProfileResponse(
    val `data`: Data? = null,
    val message: String? = null,
    val status: Int? = null,
    val success: Boolean? = null
) {
    data class Data(
        val created_at: String? = null,
        val email: String? = null,
        val gender: String? = null,
        val id: Int? = null,
        val name: String? = null,
        val phone: String? = null,
        val updated_at: String? = null
    )
}