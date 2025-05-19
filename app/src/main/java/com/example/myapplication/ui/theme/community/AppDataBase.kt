package com.example.myapplication.ui.theme.community

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplication.ui.theme.dataclass.CommentEntity

@Database (entities = [CommentEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    abstract fun commentDao(): CommentDao
}