package com.example.self.edu.firestorechatsample.usecase

import android.content.SharedPreferences
import android.util.Log
import com.example.self.edu.firestorechatsample.entity.Chat
import com.example.self.edu.firestorechatsample.entity.User
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.*
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 *
 */
class ChatUseCase(private val preferences: SharedPreferences) {

    private val fire: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var chat: MutableList<Chat> = mutableListOf()

    fun send(message: String?) {
        val userId = preferences.getString("user", "null")
        userId ?: throw IllegalStateException("WTF!?")

        val chat = Chat(message ?: "", fire.document("user/${userId}"), Date(System.currentTimeMillis()))
        fire.collection("chats/chat/message").add(chat)
                .addOnSuccessListener {
                    Log.v("send", "send success")
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    Log.v("send", "send failure")
                }
    }

    fun subscribeChat(): Observable<List<Chat>> {
        return Observable
                .create<QuerySnapshot> { emitter ->
                    fire.collection("chats/chat/message")
                            .limit(30)
                            .orderBy("timestamp")
                            .addSnapshotListener { snapShot, firestoreException ->
                                firestoreException?.let {
                                    it.printStackTrace()
                                    emitter.onError(it)
                                }
                                emitter.onNext(snapShot)
                            }
                            .also {
                                emitter.setCancellable {
                                    it.remove()
                                }
                            }
                }
                .flatMap {
                    convertChat(it)
                }
    }

    private fun convertChat(snapshot: QuerySnapshot): Observable<List<Chat>> {
        return Observable.create<List<Chat>> { emitter ->
            val chatSnapshot = chat.toMutableList()
            if (chatSnapshot.isEmpty()) {
                chatSnapshot.addAll(snapshot.documents.map {
                    val chat = Chat(it.getString("message"), it.getDocumentReference("userRef"), it.getDate("timestamp"))
                    chat.user = UserFuture(chat.userRef).get()
                    return@map chat
                })
                emitter.onNext(chatSnapshot)
                return@create
            } else {
                snapshot.documentChanges.forEach {
                    val document = it.document
                    when (it.type) {
                        DocumentChange.Type.ADDED -> {
                            val chat = Chat(document.getString("message"), document.getDocumentReference("userRef"), document.getDate("timestamp"))
                            chat.user = UserFuture(chat.userRef).get()
                            chatSnapshot.add(it.newIndex, chat)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            if (it.oldIndex == it.newIndex) {
                                val chat = Chat(document.getString("message"), document.getDocumentReference("userRef"), document.getDate("timestamp"))
                                chat.user = UserFuture(chat.userRef).get()
                                chatSnapshot[it.oldIndex] = chat
                            } else {
                                chatSnapshot.removeAt(it.oldIndex)
                                val chat = Chat(document.getString("message"), document.getDocumentReference("userRef"), document.getDate("timestamp"))
                                chat.user = UserFuture(chat.userRef).get()
                                chatSnapshot.add(it.newIndex, chat)
                            }
                        }
                        DocumentChange.Type.REMOVED -> {
                            chatSnapshot.removeAt(it.oldIndex)
                        }
                    }
                }
            }
            chat = chatSnapshot
            emitter.onNext(chat)
        }
    }

    class UserFuture(userRef: DocumentReference) : Future<User?> {

        private var user: User? = null
        private var canceled: Boolean = false

        init {
            userRef.get()
                    .addOnSuccessListener {
                        user = User(it.getString("name"))
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
        }

        override fun get(timeout: Long, unit: TimeUnit): User? {
            Thread.sleep(unit.toMillis(timeout))
            return user
        }

        override fun get(): User? {
            while (!isDone && !canceled) {
                Thread.sleep(100)
            }
            return user
        }

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            if (isDone) {
                return false
            }
            canceled = true
            return true
        }

        override fun isCancelled(): Boolean {
            return canceled
        }

        override fun isDone(): Boolean {
            return user != null
        }
    }

}