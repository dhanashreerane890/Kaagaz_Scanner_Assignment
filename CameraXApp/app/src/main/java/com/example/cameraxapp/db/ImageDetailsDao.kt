package com.example.cameraxapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageDetailsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun  addDetails(entity: ImageDetailsEntity)

    @Query("SELECT * FROM imagedetails order by id DESC")
    fun getAllDetails(): LiveData<List<ImageDetailsEntity>>
}