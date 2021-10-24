package com.chataway.ui.chatMessages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chataway.data.ChatLog
import com.chataway.data.firebase.ChatLogOperations
import com.chataway.data.model.ChatMessage

class ChatMessagesViewModel(private val userUUID: String,
                            private val targetUUID: String,
                            private val emptyChatLog: ChatLog
): ViewModel() {

    private val _chatMessages: MutableLiveData<List<ChatMessage>> by lazy {
        ChatLogOperations.getChatLog(userUUID, targetUUID, emptyChatLog)
        MutableLiveData(emptyChatLog.chatMessages)
    }
    val chatMessages: LiveData<List<ChatMessage>> = _chatMessages
}