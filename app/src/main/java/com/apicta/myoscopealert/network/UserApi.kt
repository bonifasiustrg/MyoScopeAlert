package com.apicta.myoscopealert.network


import com.apicta.myoscopealert.data.PredictPublicResponse
import com.apicta.myoscopealert.models.diagnose.DiagnoseHistoryResponse
import com.apicta.myoscopealert.models.diagnose.PatientDiagnoseResponse
import com.apicta.myoscopealert.models.diagnose.PatientLatestDiagnoseReponse
import com.apicta.myoscopealert.models.diagnose.PatientStatisticResponse
import com.apicta.myoscopealert.models.diagnose.SendWavResponse
import com.apicta.myoscopealert.models.diagnose.SingleDiagnoseResponse
import com.apicta.myoscopealert.models.user.LoginResponse
import com.apicta.myoscopealert.models.user.LogoutResponse
import com.apicta.myoscopealert.models.user.PatientProfileResponse
import com.apicta.myoscopealert.models.user.SignInRequest
import okhttp3.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @POST("login")
    suspend fun login(
        @Body loginRequest: SignInRequest
    ): Response<LoginResponse>

    @GET("logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<LogoutResponse>

    @GET("patients/{userId}")
    suspend fun profile(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<PatientProfileResponse>

    @GET("detections/{userId}")
    suspend fun diagnoseHistory(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): Response<DiagnoseHistoryResponse>

    @GET("detection/{userId}/{itemId}")
    suspend fun singleDiagnose(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Path("itemId") itemId: Int
    ): Response<SingleDiagnoseResponse>

    @GET("patient/dashboard/total-statistic")
    suspend fun patientStatistic(
        @Header("Authorization") token: String
    ): Response<PatientStatisticResponse>
    @GET("patient/dashboard/latest-diagnose")
    suspend fun patientLatesDiagnose(
        @Header("Authorization") token: String
    ): Response<PatientLatestDiagnoseReponse>

    @GET("patient/diagnoses")
    suspend fun diagnoses(
        @Header("Authorization") token: String
    ): Response<PatientDiagnoseResponse>

    @Multipart
    @POST("patient/predict")
    suspend fun uploadFile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Query("patient_id") patient_id: String,
    ): PredictPublicResponse

//    @Multipart
//    @POST("add-detection")
//    suspend fun sendWav(
//        @Header("Authorization") token: String,
//        @Part("condition") condition: RequestBody,
//        @Part file: MultipartBody.Part,
//    ): SendWavResponse

    @Multipart
    @POST("add-detection") // Ganti dengan endpoint API Anda
    suspend fun sendWav(
        @Header("Authorization") token: String,
        @Part("condition") condition: RequestBody,
        @Part file: MultipartBody.Part
    ): SendWavResponse
}
