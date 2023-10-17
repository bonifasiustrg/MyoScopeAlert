package com.apicta.myoscopealert.network


import com.apicta.myoscopealert.data.PredictResponse
import com.apicta.myoscopealert.data.diagnose.PatientDiagnoseResponse
import com.apicta.myoscopealert.data.user.LogoutResponse
import com.apicta.myoscopealert.data.user.ProfileResponse
import com.apicta.myoscopealert.data.user.SignInRequest
import com.apicta.myoscopealert.data.user.SignInResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApi {
    @POST("account/login")
    suspend fun login(
        @Body loginRequest: SignInRequest
    ): Response<SignInResponse>

    @GET("account/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<LogoutResponse>

    @GET("account/profile")
    suspend fun profile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    @GET("patient/diagnoses")
    suspend fun diagnoses(
        @Header("Authorization") token: String
    ): Response<PatientDiagnoseResponse>

    @Multipart
    @POST("public/predict")
    suspend fun uploadFile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): PredictResponse
}
