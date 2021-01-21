package com.yashas.autowhatsapp

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

        if(extras.getCharSequence("android.text")!=null){
            textData = extras.getCharSequence("android.text").toString()
        }

        val msg = Intent("Msg")
        msg.putExtra("package", packageName)
        msg.putExtra("title", titleData)
        msg.putExtra("text", textData)
        LocalBroadcastManager.getInstance(context).sendBroadcast(msg)
    }
}