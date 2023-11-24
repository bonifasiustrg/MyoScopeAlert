package com.apicta.myoscopealert.models.diagnose


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PatientLatestDiagnoseReponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: Any?,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Diagnose(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("diagnoses")
        val diagnoses: String,
        @SerializedName("doctor_id")
        val doctorId: String,
        @SerializedName("file")
        val `file`: String,
        @SerializedName("id")
        val id: String,
        @SerializedName("is_verified")
        val isVerified: String,
        @SerializedName("notes")
        val notes: String,
        @SerializedName("patient_id")
        val patientId: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )

    data class Data(
        @SerializedName("diagnoses")
        val diagnoses: List<Diagnose>
    )
}