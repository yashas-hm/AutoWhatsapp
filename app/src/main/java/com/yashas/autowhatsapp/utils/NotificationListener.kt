package com.yashas.autowhatsapp.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.yashas.autowhatsapp.database.ReplyEntity
import com.yashas.autowhatsapp.model.Action


class NotificationListener : NotificationListenerService() {

    lateinit var context: Context
    private var textData = ""
    private var titleText = ""
    private var replyList = arrayListOf<ReplyEntity>()

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        replyList.addAll(Utils.dummyReplies)
        val fromDb = Utils.GetFromDB(context, 5).execute().get() as List<ReplyEntity>
        replyList.addAll(fromDb)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val extras = sbn.notification.extras

        val action = Utils.getQuickReplyAction(sbn.notification, this.packageName)

        if(extras.getCharSequence("android.text")!=null){
            textData = extras.getCharSequence("android.text").toString()
        }

        if(extras.getCharSequence("android.title")!=null){
            titleText = extras.getCharSequence("android.title").toString()
        }

        if(!sbn.isOngoing){
            if(packageName=="com.whatsapp"&&!textData.contains("new messages")&&textData != "📷 Photo"&&titleText!="You"){
                replyList = Utils.update(context)
                val msg = Intent("Msg")
                msg.putExtra("notification", sbn)
                LocalBroadcastManager.getInstance(context).sendBroadcast(msg)
                for(reply in replyList){
                    if (textData.equals(reply.msg, ignoreCase = true)){
                        if(action!=null){
                            sendMessage(action, reply.reply, sbn)
                        }
                        break
                    }
                }
            }
        }
    }

    private fun sendMessage(action: Action, reply: String, sbn: StatusBarNotification){
        try {
            this.cancelNotification(sbn.key)
            action.sendReply(context, reply)
        }catch (e: PendingIntent.CanceledException) {
            Toast.makeText(context, "Some error occurred", Toast.LENGTH_LONG).show()
        }
    }
}