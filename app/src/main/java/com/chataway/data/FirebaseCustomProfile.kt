package com.chataway.data

import com.chataway.data.model.UserProfile
import com.chataway.data.model.FirebaseConstants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

typealias ValueListener = (DataSnapshot) -> Unit

object FirebaseCustomOperations {
    private val db = FirebaseDatabase.getInstance().reference

    private fun getUserDatabase(uid: String): DatabaseReference
        = db.child(FirebaseConstants.DB_ENTRY_KEY).child(uid)

    private fun getChatLogDatabase(uid: String, targetUID: String): DatabaseReference
        = getUserDatabase(uid).child(FirebaseConstants.USER_CHAT_LOG).child(targetUID)

    fun iterateThroughAllUsers(listener: ValueListener){
        db.child(FirebaseConstants.DB_ENTRY_KEY).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) = listener(snapshot)
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // add new user to the list of registered user for this app
    fun addUser(user: FirebaseUser, completionHandler: DatabaseReference.CompletionListener?)
        = getUserDatabase(user.uid).setValue(
            UserProfile(
                user.displayName,
                user.photoUrl,
                user.email
            ).toMap(),
            completionHandler
        )

    // edit the profile of the existing user
    fun editUser(userUID: String,
                 newProfile: UserProfile,
                 completionHandler: DatabaseReference.CompletionListener?)
        = getUserDatabase(userUID).updateChildren(newProfile.toMap(), completionHandler)

    // get all user data from the database
    fun getUser(userUID: String, onTaskCompleteHandler: OnCompleteListener<DataSnapshot>)
        = getUserDatabase(userUID).get().addOnCompleteListener(onTaskCompleteHandler)

    // delete user when the session is finished (guest)
    fun deleteUser(userUID: String, completionHandler: DatabaseReference.CompletionListener?)
        = getUserDatabase(userUID).removeValue(completionHandler)

    //
    suspend fun updateChatLog(userUID: String, targetUID: String, chatLog: ChatLog): Boolean {
        // set the encrypted log to the database
        val encryptedChatLog = withContext(Dispatchers.Default) {
            chatLog.encryptChatLog()
        }
        getChatLogDatabase(userUID, targetUID)
            .updateChildren(
                mapOf(FirebaseConstants.USER_CHAT_LOG_CONTENT to encryptedChatLog)
            )
        return true
    }

    //
    fun setRealtimeTyping(userUID: String, targetUID: String, isTyping: Boolean): Boolean {
        var isSucceeded = true
        getChatLogDatabase(userUID, targetUID)
            .updateChildren(mapOf(FirebaseConstants.USER_IS_TYPING to isTyping))
            .addOnFailureListener { isSucceeded = false }
        return isSucceeded
    }

    // an empty chat log upon failure
    fun getChatLog(userUID: String, targetUID: String, chatLog: ChatLog) {
        getChatLogDatabase(userUID, targetUID)
            .child(FirebaseConstants.USER_CHAT_LOG_CONTENT)
            .get()
            .addOnCompleteListener { result ->
                if(result.isSuccessful){
                    val data = result.result?.getValue(String::class.java)
                    if(data != null) runBlocking { chatLog.decryptChatLog(data) }
                    else chatLog.clear()
                }
            }.result
    }
}