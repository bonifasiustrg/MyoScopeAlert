package com.apicta.myoscopealert.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.data.diagnose.PatientDiagnoseResponse
import com.apicta.myoscopealert.data.user.ProfileResponse
import com.apicta.myoscopealert.repository.DiagnosesRepository
import com.apicta.myoscopealert.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosesViewModel @Inject constructor(
    private val repository: DiagnosesRepository,
    private val dataStoreManager: DataStoreManager

) : ViewModel() {
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
}