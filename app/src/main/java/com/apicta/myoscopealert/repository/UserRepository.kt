package com.apicta.myoscopealert.repository

import com.apicta.myoscopealert.data.login.SignInRequest
import com.apicta.myoscopealert.data.login.SignInResponse
import com.apicta.myoscopealert.network.UserApi
import retrofit2.Response

class UserRepository(private val userApi: UserApi) {

    suspend fun login(email: String, password: String): Response<SignInResponse> {
        val signInRequest = SignInRequest()
        signInRequest.email = email
        signInRequest.password = password
        return userApi.login(signInRequest)
    }

    // Implementasikan fungsi-fungsi lainnya seperti logout jika diperlukan
}
