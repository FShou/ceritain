package com.fshou.ceritain.ui.maps

import androidx.lifecycle.ViewModel
import com.fshou.ceritain.data.AppRepository

class MapsViewModel(private val appRepository: AppRepository): ViewModel() {

    fun getStoriesWithLocation() = appRepository.getStoriesWithLocation()
}