package com.fshou.ceritain.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fshou.ceritain.data.AppRepository
import com.fshou.ceritain.data.Result
import com.fshou.ceritain.data.remote.response.Story
import kotlinx.coroutines.launch

class HomeViewModel(private val appRepository: AppRepository) : ViewModel() {


    fun getStories(): LiveData<Result<List<Story>>> = appRepository.getStories()

    fun clearLoginUser() = viewModelScope.launch {
        appRepository.clearLoginUser()
    }

}