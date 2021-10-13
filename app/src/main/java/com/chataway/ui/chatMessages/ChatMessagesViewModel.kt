package com.chataway.ui.chatMessages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chataway.data.FirebaseCustomOperations
import com.google.firebase.database.DatabaseReference

class ChatMessagesViewModel(db: DatabaseReference,
                            private val userUUID: String,
                            private val targetUUID: String): ViewModel() {

    private val firebaseOps = FirebaseCustomOperations(db)
    private val _chatMessages: MutableLiveData<List<String>> by lazy {
        MutableLiveData(firebaseOps.getChatLog(userUUID, targetUUID))
    }
    val chatMessages: LiveData<List<String>> = _chatMessages
}