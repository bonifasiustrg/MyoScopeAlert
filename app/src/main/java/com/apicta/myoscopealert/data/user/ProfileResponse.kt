package com.apicta.myoscopealert.data.user


import com.google.gson.annotations.SerializedName

data class ProfileResponse(
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
        @SerializedName("profile")
        val profile: Profile?,
        @SerializedName("user")
        val user: User?
    ) {
        data class Profile(
            @SerializedName("address")
            val address: String?,
            @SerializedName("age")
            val age: String?,
            @SerializedName("condition")
            val condition: String?,
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("device_id")
            val deviceId: String?,
            @SerializedName("emergency_phone")
            val emergencyPhone: String?,
            @SerializedName("fullname")
            val fullname: String?,
            @SerializedName("gender")
            val gender: String?,
            @SerializedName("id")
            val id: String?,
            @SerializedName("phone")
            val phone: String?,
            @SerializedName("relations")
            val relations: List<Relation?>?,
            @SerializedName("updated_at")
            val updatedAt: String?,
            @SerializedName("user_id")
            val userId: String?
        ) {
            data class Relation(
                @SerializedName("address")
                val address: String?,
                @SerializedName("age")
                val age: String?,
                @SerializedName("created_at")
                val createdAt: String?,
                @SerializedName("emergency_phone")
                val emergencyPhone: String?,
                @SerializedName("fullname")
                val fullname: String?,
                @SerializedName("gender")
                val gender: String?,
                @SerializedName("id")
                val id: String?,
                @SerializedName("phone")
                val phone: String?,
                @SerializedName("updated_at")
                val updatedAt: String?,
                @SerializedName("user_id")
                val userId: String?
            )
        }

        data class User(
            @SerializedName("created_at")
            val createdAt: String?,
            @SerializedName("email")
            val email: String?,
            @SerializedName("email_verified_at")
            val emailVerifiedAt: Any?,
            @SerializedName("id")
            val id: String?,
            @SerializedName("role")
            val role: String?,
            @SerializedName("updated_at")
            val updatedAt: String?
        )
    }
}