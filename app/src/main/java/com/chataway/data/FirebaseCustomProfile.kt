package com.chataway.data

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCustomProfile(private val db: FirebaseFirestore) {

    // add new user to the list of registered user for this app
    fun addUser(user: FirebaseUser){

    }

    // commit changes
    fun commit() {
    }
}