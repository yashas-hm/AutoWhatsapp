package com.yashas.autowhatsapp.fragments

import android.app.NotificationManager
import android.content.*
import android.os.Bundle
import android.provider.Settings
import android.service.notification.StatusBarNotification
import android.view.*
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
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
    private var switch: MenuItem? = null
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
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.switch_menu, menu)
        switch = menu.findItem(R.id.switch_)
    }

    private fun checkSwitch(){
        val notificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(notificationManager.isNotificationPolicyAccessGranted){
            switch?.icon?.setTint(ResourcesCompat.getColor(resources, R.color.green, null))
        }else{
            switch?.icon?.setTint(ResourcesCompat.getColor(resources, R.color.red, null))
        }
    }

    override fun onResume() {
        super.onResume()
        checkSwitch()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.switch_->{
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        }
        return super.onOptionsItemSelected(item)
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

            replyList = Utils.update(context)

            for(reply in replyList){
                if(textData.equals(reply.msg, ignoreCase = true)){
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