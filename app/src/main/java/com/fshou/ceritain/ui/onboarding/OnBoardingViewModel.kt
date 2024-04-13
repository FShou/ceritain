package com.fshou.ceritain.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.fshou.ceritain.data.AppRepository

class OnBoardingViewModel(val appRepository: AppRepository): ViewModel() {

    lateinit var pref: LiveData<String?>
    init {
        getLoginUser()
    }

    fun getLoginUser() {
        pref = appRepository.getLoginUser().asLiveData()
    }
}