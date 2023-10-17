package com.apicta.myoscopealert.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apicta.myoscopealert.data.PredictResponse
import com.apicta.myoscopealert.repository.DiagnosesRepository
import com.apicta.myoscopealert.repository.PredictRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel

class PredictViewModel @Inject constructor(
    private val repository: PredictRepository

) : ViewModel() {

    private val _predictResponse = MutableStateFlow<PredictResponse?>(null)
    val predictResponse: StateFlow<PredictResponse?> = _predictResponse
//    fun performPrediction(/*token: String, */file: MultipartBody.Part) {
//
//        viewModelScope.launch {
//            try {
//                val response = repository.predict(/*token, */file)
//
//                if (response.isSuccessful) {
//                    // Handle successful response
//                    val rawResponse = response.body()
//                    if (rawResponse != null) {
//
//                        Log.e("PredictActivity", "diagnosa nya ${rawResponse.result}")
//                        _predictResponse.value = rawResponse
//                    } else {
//                        Log.e("PredictActivity", "Response body is null")
//                        _predictResponse.value = null
//                    }
//                } else {
//                    Log.e("DiagnosesViewModel", "Prediction request failed with code ${response.code()}")
//                    Log.e("DiagnosesViewModel", "Response: ${response.errorBody()?.string()}")
//                }
//            } catch (e: Exception) {
//                Log.e("DiagnosesViewModel", "Error during prediction API call: ${e.message}", e)
//            }
//        }
//    }

    fun performPredict(filePath: String) {

        val file = File(filePath)
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        viewModelScope.launch {
            try {
                val response = repository.uploadFile(body)

                // Handle response here
                Log.e("PredictActivity", "diagnosis result: ${response.result}")
                // Update UI or do something with the response
                _predictResponse.value = response
            } catch (e: Exception) {
                Log.e("PredictActivity", "Error during API call: ${e.message}", e)
                _predictResponse.value = null
            }
        }
    }
}