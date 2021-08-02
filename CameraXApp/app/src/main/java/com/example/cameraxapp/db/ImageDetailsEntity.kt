package com.example.cameraxapp.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ImageDetails")
data class ImageDetailsEntity(
    val photoName: String? = null,
    val timeStamps: String? = null,
    val photoUrl: String? = null, ) {
    @PrimaryKey(autoGenerate = true)
    var id: Int=0
}