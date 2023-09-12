package com.apicta.myoscopealert.network


import com.apicta.myoscopealert.data.diagnose.PatientDiagnoseResponse
import com.apicta.myoscopealert.data.user.LogoutResponse
import com.apicta.myoscopealert.data.user.ProfileResponse
import com.apicta.myoscopealert.data.user.SignInRequest
import com.apicta.myoscopealert.data.user.SignInResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

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
}