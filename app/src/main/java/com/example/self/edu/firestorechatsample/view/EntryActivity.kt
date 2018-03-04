package com.example.self.edu.firestorechatsample.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.self.edu.firestorechatsample.R
import com.example.self.edu.firestorechatsample.Result
import com.example.self.edu.firestorechatsample.viewmodel.EntryViewModel
import kotlinx.android.synthetic.main.activity_entory.*
import kotlinx.android.synthetic.main.activity_entory.view.*

class EntryActivity : AppCompatActivity() {

    lateinit var viewModel: EntryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entory)

        viewModel = ViewModelProviders.of(this,
                ViewModelProvider.AndroidViewModelFactory(application))
                .get(EntryViewModel::class.java)
        lifecycle.addObserver(viewModel)
        send.setOnClickListener {
            viewModel.registrationUser(entry.text.toString())
        }

        viewModel.result.observe(this, Observer {
            it ?: return@Observer
            when (it) {
                is Result.Success -> {
                    startActivity(ChatActivity.createIntent(this))
                }
                is Result.Failure -> {
                    Toast.makeText(this, "User Registration Error", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

}
