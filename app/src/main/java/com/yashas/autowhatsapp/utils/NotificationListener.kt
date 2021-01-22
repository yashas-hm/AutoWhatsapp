package com.yashas.autowhatsapp.utils

import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class NotificationListener : NotificationListenerService() {

    @Suppress("PrivatePropertyName")
    private val TAG = this.javaClass.simpleName
    lateinit var context: Context
    private var titleData = ""
    private var textData = ""
    private lateinit var statusBarNotification: StatusBarNotification

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val extras = sbn.notification.extras
        if(extras.getString("android.title")!=null){
            titleData = extras.getString("android.title").toString()
        }

        statusBarNotification = sbn

        if(extras.getCharSequence("android.text")!=null){
            textData = extras.getCharSequence("android.text").toString()
        }
        if(packageName=="com.whatsapp"){
            val msg = Intent("Msg")
            msg.putExtra("title", titleData)
            msg.putExtra("text", textData)
            LocalBroadcastManager.getInstance(context).sendBroadcast(msg)
        }
    }
}