package com.example.cameraxapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PreviewViewModel ( application: Application): AndroidViewModel(application){

    val _position = MutableLiveData<String>()
    val position: LiveData<String>
        get() = _position
}