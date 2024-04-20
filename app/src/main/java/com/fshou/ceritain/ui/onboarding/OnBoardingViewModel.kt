package com.fshou.ceritain.ui.onboarding

import androidx.lifecycle.ViewModel
import com.fshou.ceritain.data.AppRepository

class OnBoardingViewModel(private val appRepository: AppRepository): ViewModel() {



    suspend fun getLoginUser() = appRepository.getLoginUser()

}