package com.apicta.myoscopealert.data.diagnose


import com.google.gson.annotations.SerializedName

data class PatientDiagnoseResponse(
    @SerializedName("data")
    val `data`: List<Data?>?,
    @SerializedName("error")
    val error: Any?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String?,
        @SerializedName("diagnoses")
        val diagnoses: String?,
        @SerializedName("doctor")
        val doctor: Doctor?,
        @SerializedName("doctor_id")
        val doctorId: String?,
        @SerializedName("file")
        val `file`: String?,
        @SerializedName("id")
        val id: String?,
        @SerializedName("is_verified")
        val isVerified: String?,
        @SerializedName("notes")
        val notes: String?,
        @SerializedName("patient")
        val patient: Patient?,
        @SerializedName("patient_id")
        val patientId: String?,
        @SerializedName("updated_at")
        val updatedAt: String?
    ) {
        data class Doctor(
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

        data class Patient(
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
            @SerializedName("updated_at")
            val updatedAt: String?,
            @SerializedName("user_id")
            val userId: String?
        )
    }
}