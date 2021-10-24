package com.chataway.data.firebase

import com.chataway.data.FirebaseConstants
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

typealias ValueListener = (DataSnapshot) -> Unit

object Operations {
    private val db = Firebase.database.reference
    private val storage = Firebase.storage.reference

    fun getUserDatabase(uid: String): DatabaseReference
        = db.child(FirebaseConstants.DB_ENTRY_KEY).child(uid)

    fun getAvatarDatabase(): StorageReference
        = storage.child(FirebaseConstants.DB_AVATAR_KEY)

    fun getChatLogDatabase(uid: String, targetUID: String): DatabaseReference
        = db.child(FirebaseConstants.USER_CHAT_LOG)
            .orderByKey()
            .equalTo("$uid+$targetUID")
            .ref

    fun getUserFriendList(uid: String): DatabaseReference
        = getUserDatabase(uid).child(FirebaseConstants.USER_FRIEND_LIST)

    fun iterateThroughAllUsers(path: String, listener: ValueListener){
        db.child(FirebaseConstants.DB_ENTRY_KEY)
            .child(path)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) = listener(snapshot)
                override fun onCancelled(error: DatabaseError) {}
            })
    }
}