package com.example.self.edu.firestorechatsample.view

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.self.edu.firestorechatsample.R
import com.example.self.edu.firestorechatsample.viewmodel.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.view.*

/**
 *
 */
class ChatActivity : AppCompatActivity() {

    lateinit var viewModel: ChatViewModel

    companion object {
        fun createIntent(context: Context) = Intent(context, ChatActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        viewModel = ViewModelProviders.of(this,
                ViewModelProvider.AndroidViewModelFactory(application))
                .get(ChatViewModel::class.java)

        send.setOnClickListener {
            viewModel.sendMessage(message.text.toString())
        }

        viewModel.subscribeChat()
    }

}