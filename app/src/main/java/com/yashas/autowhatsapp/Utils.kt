package com.yashas.autowhatsapp

import android.content.Context
import android.provider.ContactsContract
import android.widget.Toast

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
}