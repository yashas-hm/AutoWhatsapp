package com.yashas.autowhatsapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ReplyEntity::class], version = 1)
abstract class ReplyDatabase: RoomDatabase() {
    abstract fun replyDao(): ReplyDao
}