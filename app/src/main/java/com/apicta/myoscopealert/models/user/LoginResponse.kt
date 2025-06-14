package com.apicta.myoscopealert.models.user

data class LoginResponse(
    val `data`: Data? = Data(),
    val role: String? = "",
    val token: String? = ""
) {
    data class Data(
        val created_at: String? = "",
        val email: String? = "",
        val gender: String? = "",
        val id: Int? = 0,
        val name: String? = "",
        val phone: String? = "",
        val updated_at: String? = ""
    )
}