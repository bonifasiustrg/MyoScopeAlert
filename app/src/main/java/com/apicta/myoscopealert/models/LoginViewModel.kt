package com.apicta.myoscopealert.models

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apicta.myoscopealert.data.DataStoreManager
import com.apicta.myoscopealert.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    fun performLogin(email: String, password: String) {
        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                if (response.isSuccessful) {
                    val rawResponse = response.body()
                    if (rawResponse != null) {
                        Log.e("status", "Login Successfully")
                        Log.e("token", rawResponse.data?.token ?: "Token not available")
                        val token = rawResponse.data?.token ?: ""
                        GlobalScope.launch {
                            dataStoreManager.setUserToken(token)
                        }
                        Log.e("SignInActivity", "$rawResponse")
                    } else {
                        Log.e("SignInActivity", "Response body is null")
                    }
                } else {
                    Log.e("SignInActivity", "Request failed with code ${response.code()}")
                    Log.e("SignInActivity", "Response: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("SignInActivity", "Error during API call: ${e.message}", e)
            }
        }
    }
}

