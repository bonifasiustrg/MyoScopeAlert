package com.apicta.myoscopealert.models

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.data.login.SignInResponse
import com.apicta.myoscopealert.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
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
    private val _loginResponse = MutableStateFlow<SignInResponse?>(null)
    val loginResponse: StateFlow<SignInResponse?> = _loginResponse

    fun performLogin(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (response.isSuccessful) {
                    val rawResponse = response.body()
                    if (rawResponse != null) {
                        val token = rawResponse.data?.token ?: ""
                        _loginResponse.value = rawResponse
                        Log.e("token perform login", rawResponse.data?.token ?: "Token not available")
                        Log.e("status login", "Login Successfully")
                        isLoginSuccess.value = true
                        viewModelScope.launch {
                            dataStoreManager.setUserToken(token)
                        }
//                        Log.e("SignInActivity", "${rawResponse.data}")
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

                        Log.e("LogoutActivity", "logout nya ${rawResponse.success}")
                        GlobalScope.launch {
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
}

