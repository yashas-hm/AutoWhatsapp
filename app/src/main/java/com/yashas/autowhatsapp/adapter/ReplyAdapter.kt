package com.yashas.autowhatsapp.adapter

import android.content.Context
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.yashas.autowhatsapp.R
import com.yashas.autowhatsapp.database.ReplyEntity
import com.yashas.autowhatsapp.utils.NotificationListener
import com.yashas.autowhatsapp.utils.Utils

class ReplyAdapter(val context: Context, private var list: ArrayList<ReplyEntity>): RecyclerView.Adapter<ReplyAdapter.ReplyAdapterViewHolder>() {
    class ReplyAdapterViewHolder(view: View): RecyclerView.ViewHolder(view){
        val message: AppCompatTextView = view.findViewById(R.id.msg)
        val reply: AppCompatTextView = view.findViewById(R.id.reply)
        val edit: AppCompatImageButton = view.findViewById(R.id.edit)
        val delete: AppCompatImageButton = view.findViewById(R.id.delete)
        val layout: LinearLayout = view.findViewById(R.id.layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyAdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.reply_item, parent, false)
        return ReplyAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReplyAdapterViewHolder, position: Int) {
        val reply = list[position]
        holder.message.text = context.getString(R.string.msg, reply.msg)
        holder.reply.text = context.getString(R.string.reply, reply.reply)

        if(reply in Utils.dummyReplies){
            holder.layout.visibility = View.GONE
        }

        holder.edit.setOnClickListener {
            val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val nullParent: ViewGroup? = null
            val options = inflater.inflate(R.layout.reply_popup, nullParent)
            val mPopupWindow = PopupWindow(
                options,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )

            val msgEt: AppCompatEditText = options.findViewById(R.id.msgEt)
            val replyEt: AppCompatEditText = options.findViewById(R.id.replyEt)
            val cancel: AppCompatButton = options.findViewById(R.id.cancel)
            val save:AppCompatButton = options.findViewById(R.id.save)

            msgEt.setText(reply.msg)
            replyEt.setText(reply.reply)

            save.setOnClickListener {
                val newReply = replyEt.text.toString()
                val newMsg = msgEt.text.toString()

                if(reply.msg !=newMsg || reply.reply != newReply){
                    when {
                        newReply.isEmpty() -> {
                            replyEt.error = "Empty"
                        }
                        newMsg.isEmpty() -> {
                            msgEt.error = "Empty"
                        }
                        else -> {
                            val modifiedReply = ReplyEntity(reply.id, newMsg, newReply)
                            Utils.GetFromDB(context, 3, modifiedReply).execute().get()
                            updateRecycler(modifiedReply, 3, position)
                            mPopupWindow.dismiss()
                        }
                    }
                }else{
                    mPopupWindow.dismiss()
                }
            }

            cancel.setOnClickListener {
                mPopupWindow.dismiss()
            }

            mPopupWindow.elevation = 5.0f
            mPopupWindow.isFocusable = true
            mPopupWindow.isOutsideTouchable = true
            mPopupWindow.showAtLocation(options, Gravity.CENTER, 0, 0)
        }
        holder.delete.setOnClickListener {
            Utils.GetFromDB(context, 2, reply).execute().get()
            notifyItemRemoved(position)
            updateRecycler(reply,1)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateRecycler(replyEntity: ReplyEntity, mode:Int, position: Int? = null){
        when(mode){
            1->{
                list.remove(replyEntity)
                notifyDataSetChanged()
            }
            2->{
                list.add(replyEntity)
                notifyDataSetChanged()
            }
            3->{
                list.removeAt(position!!)
                list.add(replyEntity)
                notifyDataSetChanged()
            }
        }
    }

}