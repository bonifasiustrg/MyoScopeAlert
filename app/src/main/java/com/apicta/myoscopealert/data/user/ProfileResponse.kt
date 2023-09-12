package com.apicta.myoscopealert.data.user

data class ProfileResponse(
    val `data`: Data?,
    val error: Any?,
    val message: String?,
    val status: String?
) {
    data class Data(
        val profile: Any?,
        val user: User?
    ) {
        data class User(
            val created_at: String?,
            val email: String?,
            val email_verified_at: Any?,
            val id: String?,
            val role: String?,
            val updated_at: String?
        )
    }
}