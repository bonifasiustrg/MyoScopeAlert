package com.apicta.myoscopealert.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.apicta.myoscopealert.models.user.AccountInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DataStoreManager(val context: Context) {
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "datastore_manager")
    private val USER_EMAIL = stringPreferencesKey(name = "user_email")
    private val USER_PASSWORD = stringPreferencesKey(name = "user_password")
    private val USER_TOKEN = stringPreferencesKey(name = "user_token")
    private val USER_ID = intPreferencesKey(name = "user_id")

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var INSTANCE: DataStoreManager? = null
        fun getInstance(context: Context): DataStoreManager {
            if (INSTANCE == null) {
                synchronized(DataStoreManager::class.java) {
                    INSTANCE = DataStoreManager(context.applicationContext)
                }
            }
            return INSTANCE!!
        }
    }

    suspend fun setUserToken(/*authToken: String*/ accountInfo: AccountInfo) {
        withContext(Dispatchers.IO) {
            context.datastore.edit { preferences ->
                preferences[USER_TOKEN] = /*authToken*/accountInfo.token.toString()
                preferences[USER_ID] = /*authToken*/accountInfo.userId!!.toInt()
            }
        }
    }

    //    val getAuthToken: Flow<String?> = context.datastore.data.map { preferences ->
//        preferences[USER_TOKEN] ?: ""
//    }
    val getAuthToken: Flow<AccountInfo?> = context.datastore.data.map { preferences ->
        val token = preferences[USER_TOKEN] ?: ""
        val userId = preferences[USER_ID] ?: 4
        AccountInfo(token = token, userId = userId)
    }

    suspend fun clearUserToken() {
        context.datastore.edit { preferences ->
//            preferences.remove(USER_TOKEN)
            preferences.clear()
        }
    }
}