package com.chataway.data

import android.content.Context
import com.chataway.data.model.ChatMessage
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChatLog(private val context: Context, private val uid: String) {
    private val _chatMessageLog: MutableList<ChatMessage> = mutableListOf()
    val chatMessages = _chatMessageLog

    fun addNewChatMessage(chatMessage: String){
        val now = LocalDateTime.now()
        _chatMessageLog.add(
            ChatMessage(
                now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                now.format(DateTimeFormatter.ofPattern("HH:mm")),
                chatMessage,
                uid
            )
        )

        // remove old message if it exceeds the allowed size
        if(_chatMessageLog.size > MAX_SIZE) _chatMessageLog.removeAt(0)
    }

    fun clear() = _chatMessageLog.clear()

    suspend fun encryptChatLog(): List<ByteArray> = withContext(Dispatchers.Default) {
        _chatMessageLog.map {
            Encryption.encrypt(
                context,
                base64Encoder.encode(json.encodeToString(it).toByteArray())
            ) ?: "".toByteArray()
        }
    }

    suspend fun decryptChatLog(encryptedChatLog: DataSnapshot) = withContext(Dispatchers.Default) {
        clear()
        encryptedChatLog.children.forEach {
            val encryptedByteArray = it.getValue(ByteArray::class.java) ?: return@forEach
            val base64DecodedMessage = base64Decoder.decode(encryptedByteArray)
            val decryptedString = Encryption.decrypt(context, base64DecodedMessage).toString()
            _chatMessageLog.add(json.decodeFromString(decryptedString))
        }
    }

    companion object {
        private const val MAX_SIZE = 30
        private val base64Encoder = Base64.getEncoder()
        private val base64Decoder = Base64.getDecoder()
        private val json = Json { ignoreUnknownKeys = true }
    }
}