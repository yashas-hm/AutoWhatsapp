package com.yashas.autowhatsapp.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.yashas.autowhatsapp.database.ReplyEntity
import java.util.*


class NotificationListener : NotificationListenerService() {

    @Suppress("PrivatePropertyName")
    private val TAG = this.javaClass.simpleName
    lateinit var context: Context
    private var textData = ""
    private var replyList = arrayListOf<ReplyEntity>()

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        replyList.addAll(Utils.dummyReplies)
        val fromDb = Utils.getFromDB(context, 5).execute().get() as List<ReplyEntity>
        replyList.addAll(fromDb)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val extras = sbn.notification.extras
        if(extras.getCharSequence("android.text")!=null){
            textData = extras.getCharSequence("android.text").toString()
        }
        if(!sbn.isOngoing){
            if(packageName=="com.whatsapp"&&!textData.contains("new messages")){
                val msg = Intent("Msg")
                msg.putExtra("notification", sbn)
                LocalBroadcastManager.getInstance(context).sendBroadcast(msg)
                for(reply in replyList){
                    if(textData==reply.msg){
                        sendMessage(sbn, reply.reply, context)
                        break
                    }
                }
            }
        }
    }

    private fun sendMessage(sbn: StatusBarNotification, reply: String, context: Context) {
        val wearableExtender = NotificationCompat.WearableExtender(sbn.notification)
        val wearableActions : List<NotificationCompat.Action> = wearableExtender.actions
        val remoteInputs = arrayListOf<RemoteInput>()
        val remotes = arrayListOf<RemoteInput>()
        val bundle = Bundle()
        val pendingIntent = sbn.notification.contentIntent
        val intent = Intent()


        for(action in wearableActions){
            if(action.remoteInputs!=null){
                remoteInputs.addAll(action.remoteInputs)
            }
        }

//        val actualInputs = ArrayList<RemoteInput>()
//
//        for (input in remoteInputs) {
//            bundle.putCharSequence(input.resultKey, reply)
//            val builder = RemoteInput.Builder(input.resultKey)
//            builder.setLabel(input.label)
//            builder.setChoices(input.choices)
//            builder.addExtras(input.extras)
//            actualInputs.add(builder.build())
//        }
//
//        val inputs = actualInputs.toTypedArray()
//        RemoteInput.addResultsToIntent(inputs, intent, bundle)
//        try{
//            pendingIntent.send(context, 0, intent)
//        }catch (e: PendingIntent.CanceledException){
//            println(e)
//            Toast.makeText(context, "Some error occurred", Toast.LENGTH_LONG).show()
//        }
//        println("Working")

        for(inputs in remoteInputs){
            bundle.putCharSequence(inputs.resultKey, reply)
            val builder = RemoteInput.Builder(inputs.resultKey)
                .setLabel(inputs.label)
                .setChoices(inputs.choices)
                .setAllowFreeFormInput(inputs.allowFreeFormInput)
                .addExtras(inputs.extras)
            remotes.add(builder.build())
        }

        val inputs = remotes.toTypedArray()

        RemoteInput.addResultsToIntent(inputs, intent, bundle)
        try{
            pendingIntent.send(context, 0, intent)
            println("worked")
        }catch (e: PendingIntent.CanceledException){
            println(e)
            Toast.makeText(context, "Some error occurred", Toast.LENGTH_LONG).show()
        }
    }
}