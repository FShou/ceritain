package com.fshou.ceritain.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.fshou.ceritain.data.local.datastore.LoginUserPreference
import com.fshou.ceritain.data.paging.StoryPagingSource
import com.fshou.ceritain.data.remote.response.BaseResponse
import com.fshou.ceritain.data.remote.response.LoginResult
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

    fun register(name: String, email: String, password: String): LiveData<Result<BaseResponse>> =
        liveData {
            emit(Result.Loading)
            try {
                val response = apiService.register(name, email, password)
                emit(Result.Success(response))
            } catch (e: HttpException) {
                val jsonBody = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonBody, BaseResponse::class.java)
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

    fun login(email: String, password: String): LiveData<Result<LoginResult>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            response.loginResult?.let {
                emit(Result.Success(it))
            }
        } catch (e: HttpException) {
            val jsonBody = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonBody, BaseResponse::class.java)
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

    fun getStories(): LiveData<PagingData<Story>> = liveData {

        val token = getLoginUser()
        val pager = Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { StoryPagingSource(apiService, "Bearer $token") }
        ).liveData
        emitSource(pager)
    }

    fun getStoriesWithLocation(): LiveData<Result<List<Story>>> = liveData {
        emit(Result.Loading)
        try {
            val token = getLoginUser()
            val response = apiService.getStoriesWitLocation(token = "Bearer $token")
            response.listStory?.let {
                emit(Result.Success(it))
            }
        } catch (e: HttpException) {
            val jsonBody = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonBody, BaseResponse::class.java)
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
    ): LiveData<Result<BaseResponse>> = liveData {
        emit(Result.Loading)
        try {
            val token = getLoginUser()
            val response = apiService.postStory(imgFile, description, "Bearer $token")
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val jsonBody = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonBody, BaseResponse::class.java)
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
