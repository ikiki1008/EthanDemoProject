package com.example.myapplication.dataclass

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.dataclass.CommentDao

@Database(entities = [CommentEntity::class], version = 2)
abstract class AppDataBase : RoomDatabase() {
    abstract fun commentDao(): CommentDao
}