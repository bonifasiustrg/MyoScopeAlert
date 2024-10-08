package com.apicta.myoscopealert.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.models.user.ProfileResponse
import com.apicta.myoscopealert.models.user.SignInResponse
import com.apicta.myoscopealert.data.repository.UserRepository
import com.apicta.myoscopealert.models.user.AccountInfo
import com.apicta.myoscopealert.models.user.LoginResponse
import com.apicta.myoscopealert.models.user.PatientProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val isLoginSuccess =  mutableStateOf(false)

    // Create a State object to hold the login status
    private val _loginResponse = MutableStateFlow<LoginResponse?>(null)
    val loginResponse: StateFlow<LoginResponse?> = _loginResponse

    private val _profileResponse = MutableStateFlow</*ProfileResponse*/PatientProfileResponse?>(null)
    val profileResponse: StateFlow</*ProfileResponse*/PatientProfileResponse?> = _profileResponse

    private val _tokenSaved = MutableStateFlow<Boolean>(false)
    val tokenSaved: StateFlow<Boolean> = _tokenSaved

    fun performLogin(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                Log.e("status", "$response")


                if (response.isSuccessful) {
                    val rawResponse = response.body()
                    if (rawResponse != null) {
//                        val token = rawResponse.data?.accessToken ?: ""
                        val token = rawResponse.token ?: ""
                        val userId = rawResponse.data?.id ?: 0
                        _loginResponse.value = rawResponse
                        Log.e("token perform login", rawResponse.token ?: "Token not available")
                        Log.e("status login", "Login Successfully")
                        isLoginSuccess.value = true
                        viewModelScope.launch {
                            dataStoreManager.setUserToken(/*token, userId*/AccountInfo(
                                token, userId
                            ))
                            _tokenSaved.value = true // Token telah disimpan
                        }
                    } else {
                        Log.e("SignInActivity", "Response body is null")
                        _loginResponse.value = null
                    }
                } else {
                    Log.e("SignInActivity", "Request failed with code ${response.code()}")
                    Log.e("SignInActivity", "Response: ${response.errorBody()?.string()}")
                    _loginResponse.value = null
                }
            } catch (e: Exception) {
                Log.e("SignInActivity", "Error during API call: ${e.message}", e)
                _loginResponse.value = null
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun performLogout(token: String) {
        viewModelScope.launch {
            try {
                val response = repository.logout(token)
                Log.e("status", "Logout Successfully, current token -> ${dataStoreManager.getAuthToken.first()}")


                if (response.isSuccessful) {
                    val rawResponse = response.body()
                    if (rawResponse != null) {

                        Log.e("LogoutActivity", "logout nya ${rawResponse}")
                        viewModelScope.launch {
                            dataStoreManager.clearUserToken()
                            Log.e("status", "token cleared ${dataStoreManager.getAuthToken.first()}")
                        }
                    } else {
                        Log.e("LogoutActivity", "Response body is null")
                    }
                } else {
                    Log.e("LogoutActivity", "Request failed with code ${response.code()}")
                    Log.e("LogoutActivity", "Response: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("LogoutActivity", "Error during API call: ${e.message}", e)
            }
        }
    }

    fun performProfile(/*token: String*/accountInfo: AccountInfo) {
//        viewModelScope.launch {
//            try {
//                val response = repository.profile(token)
//                if (response.isSuccessful) {
//                    val rawResponse = response.body()
//                    if (rawResponse != null) {
//                        _profileResponse.value = rawResponse
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("UserViewModel", "Error during API call: ${e.message}", e)
//            }
//        }

//        if (token.isBlank()) {
        if (accountInfo.token!!.isBlank()) {
            // Token belum tersedia, tidak perlu melakukan request
            return
        }

        viewModelScope.launch {
            try {
                val response = repository.profile(/*token*/
                    accountInfo
                )

                if (response.isSuccessful) {
                    val rawResponse = response.body()
                    if (rawResponse != null) {

                        Log.e("ProfileActivity", "profile nya ${rawResponse.status}")
                        _profileResponse.value = rawResponse
                    } else {
                        Log.e("ProfileActivity", "Response body is null")
                        _profileResponse.value = null
                    }
                } else {
                    Log.e("ProfileActivity", "Request failed with code ${response.code()}")
//                    Log.e("ProfileActivity", "Response: ${response.errorBody()?.string()}")
                    _profileResponse.value = null
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error during API call: ${e.message}", e)
                _profileResponse.value = null
            }
        }
    }
}

