package com.fshou.ceritain.ui.capture

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CaptureViewMdoel: ViewModel() {
    private var _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri


    fun setCurrentImageUri (uri: Uri?) {
        _currentImageUri.value = uri
    }



}