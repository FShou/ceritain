package com.fshou.ceritain.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


val Context.datastore : DataStore<Preferences> by preferencesDataStore(name = "login-user")

class LoginUserPreference private constructor(private val dataStore: DataStore<Preferences>){
    private val LOGIN_USER_KEY = stringSetPreferencesKey("user")

    fun getLoginUser(): Flow<Set<String>?>{
        return dataStore.data.map {
            it[LOGIN_USER_KEY]
        }
    }

    suspend fun saveLoginUser(user: Set<String>){
        dataStore.edit {
            it[LOGIN_USER_KEY] = user
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