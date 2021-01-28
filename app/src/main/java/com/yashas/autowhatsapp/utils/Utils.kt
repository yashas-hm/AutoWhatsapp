package com.yashas.autowhatsapp.utils

import androidx.core.app.RemoteInput
import android.content.Context
import android.os.AsyncTask
import android.provider.ContactsContract
import android.widget.Toast
import androidx.room.Room
import com.yashas.autowhatsapp.database.ReplyDatabase
import com.yashas.autowhatsapp.database.ReplyEntity

object Utils {
    fun getContactFromName(context: Context, name: String): String{
        val resolver = context.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts.DISPLAY_NAME+" = "+name, null, null)
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
        ReplyEntity( "hello", "Hi"),
        ReplyEntity("Fine", "how are you"),
        ReplyEntity("fine", "how r u"),
        ReplyEntity("nothing much bored", "supp")
    )

    class getFromDB(val context: Context, private val mode: Int, val reply: ReplyEntity? = null): AsyncTask<Void, Void, Any>(){
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
                1->{
                    return db.replyDao().insertReply(reply!!)
                }
                2->{
                    return db.replyDao().deleteReply(reply!!)
                }
                3->{
                    return db.replyDao().modify(reply!!.id, reply.msg, reply.reply)
                }
                4->{
                    return db.replyDao().deleteAll()
                }
                5-> {
                    return db.replyDao().getAllReplies()
                }
            }
            return ""
        }

    }

}