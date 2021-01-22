package com.yashas.autowhatsapp.fragments

import android.app.PendingIntent
import androidx.core.app.RemoteInput
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
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yashas.autowhatsapp.R
import com.yashas.autowhatsapp.adapter.NotificationAdapter
import com.yashas.autowhatsapp.database.ReplyEntity
import com.yashas.autowhatsapp.model.Notification
import com.yashas.autowhatsapp.utils.Utils
import java.util.*


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
        val fromDb = Utils.getFromDB(context!!, 5).execute().get() as List<ReplyEntity>
        replyList.addAll(fromDb)
        LocalBroadcastManager.getInstance(context!!).registerReceiver(onNotice, IntentFilter("Msg"))
    }

    fun sendMessage() {
        val  notificationWear = notificationList[notificationList.lastIndex]
        val remoteInputs =
            arrayOfNulls<RemoteInput>(notificationWear.remoteInputs.size)

        val localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val localBundle: Bundle = notificationWear.bundle!!
        for ((i, remoteIn) in notificationWear.remoteInputs.withIndex()) {
            remoteInputs[i] = remoteIn
            localBundle.putCharSequence(
                remoteInputs[i]!!.resultKey,
                "Our answer"
            )
        }
        RemoteInput.addResultsToIntent(
            remoteInputs,
            localIntent,
            localBundle
        )
        try {
            notificationWear.pendingIntent!!.send(context, 0, localIntent)
        } catch (e: PendingIntent.CanceledException) {}
    }

//    private fun getDetailsOfNotification(remoteInput: RemoteInput) {
//        val resultKey = remoteInput.resultKey
//        val label = remoteInput.label.toString()
//        val canFreeForm = remoteInput.allowFreeFormInput
//        if (remoteInput.choices != null && remoteInput.choices.isNotEmpty()) {
//            val possibleChoices = arrayOfNulls<String>(remoteInput.choices.size)
//            for (i in remoteInput.choices.indices) {
//                possibleChoices[i] = remoteInput.choices[i].toString()
//            }
//        }
//    }


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

            val remoteInputs = arrayListOf<RemoteInput>()
            val wearableExtender: NotificationCompat.WearableExtender =
                NotificationCompat.WearableExtender(statusBarNotification.notification)
            val actions: List<NotificationCompat.Action> = wearableExtender.actions
            for(data in actions){
                if(data.remoteInputs!=null){
                    remoteInputs.addAll(data.remoteInputs)
                }
            }
            val pendingIntent = statusBarNotification.notification.contentIntent
            val bundle = statusBarNotification.notification.extras

            val notification = Notification(titleData, textData, "", remoteInputs, bundle, pendingIntent)
            notificationList.add(notification)
            checkNotifications()
            adapter.updateData(notification)
            sendMessage()
        }
    }

}