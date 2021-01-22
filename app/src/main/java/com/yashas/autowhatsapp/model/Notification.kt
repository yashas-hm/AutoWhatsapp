package com.yashas.autowhatsapp.model

import android.app.PendingIntent
import android.os.Bundle
import androidx.core.app.RemoteInput
import java.io.Serializable

data class Notification(
    var sender: String,
    var message: String,
    var reply:String = "",
    var remoteInputs: ArrayList<RemoteInput> = arrayListOf(),
    var bundle: Bundle? = null,
    var pendingIntent: PendingIntent? = null
): Serializable
