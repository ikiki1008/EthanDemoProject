package com.example.myapplication.dataclass

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.dataclass.CommunityPostDao
import com.example.myapplication.dataclass.CommunityPostEntity

@Database(entities = [CommunityPostEntity::class], version = 1)
abstract class CommunityDataBase : RoomDatabase() {
    abstract fun communityPostDao(): CommunityPostDao

    companion object {
        @Volatile
        private var INSTANCE: CommunityDataBase? = null

        fun getDataBase(context: Context): CommunityDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CommunityDataBase::class.java,
                    "community_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}