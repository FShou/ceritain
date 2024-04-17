package com.fshou.ceritain.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.fshou.ceritain.data.AppRepository
import com.fshou.ceritain.data.Result
import com.fshou.ceritain.data.remote.response.Story
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(private val appRepository: AppRepository): ViewModel() {


    lateinit var pref: LiveData<String?>
    init {
        getLoginUser()
    }
    suspend fun getStories(): LiveData<Result<List<Story>>>  {
        val token: String? = appRepository.getLoginUser().first()
        return appRepository.getStories(token.toString())
    }

    fun clearLoginUser() = viewModelScope.launch {
        appRepository.clearLoginUser()
    }



    fun getLoginUser() {
        pref = appRepository.getLoginUser().asLiveData()
    }
}