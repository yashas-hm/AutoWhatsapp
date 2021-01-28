package com.yashas.autowhatsapp.utils

import android.app.Notification
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.provider.ContactsContract
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.yashas.autowhatsapp.database.ReplyDatabase
import com.yashas.autowhatsapp.database.ReplyEntity
import com.yashas.autowhatsapp.model.Action

object Utils {
    private val REPLY_KEYWORDS = arrayOf("reply", "android.intent.extra.text")

    fun getContactFromName(context: Context, name: String): String{
        val resolver = context.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            ContactsContract.Contacts.DISPLAY_NAME + " = " + name,
            null,
            null
        )
        if(resolver!!.count>0){
            while (resolver.moveToNext()){
                return resolver.getString(resolver.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
            }
        }else{
            Toast.makeText(context, "No contacts found", Toast.LENGTH_LONG).show()
        }
        return ""
    }

    val dummyReplies: List<ReplyEntity> = arrayListOf(
        ReplyEntity("hello", "Hi"),
        ReplyEntity("Fine", "how are you"),
        ReplyEntity("fine", "how r u"),
        ReplyEntity("nothing much bored", "supp")
    )

    fun getQuickReplyAction(n: Notification, packageName: String?): Action? {
        var action: NotificationCompat.Action? = null
        if (Build.VERSION.SDK_INT >= 24) action = getQuickReplyAction(n)
        if (action == null) action = getWearReplyAction(n)
        return if (action == null) null else Action(action, packageName, true)
    }

    private fun getQuickReplyAction(n: Notification): NotificationCompat.Action? {
        for (i in 0 until NotificationCompat.getActionCount(n)) {
            val action = NotificationCompat.getAction(n, i)
            if (action.remoteInputs != null) {
                for (x in action.remoteInputs.indices) {
                    val remoteInput = action.remoteInputs[x]
                    if(isKnownReplyKey(remoteInput.resultKey))
                        return action
                }
            }
        }
        return null
    }

    private fun getWearReplyAction(n: Notification): NotificationCompat.Action? {
        val wearableExtender = NotificationCompat.WearableExtender(n)
        for (action in wearableExtender.actions) {
            if (action.remoteInputs != null) {
                for (x in action.remoteInputs.indices) {
                    val remoteInput = action.remoteInputs[x]
                    if(isKnownReplyKey(remoteInput.resultKey))
                        return action
                }
            }
        }
        return null
    }

    private fun isKnownReplyKey(resultKey: String): Boolean {
        var resultKey = resultKey
        if (TextUtils.isEmpty(resultKey)) return false
        resultKey = resultKey.toLowerCase()
        for (keyword in REPLY_KEYWORDS){
            if (resultKey.contains(keyword))
                return true
        }
        return false
    }

    class GetFromDB(val context: Context, private val mode: Int, val reply: ReplyEntity? = null): AsyncTask<Void, Void, Any>(){
        /*
               1-> INSERT into DB
               2-> DELETE from DB
               3-> MODIFY in DB
               4-> DELETE whole DB
               5-> RETURN whole DB
         */

        override fun doInBackground(vararg p0: Void?): Any {
            val db = Room.databaseBuilder(context, ReplyDatabase::class.java, "Replies").build()
            when(mode){
                1 -> {
                    return db.replyDao().insertReply(reply!!)
                }
                2 -> {
                    return db.replyDao().deleteReply(reply!!)
                }
                3 -> {
                    return db.replyDao().modify(reply!!.id, reply.msg, reply.reply)
                }
                4 -> {
                    return db.replyDao().deleteAll()
                }
                5 -> {
                    return db.replyDao().getAllReplies()
                }
            }
            return ""
        }

    }

}