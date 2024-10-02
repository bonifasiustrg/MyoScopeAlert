package com.apicta.myoscopealert.data.repository

import com.apicta.myoscopealert.data.PredictPublicResponse
import com.apicta.myoscopealert.models.diagnose.DiagnoseHistoryResponse
import com.apicta.myoscopealert.models.diagnose.PatientDiagnoseResponse
import com.apicta.myoscopealert.models.diagnose.PatientLatestDiagnoseReponse
import com.apicta.myoscopealert.models.diagnose.PatientStatisticResponse
import com.apicta.myoscopealert.models.diagnose.PredictResult
import com.apicta.myoscopealert.models.diagnose.SendWavResponse
import com.apicta.myoscopealert.models.diagnose.SingleDiagnoseResponse
import com.apicta.myoscopealert.network.MLApi
import com.apicta.myoscopealert.network.UserApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

class DiagnosesRepository(private val userApi: UserApi, private val mlApi: MLApi) {
    suspend fun diagnoses(token: String): Response<PatientDiagnoseResponse> {
        return userApi.diagnoses("Bearer $token")
    }

    suspend fun patientStatistic(token: String): Response<PatientStatisticResponse> {
        return userApi.patientStatistic("Bearer $token")
    }
    suspend fun patientLatestDiagnose(token: String): Response<PatientLatestDiagnoseReponse> {
        return userApi.patientLatesDiagnose("Bearer $token")
    }
    suspend fun diagnoseHistory(token: String, userId: Int): Response<DiagnoseHistoryResponse> {
        return userApi.diagnoseHistory("Bearer $token", userId)
    }
    suspend fun singleDiagnose(token: String, userId: Int, itemId: Int): Response<SingleDiagnoseResponse> {
        return userApi.singleDiagnose("Bearer $token", userId, itemId)
    }

//    suspend fun predict(/*token: String, */file: MultipartBody.Part): Response<PredictResponse> {
////        val authorizationHeader = "Bearer $token"
//        return userApi.predict(/*authorizationHeader, */file)
//    }
//    suspend fun predict(/*token: String, */file: MultipartBody.Part): Response<PredictResult> {
//        return mlApi.predict(/*authorizationHeader, */file)
//    }
    suspend fun predict(file: MultipartBody.Part): PredictResult {
        return mlApi.predict(file)
    }
    suspend fun uploadFile(file: MultipartBody.Part, token: String, id: String): PredictPublicResponse {
        return userApi.uploadFile("Bearer $token", file, id)
    }

//    suspend fun sendWav(file: MultipartBody.Part, token: String, condition: RequestBody): SendWavResponse {
//        return userApi.sendWav("Bearer $token", condition, file)
//    }
suspend fun sendWav(file: MultipartBody.Part, token: String, condition: RequestBody): SendWavResponse {
    return userApi.sendWav("Bearer $token", condition, file)
}
}