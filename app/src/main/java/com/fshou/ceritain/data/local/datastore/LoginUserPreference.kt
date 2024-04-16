package com.fshou.ceritain.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.datastore : DataStore<Preferences> by preferencesDataStore(name = "login-user")

class LoginUserPreference private constructor(private val dataStore: DataStore<Preferences>){
    private val LOGIN_USER_KEY = stringPreferencesKey("user")

    fun getLoginUser(): Flow<String?>{
        return dataStore.data.map {
            it[LOGIN_USER_KEY]
        }
    }

    suspend fun saveLoginUser(token: String){
        dataStore.edit {
            it[LOGIN_USER_KEY] = token
        }
    }

    suspend fun clearLoginUser(){
        dataStore.edit {
            it.clear()
        }
    }



    companion object {
        @Volatile
        private var _instance: LoginUserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): LoginUserPreference{
            return _instance ?: synchronized(this) {
                val instance = LoginUserPreference(dataStore)
                _instance = instance
                instance
            }
        }
    }
}