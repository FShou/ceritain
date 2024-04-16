package com.fshou.ceritain.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fshou.ceritain.data.AppRepository
import com.fshou.ceritain.data.di.Injection
import com.fshou.ceritain.ui.home.HomeViewModel
import com.fshou.ceritain.ui.login.LoginViewModel
import com.fshou.ceritain.ui.onboarding.OnBoardingViewModel
import com.fshou.ceritain.ui.post.PostViewModel
import com.fshou.ceritain.ui.register.RegisterViewModel

class ViewModelFactory private constructor(
    private val appRepository: AppRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> return LoginViewModel(
                appRepository
            ) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> return RegisterViewModel(
                appRepository
            ) as T
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> return HomeViewModel(
                appRepository
            ) as T
            modelClass.isAssignableFrom(OnBoardingViewModel::class.java) -> return OnBoardingViewModel(
                appRepository
            ) as T
            modelClass.isAssignableFrom(PostViewModel::class.java) -> return PostViewModel(
                appRepository
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}