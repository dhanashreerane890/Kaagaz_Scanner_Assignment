package com.example.cameraxapp.repository

import androidx.lifecycle.LiveData
import com.example.cameraxapp.db.ImageDetailsDao
import com.example.cameraxapp.db.ImageDetailsEntity


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageRepository (val imageDao: ImageDetailsDao) {

 fun addPicture(imageDetailsEntity: ImageDetailsEntity){
     CoroutineScope(Dispatchers.IO).launch {
         imageDao.addDetails(imageDetailsEntity)
     }
 }
//    fun getAllPictures():LiveData<List<ImageDetailsEntity>>{
//        return imageDao.getAllDetails()
//    }


}