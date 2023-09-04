package com.apicta.myoscopealert.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DataStoreManager(val context: Context) {
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "datastore_manager")
    private val USER_EMAIL = stringPreferencesKey(name = "user_email")
    private val USER_PASSWORD = stringPreferencesKey(name = "user_password")
    private val USER_TOKEN = stringPreferencesKey(name = "user_token")

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

    suspend fun setUserToken(authToken: String) {
        withContext(Dispatchers.IO) {
            context.datastore.edit { preferences ->
                preferences[USER_TOKEN] = authToken
            }
        }
    }
    val getAuthToken: Flow<String?> = context.datastore.data.map { preferences ->
        preferences[USER_TOKEN] ?: ""
    }
    suspend fun clearUserToken() {
        context.datastore.edit { preferences ->
            preferences.remove(USER_TOKEN)
        }
    }
}
