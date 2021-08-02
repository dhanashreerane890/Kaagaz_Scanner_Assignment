package com.example.cameraxapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ImageDetailsEntity::class],version = 1,exportSchema = false)
abstract class ImageDetailsDatabase(): RoomDatabase() {

    abstract fun getMyImageDetailsDao(): ImageDetailsDao
    companion object{
        private var INSTANCE: ImageDetailsDatabase?=null
        fun getDatabase(context: Context): ImageDetailsDatabase {
            if(INSTANCE ==null){
                val builder: Builder<ImageDetailsDatabase> = Room.databaseBuilder(
                    context.applicationContext,
                    ImageDetailsDatabase::class.java,
                    "details_database"
                )
                builder.fallbackToDestructiveMigration()
                return builder.build()
                return INSTANCE!!
            }else{
                return INSTANCE!!
            }
        }
    }
}