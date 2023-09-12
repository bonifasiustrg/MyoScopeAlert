package com.apicta.myoscopealert.data.user


import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("data")
    val `data`: Data?,
    @SerializedName("error")
    val error: Any?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
) {
    data class Data(
        @SerializedName("access_token")
        val accessToken: String?,
        @SerializedName("expired")
        val expired: Int?,
        @SerializedName("user")
        val user: User?
    ) {
        data class User(
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("email")
            val email: String?,
            @SerializedName("email_verified_at")
            val emailVerifiedAt: Any?,
            @SerializedName("id")
            val id: String?,
            @SerializedName("updated_at")
            val updatedAt: String?
        )
    }
}