package com.yashas.autowhatsapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.yashas.autowhatsapp.R
import com.yashas.autowhatsapp.adapter.ReplyAdapter
import com.yashas.autowhatsapp.database.ReplyEntity
import com.yashas.autowhatsapp.utils.Utils

class CustomReplyFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var adapter: ReplyAdapter
    private lateinit var addButton: ExtendedFloatingActionButton
    private var replyList = arrayListOf<ReplyEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_custom_reply, container, false)
        setup(view)
        return view
    }

    private fun setup(view: View) {
        initUI(view)
        setupRecycler()
        listener()
    }

    private fun initUI(view: View) {
        recyclerView = view.findViewById(R.id.recycler)
        layoutManager = LinearLayoutManager(context)
        addButton = view.findViewById(R.id.add)
        replyList.addAll(Utils.dummyReplies)
        val fromDb = Utils.getFromDB(context!!, 5).execute().get() as List<ReplyEntity>
        replyList.addAll(fromDb)
    }

    private fun setupRecycler() {
        adapter = ReplyAdapter(context!!, replyList)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun listener() {
        addButton.setOnClickListener {
            val inflater =
                context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val nullParent: ViewGroup? = null
            val options = inflater.inflate(R.layout.reply_popup, nullParent)
            val mPopupWindow = PopupWindow(
                options,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            val msgEt: AppCompatEditText = options.findViewById(R.id.msgEt)
            val replyEt: AppCompatEditText = options.findViewById(R.id.replyEt)
            val cancel: AppCompatButton = options.findViewById(R.id.cancel)
            val save: AppCompatButton = options.findViewById(R.id.save)

            save.setOnClickListener {
                val newReply = replyEt.text.toString()
                val newMsg = msgEt.text.toString()
                when {
                    newReply.isEmpty() -> {
                        replyEt.error = "Empty"
                    }
                    newMsg.isEmpty() -> {
                        msgEt.error = "Empty"
                    }
                    else -> {
                        val modifiedReply = ReplyEntity(newReply, newMsg)
                        Utils.getFromDB(context!!, 3, modifiedReply).execute().get()
                        adapter.updateRecycler(modifiedReply, 2)
                    }
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
    }
}