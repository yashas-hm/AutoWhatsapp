package com.yashas.autowhatsapp.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.service.notification.StatusBarNotification
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yashas.autowhatsapp.R
import com.yashas.autowhatsapp.adapter.NotificationAdapter
import com.yashas.autowhatsapp.database.ReplyEntity
import com.yashas.autowhatsapp.model.Notification
import com.yashas.autowhatsapp.utils.Utils


class NotificationRepliedFragment : Fragment(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: NotificationAdapter
    private lateinit var text: AppCompatTextView
    private var notificationList = arrayListOf<Notification>()
    private var replyList = arrayListOf<ReplyEntity>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_notification_replied, container, false)
        setup(view)
        return view
    }

    private fun setup(view: View){
        initUI(view)
        setupRecycler()
        checkNotifications()
    }

    private fun setupRecycler(){
        adapter = NotificationAdapter(context!!, notificationList)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun checkNotifications(){
        if(notificationList.isEmpty()){
            text.visibility = View.VISIBLE
        }else{
            text.visibility = View.GONE
        }
    }

    private fun initUI(view: View){
        recyclerView = view.findViewById(R.id.recycler)
        layoutManager = LinearLayoutManager(context)
        text = view.findViewById(R.id.noNotification)
        replyList.addAll(Utils.dummyReplies)
        val fromDb = Utils.GetFromDB(context!!, 5).execute().get() as List<ReplyEntity>
        replyList.addAll(fromDb)
        LocalBroadcastManager.getInstance(context!!).registerReceiver(onNotice, IntentFilter("Msg"))
    }

    private val onNotice: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val statusBarNotification = intent.getParcelableExtra<StatusBarNotification>("notification")
            val extras = statusBarNotification!!.notification.extras
            var titleData = ""
            var textData = ""

            if(extras.getString("android.title")!=null){
                titleData = extras.getString("android.title").toString()
            }
            if(extras.getCharSequence("android.text")!=null){
                textData = extras.getCharSequence("android.text").toString()
            }
            var notification = Notification(titleData, textData, "")
            for(reply in replyList){
                if(textData==reply.msg){
                    notification = Notification(titleData, textData, reply.reply)
                    break
                }
            }
            checkNotifications()
            if(!notificationList.contains(notification)){
                adapter.updateData(notification)
            }
        }
    }

}