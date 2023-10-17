package com.apicta.myoscopealert.repository


import android.util.Log
import com.apicta.myoscopealert.data.diagnose.PatientDiagnoseResponse
import com.apicta.myoscopealert.data.user.LogoutResponse
import com.apicta.myoscopealert.data.user.ProfileResponse
import com.apicta.myoscopealert.data.user.SignInRequest
import com.apicta.myoscopealert.data.user.SignInResponse
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
//        return userApi.login(signInRequest)
    }

    // Implementasikan fungsi-fungsi lainnya seperti logout jika diperlukan
    suspend fun logout(token: String): Response<LogoutResponse> {
        return userApi.logout("Bearer $token")
    }

    suspend fun profile(token: String): Response<ProfileResponse> {
        val respon = userApi.profile("Bearer $token")
        Log.e("profile repo", respon.body().toString())
        return respon
    }

    suspend fun diagnoses(token: String): Response<PatientDiagnoseResponse> {
        return userApi.diagnoses("Bearer $token")
    }
}
