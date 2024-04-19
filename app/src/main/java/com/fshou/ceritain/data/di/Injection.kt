package com.fshou.ceritain.data.di

import android.content.Context
import com.fshou.ceritain.data.AppRepository
import com.fshou.ceritain.data.local.datastore.LoginUserPreference
import com.fshou.ceritain.data.local.datastore.datastore
import com.fshou.ceritain.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): AppRepository {
        val pref = LoginUserPreference.getInstance(context.datastore)
        val user = runBlocking { pref.getLoginUser().first().toString() }
        val apiService = ApiConfig.getApiService(user)


        return AppRepository.getInstance(apiService, pref)
    }
}