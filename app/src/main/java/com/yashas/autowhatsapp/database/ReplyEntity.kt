package com.yashas.autowhatsapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Replies")
data class ReplyEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    @ColumnInfo(name = "msg")
    var msg: String,
    @ColumnInfo(name = "reply")
    var reply: String
){
    constructor(reply: String, msg: String): this(id=null, msg = msg, reply = reply)
}