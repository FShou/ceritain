package com.fshou.ceritain.data.di

import android.content.Context
import com.fshou.ceritain.data.AppRepository
import com.fshou.ceritain.data.local.datastore.LoginUserPreference
import com.fshou.ceritain.data.local.datastore.datastore
import com.fshou.ceritain.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): AppRepository {
        val pref = LoginUserPreference.getInstance(context.datastore)
        val apiService = ApiConfig.getApiService()


        return AppRepository.getInstance(apiService, pref)
    }
}