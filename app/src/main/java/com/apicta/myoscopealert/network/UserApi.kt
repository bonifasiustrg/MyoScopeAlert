package com.apicta.myoscopealert.network


import com.apicta.myoscopealert.data.login.LogoutResponse
import com.apicta.myoscopealert.data.login.SignInRequest
import com.apicta.myoscopealert.data.login.SignInResponse
import okhttp3.ResponseBody
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
}