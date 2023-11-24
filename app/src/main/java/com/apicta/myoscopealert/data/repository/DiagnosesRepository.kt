package com.apicta.myoscopealert.data.repository

import com.apicta.myoscopealert.data.PredictResponse
import com.apicta.myoscopealert.models.diagnose.PatientDiagnoseResponse
import com.apicta.myoscopealert.models.diagnose.PatientLatestDiagnoseReponse
import com.apicta.myoscopealert.models.diagnose.PatientStatisticResponse
import com.apicta.myoscopealert.models.user.ProfileResponse
import com.apicta.myoscopealert.network.UserApi
import okhttp3.MultipartBody
import retrofit2.Response

class DiagnosesRepository(private val userApi: UserApi) {
    suspend fun diagnoses(token: String): Response<PatientDiagnoseResponse> {
        return userApi.diagnoses("Bearer $token")
    }
    suspend fun patientStatistic(token: String): Response<PatientStatisticResponse> {
        return userApi.patientStatistic("Bearer $token")
    }
    suspend fun patientLatestDiagnose(token: String): Response<PatientLatestDiagnoseReponse> {
        return userApi.patientLatesDiagnose("Bearer $token")
    }

//    suspend fun predict(/*token: String, */file: MultipartBody.Part): Response<PredictResponse> {
////        val authorizationHeader = "Bearer $token"
//        return userApi.predict(/*authorizationHeader, */file)
//    }
    suspend fun uploadFile(file: MultipartBody.Part, token: String): PredictResponse {
        return userApi.uploadFile("Bearer $token", file)
    }
}