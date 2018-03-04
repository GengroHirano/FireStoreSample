package com.example.self.edu.firestorechatsample.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.util.Log
import com.example.self.edu.firestorechatsample.Const
import com.example.self.edu.firestorechatsample.usecase.ChatUseCase
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 *
 */
class ChatViewModel(app: Application) : AndroidViewModel(app) {

    private val useCase: ChatUseCase = ChatUseCase(app.getSharedPreferences(Const.PREFERENCE_NAME, Context.MODE_PRIVATE))
    private val disposeTrriger: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        disposeTrriger.clear()
        super.onCleared()
    }

    fun sendMessage(message: String?) {
        useCase.send(message)
    }

    fun subscribeChat() {
        useCase.subscribeChat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    disposeTrriger.add(it)
                }
                .subscribe({
                    Log.v("chat", "list $it")
                }, {
                    it.printStackTrace()
                })
    }

}