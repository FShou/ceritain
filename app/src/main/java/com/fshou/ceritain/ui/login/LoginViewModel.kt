package com.fshou.ceritain.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fshou.ceritain.data.AppRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    val appRepository: AppRepository
) : ViewModel() {

    fun login(email: String, password: String) = appRepository.login(email, password)
    fun saveLoginUser(token: String) = viewModelScope.launch {
        val formattedToken = "Bearer $token"
        appRepository.saveLoginUser(formattedToken)
    }
}