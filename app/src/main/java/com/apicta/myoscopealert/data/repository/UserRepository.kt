package com.apicta.myoscopealert.data.repository


import android.util.Log
import com.apicta.myoscopealert.models.diagnose.PatientDiagnoseResponse
import com.apicta.myoscopealert.models.user.AccountInfo
import com.apicta.myoscopealert.models.user.LoginResponse
import com.apicta.myoscopealert.models.user.LogoutResponse
import com.apicta.myoscopealert.models.user.PatientProfileResponse
import com.apicta.myoscopealert.models.user.ProfileResponse
import com.apicta.myoscopealert.models.user.SignInRequest
import com.apicta.myoscopealert.models.user.SignInResponse
import com.apicta.myoscopealert.network.UserApi
import retrofit2.Response

class UserRepository(private val userApi: UserApi) {

    suspend fun login(email: String, password: String): Response<LoginResponse> {
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

    suspend fun profile(/*token: String, userId: Int*/ accountInfo: AccountInfo): Response<PatientProfileResponse> {
        val respon = userApi.profile("Bearer ${accountInfo.token}", accountInfo.userId!!)

//        Log.e("profile repo", "$accountInfo")
//        val respon = userApi.profile("Bearer 26|PQ3YoF7G49aClij2oPjZx2PZMQupQsfQx7zgDduWf8c0e1ff", /*accountInfo.userId!!*/4)
//        Log.e("profile repo", "token: Bearer 26|PQ3YoF7G49aClij2oPjZx2PZMQupQsfQx7zgDduWf8c0e1ff, id: 4")
        Log.e("profile repo", respon.body().toString())
        return respon
    }

    suspend fun diagnoses(token: String): Response<PatientDiagnoseResponse> {
        return userApi.diagnoses("Bearer $token")
    }
}
