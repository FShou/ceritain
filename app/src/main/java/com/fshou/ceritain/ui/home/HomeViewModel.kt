package com.fshou.ceritain.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fshou.ceritain.data.AppRepository
import com.fshou.ceritain.data.Result
import com.fshou.ceritain.data.remote.response.Story

class HomeViewModel(val appRepository: AppRepository): ViewModel() {


    // Todo make stories imune to lifecycle
    lateinit var pref: LiveData<Set<String>?>
    init {
        getLoginUser()
    }
    fun getStories(token: String): LiveData<Result<List<Story>>> {
        return appRepository.getStories(token)
    }

    fun getLoginUser() {
        pref = appRepository.getLoginUser().asLiveData()
    }
}