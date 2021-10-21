package com.chataway.ui.chatMessages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chataway.data.ChatLog
import com.chataway.data.FirebaseCustomOperations
import com.chataway.data.model.ChatMessage
import com.google.firebase.database.DatabaseReference

class ChatMessagesViewModel(db: DatabaseReference,
                            private val userUUID: String,
                            private val targetUUID: String,
                            private val emptyChatLog: ChatLog
): ViewModel() {

    private val firebaseOps = FirebaseCustomOperations(db)
    private val _chatMessages: MutableLiveData<List<ChatMessage>> by lazy {
        firebaseOps.getChatLog(userUUID, targetUUID, emptyChatLog)
        MutableLiveData(emptyChatLog.chatMessages)
    }
    val chatMessages: LiveData<List<ChatMessage>> = _chatMessages
}