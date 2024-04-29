package com.fshou.ceritain.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.fshou.ceritain.data.AppRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val appRepository: AppRepository) : ViewModel() {


//    fun getStories(): LiveData<Result<List<Story>>> = appRepository.getStories()

    val stories = appRepository.getStories().cachedIn(viewModelScope)


    fun clearLoginUser() = viewModelScope.launch {
        appRepository.clearLoginUser()
    }

}