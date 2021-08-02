package com.example.cameraxapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cameraxapp.db.ImageDetailsDatabase
import com.example.cameraxapp.db.ImageDetailsEntity
import com.example.cameraxapp.repository.ImageRepository


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageViewModel( application: Application): AndroidViewModel(application) {
    private val imageRepository: ImageRepository

    init {
        val dao= ImageDetailsDatabase.getDatabase(application).getMyImageDetailsDao()
        imageRepository= ImageRepository(dao)
    }
 fun insertImage(imageDetailsEntity: ImageDetailsEntity) = viewModelScope.launch(Dispatchers.IO){
     imageRepository.addPicture(imageDetailsEntity)
 }




}