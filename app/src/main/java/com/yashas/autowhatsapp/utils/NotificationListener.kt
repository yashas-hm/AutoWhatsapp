package com.yashas.autowhatsapp.utils

import androidx.core.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.*


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
        val ri = arrayListOf<RemoteInput>()
        var v: RemoteInput
        val wearableExtender: NotificationCompat.WearableExtender =
            NotificationCompat.WearableExtender(sbn.notification)
        val actions: List<NotificationCompat.Action> = wearableExtender.actions
        for (act in actions) {
            if (act.remoteInputs != null) {
                ri.addAll(act.remoteInputs)

            }

        }
        if(packageName=="com.whatsapp"){
            val msg = Intent("Msg")
            msg.putExtra("notification", sbn)
            LocalBroadcastManager.getInstance(context).sendBroadcast(msg)
        }

    }

//    /**
//     * To extract WearNotification with RemoteInputs that we can use to respond later on
//     * @param statusBarNotification
//     * @return
//     */
//    private fun extractWearNotification(statusBarNotification: StatusBarNotification): NotificationWear {
//        //Should work for communicators such:"com.whatsapp", "com.facebook.orca", "com.google.android.talk", "jp.naver.line.android", "org.telegram.messenger"
//        val notificationWear = NotificationWear()
//        notificationWear.packageName = statusBarNotification.packageName
//        val wearableExtender: NotificationCompat.WearableExtender =
//            NotificationCompat.WearableExtender(statusBarNotification.notification)
//        val actions: List<NotificationCompat.Action> = wearableExtender.actions
//        for (act in actions) {
//            if (act.remoteInputs != null) {
//                notificationWear.remoteInputs.addAll(act.remoteInputs)
//            }
//        }
//        val pages: List<Notification> = wearableExtender.pages
//        notificationWear.pages.addAll(pages)
//        notificationWear.bundle = statusBarNotification.notification.extras
//        notificationWear.tag =
//            statusBarNotification.tag //TODO find how to pass Tag with sending PendingIntent, might fix Hangout problem
//        notificationWear.pendingIntent = statusBarNotification.notification.contentIntent
//        return notificationWear
//    }
//
//    /**
//     * Sample of how it is possible to do manually without using NotificationCompat.WearableExtender constructor
//     * @param statusBarNotification
//     * @return
//     */
//    private fun extractOldWearNotification(statusBarNotification: StatusBarNotification): NotificationWear {
//        //Should work for communicators such:"com.whatsapp", "com.facebook.orca", "com.google.android.talk", "jp.naver.line.android", "org.telegram.messenger"
//        val notificationWear = NotificationWear()
//        val bundle = statusBarNotification.notification.extras
//        for (key in bundle.keySet()) {
//            val value = bundle[key]
//            if ("android.wearable.EXTENSIONS" == key) {
//                val wearBundle = value as Bundle?
//                for (keyInner in wearBundle!!.keySet()) {
//                    val valueInner = wearBundle[keyInner]
//                    if (keyInner != null && valueInner != null) {
//                        if ("actions" == keyInner && valueInner is ArrayList<*>) {
//                            val actions= arrayListOf<Notification.Action>()
//                            actions.addAll(valueInner as Collection<Notification.Action>)
//                            for (act in actions) {
//                                if (act.remoteInputs != null) { //API > 20 needed
//                                    val remoteInputs: Array<RemoteInput> = act.remoteInputs
//                                }
//                            }
//                            //get remote inputs and save them to notificationWear... long spaghetti code
//                        }
//                    }
//                }
//            }
//        }
//        return notificationWear
//    }

}