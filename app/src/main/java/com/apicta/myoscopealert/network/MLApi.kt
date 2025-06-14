package com.apicta.myoscopealert.network


import com.apicta.myoscopealert.data.PredictPublicResponse
import com.apicta.myoscopealert.data.PredictResponse
import com.apicta.myoscopealert.models.diagnose.DiagnoseHistoryResponse
import com.apicta.myoscopealert.models.diagnose.PatientDiagnoseResponse
import com.apicta.myoscopealert.models.diagnose.PatientLatestDiagnoseReponse
import com.apicta.myoscopealert.models.diagnose.PatientStatisticResponse
import com.apicta.myoscopealert.models.diagnose.PredictResult
import com.apicta.myoscopealert.models.user.LoginResponse
import com.apicta.myoscopealert.models.user.LogoutResponse
import com.apicta.myoscopealert.models.user.PatientProfileResponse
import com.apicta.myoscopealert.models.user.ProfileResponse
import com.apicta.myoscopealert.models.user.SignInRequest
import com.apicta.myoscopealert.models.user.SignInResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface MLApi {
    @Multipart
    @POST("predict")
    suspend fun predict(
        @Part audio: MultipartBody.Part,
    ): PredictResult
}
