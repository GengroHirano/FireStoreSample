package com.example.self.edu.firestorechatsample.viewmodel

import android.app.Application
import android.arch.lifecycle.*
import android.content.Context
import com.example.self.edu.firestorechatsample.Const
import com.example.self.edu.firestorechatsample.Result
import com.example.self.edu.firestorechatsample.usecase.EntryUseCase
import io.reactivex.disposables.CompositeDisposable

/**
 *
 */
class EntryViewModel(app: Application) : AndroidViewModel(app), LifecycleObserver {

    val result: MutableLiveData<Result<String>> = MutableLiveData()
    private val useCase: EntryUseCase = EntryUseCase(app.getSharedPreferences(Const.PREFERENCE_NAME, Context.MODE_PRIVATE))
    private val disposeTrigger: CompositeDisposable = CompositeDisposable()

    fun registrationUser(name: String?) {
        name ?: return result.postValue(Result.failure(NullPointerException("Validation Error")))
        useCase.registrationUser(name)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onOwnerResumed() {
        val existName = useCase.existAccounts()
        if (existName != null) {
            result.postValue(Result.success(existName))
        }
        useCase.subject.subscribe({
            result.postValue(Result.success(it))
        }, {
            result.postValue(Result.failure(it))
        }).also {
            disposeTrigger.add(it)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onOwnerPaused() {
        disposeTrigger.clear()
    }

    override fun onCleared() {
        disposeTrigger.clear()
        super.onCleared()
    }

}