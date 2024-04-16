package com.fshou.ceritain.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.fshou.ceritain.data.AppRepository
import com.fshou.ceritain.data.Result
import com.fshou.ceritain.data.remote.response.Response
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostViewModel(private val appRepository: AppRepository) : ViewModel() {


    suspend fun postStory(
        imgFile: MultipartBody.Part,
        description: RequestBody
    ): LiveData<Result<Response>> {
        val token: String? = appRepository.getLoginUser().first()
        return appRepository.postStory(imgFile, description, token.toString())
    }

}