package com.apicta.myoscopealert.models.diagnose


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class PatientStatisticResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("error")
    val error: Any?,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String
) {
    data class Data(
        @SerializedName("total_patient_mi")
        val totalPatientMi: Int,
        @SerializedName("total_patient_normal")
        val totalPatientNormal: Int,
        @SerializedName("total_unverified")
        val totalUnverified: Int,
        @SerializedName("total_verified")
        val totalVerified: Int
    )
}