package com.example.self.edu.firestorechatsample.entity

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

/**
 *
 */
@IgnoreExtraProperties
data class User(
        var name: String,
        @get:Exclude var id: String? = null)