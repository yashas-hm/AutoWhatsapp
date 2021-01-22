package com.yashas.autowhatsapp.model

data class Notification(
    var sender: String,
    var message: String,
    var reply:String = ""
)
