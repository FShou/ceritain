package com.fshou.ceritain.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.fshou.ceritain.data.local.datastore.LoginUserPreference
import com.fshou.ceritain.data.remote.response.LoginResult
import com.fshou.ceritain.data.remote.response.Response
import com.fshou.ceritain.data.remote.response.Story
import com.fshou.ceritain.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class AppRepository private constructor(
    private val apiService: ApiService,
    private val loginUserPreference: LoginUserPreference,
) {

    suspend fun getLoginUser() = loginUserPreference.getLoginUser().first()
    suspend fun saveLoginUser(user: String) = loginUserPreference.saveLoginUser(user)
    suspend fun clearLoginUser() = loginUserPreference.clearLoginUser()

    fun register(name: String, email: String, password: String): LiveData<Result<Response>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.register(name, email, password)
                emit(Result.Success(response))
            } catch (e: HttpException) {
                val jsonBody = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonBody, Response::class.java)
                errorBody.message?.let {
                    emit(Result.Error(it))
                }
            }catch (e: Exception) {
                val msg = e.message
                msg?.let {
                    emit(Result.Error(it))
                }
            }
        }

    fun login(email: String, password: String): LiveData<Result<LoginResult>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            response.loginResult?.let {
                emit(Result.Success(it))
            }
        } catch (e: HttpException) {
            val jsonBody = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonBody, Response::class.java)
            errorBody.message?.let {
                emit(Result.Error(it))
            }
        }catch (e: Exception) {
            val msg = e.message
            msg?.let {
                emit(Result.Error(it))
            }
        }
    }

    fun getStories(): LiveData<Result<List<Story>>> = liveData {
        emit(Result.Loading)
        try {
            val token = getLoginUser()
            val response = apiService.getStories("Bearer $token")
            response.listStory?.let {
                emit(Result.Success(it))
            }
        } catch (e: HttpException) {
            val jsonBody = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonBody, Response::class.java)
            errorBody.message?.let {
                emit(Result.Error(it))
            }
        } catch (e: Exception) {
            val msg = e.message
            msg?.let {
                emit(Result.Error(it))
            }
        }
    }

    fun postStory(
        imgFile: MultipartBody.Part,
        description: RequestBody,
    ): LiveData<Result<Response>> = liveData {
        emit(Result.Loading)
        try {
            val token = getLoginUser()
            val response = apiService.postStory(imgFile, description, "Bearer $token")
            emit(Result.Success(response))
        }catch (e: HttpException) {
            val jsonBody = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonBody, Response::class.java)
            errorBody.message?.let {
                emit(Result.Error(it))
            }
        }catch (e: Exception) {
            val msg = e.message
            msg?.let {
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
