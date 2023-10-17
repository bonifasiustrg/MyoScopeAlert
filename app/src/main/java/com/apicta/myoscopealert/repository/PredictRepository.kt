package com.apicta.myoscopealert.repository

import com.apicta.myoscopealert.data.PredictResponse
import com.apicta.myoscopealert.network.MLApi
import com.apicta.myoscopealert.network.UserApi
import okhttp3.MultipartBody

class PredictRepository(private val mlApi: MLApi) {
    suspend fun uploadFile(file: MultipartBody.Part): PredictResponse {
        return mlApi.uploadFile(file)
    }
}