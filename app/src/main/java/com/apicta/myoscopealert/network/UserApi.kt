<<<<<<< HEAD
package com.apicta.myoscopealert.network


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
    fun logout(
        @Header("Authorization") token: String
    )
}
=======
package com.apicta.myoscopealert.network


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
    fun logout(
        @Header("Authorization") token: String
    )
}
>>>>>>> 7f1866f (Implement Depedency Injection with Dagger Hilt for Viewmodel, Datastore (stored token), Repository, and Api Interface.)
