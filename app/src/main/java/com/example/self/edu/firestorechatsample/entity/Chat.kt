package com.example.self.edu.firestorechatsample.entity

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

/**
 *
 */
@IgnoreExtraProperties
class Chat(
        val message: String,
        val userRef: DocumentReference,
        @ServerTimestamp val timestamp: Date,
        @get:Exclude var user: User? = null)