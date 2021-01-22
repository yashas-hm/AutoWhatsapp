package com.yashas.autowhatsapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.yashas.autowhatsapp.R
import com.yashas.autowhatsapp.model.Notification

class NotificationAdapter(private val context: Context, private val list: ArrayList<Notification>): RecyclerView.Adapter<NotificationAdapter.NotificationAdapterViewHolder>() {
    class NotificationAdapterViewHolder(view: View): RecyclerView.ViewHolder(view){
        val sender: AppCompatTextView = view.findViewById(R.id.sender)
        val message: AppCompatTextView = view.findViewById(R.id.msg)
        val reply: AppCompatTextView = view.findViewById(R.id.reply)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return NotificationAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationAdapterViewHolder, position: Int) {
        val reply = list[position]
        holder.sender.text = context.getString(R.string.msgBy, reply.sender)
        holder.message.text = context.getString(R.string.msg, reply.message)
        if(reply.reply==""){
            holder.reply.text = context.getString(R.string.autoReply)
        }else{
            holder.reply.text = context.getString(R.string.reply, reply.reply)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateData(notification: Notification){
        list.add(notification)
        notifyDataSetChanged()
    }
}