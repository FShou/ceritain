package com.fshou.ceritain.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.fshou.ceritain.data.AppRepository
import com.fshou.ceritain.data.Result
import com.fshou.ceritain.data.remote.response.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostViewModel(private val appRepository: AppRepository) : ViewModel() {
    fun postStory(
        imgFile: MultipartBody.Part,
        description: RequestBody,
        lon: RequestBody,
        lat: RequestBody,
    ): LiveData<Result<BaseResponse>> =
        appRepository.postStory(imgFile, description,lon,lat)


}