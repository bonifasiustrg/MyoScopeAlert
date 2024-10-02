package com.apicta.myoscopealert.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.models.diagnose.PatientDiagnoseResponse
import com.apicta.myoscopealert.data.repository.DiagnosesRepository
import com.apicta.myoscopealert.models.diagnose.DiagnoseHistoryResponse
import com.apicta.myoscopealert.models.diagnose.PatientLatestDiagnoseReponse
import com.apicta.myoscopealert.models.diagnose.PatientStatisticResponse
import com.apicta.myoscopealert.models.diagnose.PredictResult
import com.apicta.myoscopealert.models.diagnose.SendWavResponse
import com.apicta.myoscopealert.models.diagnose.SingleDiagnoseResponse
import com.apicta.myoscopealert.models.user.AccountInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DiagnosesViewModel @Inject constructor(
    private val repository: DiagnosesRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    private val _accountInfo = MutableStateFlow<AccountInfo?>(null)
    val accountInfo: StateFlow<AccountInfo?> get() = _accountInfo


    private val _userToken = MutableStateFlow<String?>(null)
    val userToken: StateFlow<String?> get() = _userToken

    init {
        viewModelScope.launch {
            // Read token from DataStore
            dataStoreManager.getAuthToken.collect { /*userToken*/accountInfo ->
//                _userToken.value = userToken?.token
                _accountInfo.value = accountInfo
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

//    private val _predictResponse = MutableStateFlow<PredictPublicResponse?>(null)
//    val predictResponse: StateFlow<PredictPublicResponse?> = _predictResponse
    private val _predictResponse = MutableStateFlow<PredictResult?>(null)
    val predictResponse: StateFlow<PredictResult?> = _predictResponse

    fun performPredict(filePath: String/*, token: String, id: String*/) {

        val file = File(filePath)
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("audio", file.name, requestFile)

        viewModelScope.launch {
            try {
//                val response = repository.uploadFile(body, token, id)
                val response = repository.predict(body)

                // Handle response here
//                Log.e("PredictActivity", "diagnosis result: ${response.body()?.result}")
                Log.e("PredictActivity", "diagnosis result: ${response}")
                // Update UI or do something with the response
                _predictResponse.value = response
            } catch (e: Exception) {
                Log.e("PredictActivity", "Error during API call: ${e.message}", e)
                _predictResponse.value = null
            }
        }
    }

    private val _patientStatisticResponse = MutableStateFlow<PatientStatisticResponse?>(null)
    val patientStatisticResponse: StateFlow<PatientStatisticResponse?> = _patientStatisticResponse

    fun getPatientStatistic(token: String) {
        if (token.isBlank()) {
            // Token belum tersedia, tidak perlu melakukan request
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.patientStatistic(token)

                if (response.isSuccessful) {
                    val rawResponse = response.body()
                    if (rawResponse != null) {

                        Log.e("PatientStatisticVM", "profile nya ${rawResponse.status}")
                        _patientStatisticResponse.value = rawResponse
                    } else {
                        Log.e("PatientStatisticVM", "Response body is null")
                        _patientStatisticResponse.value = null
                    }
                } else {
                    Log.e("PatientStatisticVM", "Request failed with code ${response.code()}")
//                    Log.e("PatientStatisticVM", "Response: ${response.errorBody()?.string()}")
                    _patientStatisticResponse.value = null
                }
            } catch (e: Exception) {
                Log.e("PatientStatisticVM", "Error during API call: ${e.message}", e)
                _patientStatisticResponse.value = null
            }
        }
    }
    private val _patientLatestDiagnoseResponse = MutableStateFlow<PatientLatestDiagnoseReponse?>(null)
    val patientLatestDiagnoseResponse: StateFlow<PatientLatestDiagnoseReponse?> = _patientLatestDiagnoseResponse

    fun getLatestDiagnose(token: String) {
        if (token.isBlank()) {
            // Token belum tersedia, tidak perlu melakukan request
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.patientLatestDiagnose(token)

                if (response.isSuccessful) {
                    val rawResponse = response.body()
                    if (rawResponse != null) {

                        Log.e("LatestDiagnoseVM", "profile nya ${rawResponse.status}")
                        _patientLatestDiagnoseResponse.value = rawResponse
                    } else {
                        Log.e("LatestDiagnoseVM", "Response body is null")
                        _patientLatestDiagnoseResponse.value = null
                    }
                } else {
                    Log.e("LatestDiagnoseVM", "Request failed with code ${response.code()}")
                    Log.e("LatestDiagnoseVM", "Response: ${response.errorBody()?.string()}")
                    _patientLatestDiagnoseResponse.value = null
                }
            } catch (e: Exception) {
                Log.e("LatestDiagnoseVM", "Error during API call: ${e.message}", e)
                _patientLatestDiagnoseResponse.value = null
            }
        }
    }


    private val _diagnoseHistoryResponse = MutableStateFlow<DiagnoseHistoryResponse?>(null)
    val diagnoseHistoryResponse: StateFlow<DiagnoseHistoryResponse?> = _diagnoseHistoryResponse

    fun diagnoseHistory(token: String = accountInfo.value?.token.toString(), userId: Int = accountInfo.value?.userId!!) {
        if (token.isBlank()) {
            // Token belum tersedia, tidak perlu melakukan request
            return
        }

        viewModelScope.launch {
            try {
                Log.e("Diagnose History 0", "profile nya $token $userId")

                val response = repository.diagnoseHistory(token, userId)

                if (response.isSuccessful) {
                    val rawResponse = response.body()
                    if (rawResponse != null) {

                        Log.e("Diagnose History 1", "profile nya ${rawResponse.status}")
                        Log.e("Diagnose History 1", "profile nya ${rawResponse}")
                        _diagnoseHistoryResponse.value = rawResponse
                    } else {
                        Log.e("Diagnose History 1", "Response body is null")
                        _diagnoseHistoryResponse.value = null
                    }
                } else {
                    Log.e("Diagnose History 2", "Request failed with code ${response.code()}")
                    Log.e("Diagnose History 2", "Response: ${response.errorBody()?.string()}")
                    _diagnoseHistoryResponse.value = null
                }
            } catch (e: Exception) {
                Log.e("Diagnose History 3", "Error during API call: ${e.message}", e)
                _diagnoseHistoryResponse.value = null
            }
        }
    }

    private val _singleDiagnoseResponse = MutableStateFlow<SingleDiagnoseResponse?>(null)
    val singleDiagnoseResponse: StateFlow<SingleDiagnoseResponse?> = _singleDiagnoseResponse

    fun singleDiagnose(token: String = accountInfo.value?.token.toString(), userId: Int = accountInfo.value?.userId!!, itemId: Int?) {
        if (token.isBlank()) {
            // Token belum tersedia, tidak perlu melakukan request
            Log.e("SingleDiagnose  cek token", "$token kosong")

            return
        }

        if (itemId == null) {
            Log.e("SingleDiagnose  cek itemid", "$itemId kosong")

            return
        }

        viewModelScope.launch {
            try {
                Log.e("SingleDiagnose  0", "profile nya $token $userId $itemId")

                val response = repository.singleDiagnose(token, userId, itemId)

                if (response.isSuccessful) {
                    val rawResponse = response.body()
                    if (rawResponse != null) {

                        Log.e("SingleDiagnose  1", "profile nya ${rawResponse.status}")
                        Log.e("SingleDiagnose  1", "profile nya ${rawResponse}")
                        _singleDiagnoseResponse.value = rawResponse
                    } else {
                        Log.e("SingleDiagnose  1", "Response body is null")
                        _singleDiagnoseResponse.value = null
                    }
                } else {
                    Log.e("SingleDiagnose  2", "Request failed with code ${response.code()}")
                    Log.e("SingleDiagnose  2", "Response: ${response.errorBody()?.string()}")
                    _singleDiagnoseResponse.value = null
                }
            } catch (e: Exception) {
                Log.e("SingleDiagnose  3", "Error during API call: ${e.message}", e)
                _singleDiagnoseResponse.value = null
            }
        }
    }

    private val _sendWavResponse = MutableStateFlow<SendWavResponse?>(null)
    val sendWavResponse: StateFlow<SendWavResponse?> = _sendWavResponse

//    fun sendWav(filePath: String, token: String="118|1h1JzlHkzjrBYuWBZtga8EB0nEzrw4eXWqdhB3o9d7997783", condition: String="normal") {
//        // Prepare RequestBody for the condition text
//        val conditionBody = RequestBody.create("text/plain".toMediaTypeOrNull(), condition)
//
//        val file = File(filePath)
//        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
////        val body = MultipartBody.Part.createFormData("audio", file.name, requestFile)
//        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
//
//        viewModelScope.launch {
//            try {
////                val response = repository.uploadFile(body, token, id)
//                val response = repository.sendWav(body, token ,conditionBody)
//
//                // Handle response here
////                Log.e("PredictActivity", "diagnosis result: ${response.body()?.result}")
//                Log.e("SendWav", "SendWav result: ${response}")
//                // Update UI or do something with the response
//                _sendWavResponse.value = response
//            } catch (e: Exception) {
//                Log.e("SendWav", "Error during API call: ${e.message}", e)
//                _sendWavResponse.value = null
//            }
//        }
//    }

//fun sendWav(filePath: String, conditionText: String, token: String) {
fun sendWav(filePath: String, token: String, condition: String="normal") {

    // Prepare RequestBody untuk condition text
    val conditionBody = condition.toRequestBody("text/plain".toMediaTypeOrNull())

    val file = File(filePath)
    val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
    val body = MultipartBody.Part.createFormData("heartwave", file.name, requestFile)

    viewModelScope.launch {
        try {
            val response = repository.sendWav(body, token, conditionBody)
            if (response.success == true) {
                // Tangani respons sukses
                Log.e("SendWav", "SendWav result: ${response}")
                _sendWavResponse.value = response
            } else {
                Log.e("SendWav", "Request failed with code ${response}")
                _sendWavResponse.value = null
            }
        } catch (e: Exception) {
            Log.e("SendWav", "Error during API call: ${e.message}", e)
            _sendWavResponse.value = null
        }
    }
}

}