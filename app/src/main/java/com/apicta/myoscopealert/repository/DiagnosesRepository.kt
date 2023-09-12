package com.apicta.myoscopealert.repository

import com.apicta.myoscopealert.data.diagnose.PatientDiagnoseResponse
import com.apicta.myoscopealert.data.user.ProfileResponse
import com.apicta.myoscopealert.network.UserApi
import retrofit2.Response

class DiagnosesRepository(private val userApi: UserApi) {
    suspend fun diagnoses(token: String): Response<PatientDiagnoseResponse> {
        return userApi.diagnoses("Bearer $token")
    }
}