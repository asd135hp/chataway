package com.chataway.data

import com.chataway.data.model.UserProfile
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference

class FirebaseCustomOperations(private val db: DatabaseReference) {
    companion object{
        private const val DB_ENTRY_KEY = "users"
        private const val DB_CHAT_LOG_KEY = "chat_log"
    }

    private fun getDatabase(uid: String): DatabaseReference
        = db.child(DB_ENTRY_KEY).child(uid)

    private fun getChatLogDatabase(uid: String, targetUID: String): DatabaseReference
        = getDatabase(uid).child(DB_CHAT_LOG_KEY).child(targetUID)

    // add new user to the list of registered user for this app
    fun addUser(user: FirebaseUser, completionHandler: DatabaseReference.CompletionListener?)
        = getDatabase(user.uid).setValue(
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
        = getDatabase(userUID).updateChildren(newProfile.toMap(), completionHandler)

    // get all user data from the database
    fun getUser(userUID: String, onTaskCompleteHandler: OnCompleteListener<DataSnapshot>)
        = getDatabase(userUID).get().addOnCompleteListener(onTaskCompleteHandler)

    // delete user when the session is finished (guest)
    fun deleteUser(userUID: String, completionHandler: DatabaseReference.CompletionListener?)
        = getDatabase(userUID).removeValue(completionHandler)

    //
    fun updateChatLog(userUID: String, targetUID: String, messages: List<String>): Boolean {
        var encryptedLog = Encryption.encryptChatLog(userUID, messages)
        if(encryptedLog == null){
            encryptedLog = Encryption.encryptChatLog(userUID, messages)
            if(encryptedLog == null) return false
        }

        // set the encrypted log to the database
        getChatLogDatabase(userUID, targetUID).setValue(encryptedLog)
        return true
    }

    //
    fun setRealtimeTyping(userUID: String, targetUID: String, isTyping: Boolean): Boolean {
        var isSucceeded = true
        getChatLogDatabase(userUID, targetUID)
            .updateChildren(mapOf("is_typing" to isTyping))
            .addOnFailureListener { isSucceeded = false }
        return isSucceeded
    }

    // an empty chat log upon failure
    fun getChatLog(userUID: String, targetUID: String): List<String> {
        var chatLog: List<String> = listOf()
        getChatLogDatabase(userUID, targetUID)
            .get()
            .addOnCompleteListener { result ->
                if(result.isSuccessful){
                    val data = result.result?.getValue(String::class.java)
                    if(data != null) {
                        val decryptedChatLog = Encryption.decryptChatLog(userUID, data)
                        if(decryptedChatLog != null) chatLog = decryptedChatLog
                    }
                }
            }
        return chatLog
    }
}