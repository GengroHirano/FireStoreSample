package com.example.self.edu.firestorechatsample.usecase

import android.content.SharedPreferences
import com.example.self.edu.firestorechatsample.Const
import com.example.self.edu.firestorechatsample.Result
import com.example.self.edu.firestorechatsample.entity.User
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.subjects.PublishSubject
import java.util.prefs.AbstractPreferences

/**
 *
 */
class EntryUseCase(private val preferences: SharedPreferences) {

    val subject: PublishSubject<String> = PublishSubject.create()
    private val fire: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun existAccounts(): String? {
        return preferences.getString("user", null)
    }

    fun registrationUser(name: String) {
        val user = User(name)
        fire.collection(Const.USER_ROOT).add(user)
                .addOnSuccessListener {
                    preferences.edit().putString("user", it.id).apply()
                    subject.onNext(it.id)
                }
                .addOnFailureListener {
                    subject.onError(it)
                }
    }


}