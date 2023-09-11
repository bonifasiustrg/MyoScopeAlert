package com.apicta.myoscopealert.repository


import android.util.Log
import com.apicta.myoscopealert.data.login.LogoutResponse
import com.apicta.myoscopealert.data.login.SignInRequest
import com.apicta.myoscopealert.data.login.SignInResponse
import com.apicta.myoscopealert.network.UserApi
import retrofit2.Response

class UserRepository(private val userApi: UserApi) {

    suspend fun login(email: String, password: String): Response<SignInResponse> {
        val signInRequest = SignInRequest()
        signInRequest.email = email
        signInRequest.password = password
        val respon = userApi.login(signInRequest)
        Log.e("login repo", respon.body().toString())
        return respon
    }

    // Implementasikan fungsi-fungsi lainnya seperti logout jika diperlukan
    suspend fun logout(token: String): Response<LogoutResponse> {
        return userApi.logout("Bearer $token")
    }
}
