package com.fshou.ceritain.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fshou.ceritain.data.AppRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    val appRepository: AppRepository
) : ViewModel() {

    fun login(email: String,password:String) = appRepository.login(email, password)
    fun saveLoginUser(name:String , userId: String, token: String)= viewModelScope.launch {
        val formattedToken = "Bearer: $token"
        val user = setOf( name, userId, formattedToken)
        appRepository.saveLoginUser(user)
    }
}