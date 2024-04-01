package com.fshou.ceritain.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.fshou.ceritain.data.local.datastore.LoginUserPreference
import com.fshou.ceritain.data.remote.response.LoginResult
import com.fshou.ceritain.data.remote.response.Response
import com.fshou.ceritain.data.remote.retrofit.ApiService
import com.google.gson.Gson
import retrofit2.HttpException

class AppRepository private constructor(
    private val apiService: ApiService,
    private val loginUserPreference: LoginUserPreference,
) {

    fun getLoginUser() = loginUserPreference.getLoginUser()
    suspend fun saveLoginUser(user: Set<String>) = loginUserPreference.saveLoginUser(user)
    suspend fun clearLoginUser() = loginUserPreference.clearLoginUser()

     fun register(name: String, email: String, password: String): LiveData<Result<Response>> = liveData {
       emit(Result.Loading)
         println(name + email + password)
        try {
            val response = apiService.register(name,email,password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonBody = e.response()?.errorBody()?.toString()
            val errorBody = Gson().fromJson(jsonBody,Response::class.java)
            errorBody.message?.let {
                emit(Result.Error(it))
            }
        }
    }

     fun login( email: String, password: String): LiveData<Result<LoginResult>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email,password)
             response.loginResult?.let {
                 emit(Result.Success(it))
             }
        } catch (e: HttpException) {
            val jsonBody = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonBody,Response::class.java)
            errorBody.message?.let {
                emit(Result.Error(it))
            }
        }
    }
    companion object {
        @Volatile
        private var instance: AppRepository? = null
        fun getInstance(
            apiService: ApiService,
            loginUserPreference: LoginUserPreference
        ): AppRepository = instance ?: synchronized(this) {
            instance ?: AppRepository(apiService, loginUserPreference)
        }.also { instance = it }

    }


}