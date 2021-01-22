package com.yashas.autowhatsapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReplyDao {
    @Insert
    fun insertReply(reply: ReplyEntity)

    @Delete
    fun deleteReply(reply: ReplyEntity)

    @Query("DELETE FROM Replies")
    fun deleteAll()

    @Query("SELECT * FROM Replies")
    fun getAllReplies(): List<ReplyEntity>

    @Query("UPDATE Replies SET msg = :msg, reply = :reply where id = :id")
    fun modify(id: Long, msg: String, reply: String)
}