package com.yashas.autowhatsapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Replies")
data class ReplyEntity (
    @PrimaryKey
    var id: Long,
    @ColumnInfo(name = "msg")
    var msg: String,
    @ColumnInfo(name = "reply")
    var reply: String
){
    constructor(reply: String, msg: String): this(0, reply, msg)
}