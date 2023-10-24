package com.apicta.myoscopealert.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.data.PredictResponse
import com.apicta.myoscopealert.models.diagnose.PatientDiagnoseResponse
import com.apicta.myoscopealert.data.repository.DiagnosesRepository
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
class DiagnosesViewModel @Inject constructor(
    private val repository: DiagnosesRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val _userToken = MutableStateFlow<String?>(null)
    val userToken: StateFlow<String?> get() = _userToken

    init {
        viewModelScope.launch {
            // Read token from DataStore
            dataStoreManager.getAuthToken.collect { userToken ->
                _userToken.value = userToken
            }
        }
    }

    private val _diagnosesResponse = MutableStateFlow<PatientDiagnoseResponse?>(null)
    val diagnosesResponse: StateFlow<PatientDiagnoseResponse?> = _diagnosesResponse

    fun performDiagnoses(token: String) {
        if (token.isBlank()) {
            // Token belum tersedia, tidak perlu melakukan request
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.diagnoses(token)

                if (response.isSuccessful) {
                    val rawResponse = response.body()
                    if (rawResponse != null) {

                        Log.e("DiagnosesActivity", "diagnosa nya ${rawResponse.status}")
                        _diagnosesResponse.value = rawResponse
                    } else {
                        Log.e("DiagnosesActivity", "Response body is null")
                        _diagnosesResponse.value = null
                    }
                } else {
                    Log.e("DiagnosesActivity", "Request failed with code ${response.code()}")
                    Log.e("DiagnosesActivity", "Response: ${response.errorBody()?.string()}")
                    _diagnosesResponse.value = null
                }
            } catch (e: Exception) {
                Log.e("DiagnosesActivity", "Error during API call: ${e.message}", e)
                _diagnosesResponse.value = null
            }
        }
    }

    private val _predictResponse = MutableStateFlow<PredictResponse?>(null)
    val predictResponse: StateFlow<PredictResponse?> = _predictResponse

    fun performPredict(filePath: String, token: String) {

        val file = File(filePath)
        val requestFile = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        viewModelScope.launch {
            try {
                val response = repository.uploadFile(body, token)

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