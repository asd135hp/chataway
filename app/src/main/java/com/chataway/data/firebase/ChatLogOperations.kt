package com.chataway.data.firebase

import com.chataway.data.ChatLog
import com.chataway.data.FirebaseConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object ChatLogOperations {
    //
    suspend fun updateChatLog(
        userUID: String,
        targetUID: String,
        chatLog: ChatLog
    ): Boolean = withContext(Dispatchers.Default) {
        // set the encrypted log to the database
        Operations.getChatLogDatabase(userUID, targetUID)
            .child(FirebaseConstants.USER_CHAT_LOG_CONTENT)
            .setValue(chatLog.encryptChatLog())
        true
    }

    //
    fun setRealtimeTyping(userUID: String, targetUID: String, isTyping: Boolean): Boolean {
        var isSucceeded = true
        Operations.getChatLogDatabase(userUID, targetUID)
            .child(FirebaseConstants.USER_IS_TYPING)
            .setValue(isTyping)
            .addOnFailureListener { isSucceeded = false }
        return isSucceeded
    }

    // an empty chat log upon failure
    fun getChatLog(userUID: String, targetUID: String, chatLog: ChatLog) {
        Operations.getChatLogDatabase(userUID, targetUID)
            .child(FirebaseConstants.USER_CHAT_LOG_CONTENT)
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val snapshot = it.result
                    if(snapshot != null) runBlocking { chatLog.decryptChatLog(snapshot) }
                    else chatLog.clear()
                }
            }.result
    }
}