package com.chataway.data

import android.content.Context
import com.chataway.data.model.ChatMessage
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ChatLog(private val context: Context) {
    private val MAX_SIZE = 30

    private val _chatMessageLog: MutableList<ChatMessage> = mutableListOf()
    val chatMessages = _chatMessageLog

    fun addNewChatMessage(chatMessage: String){
        val now = LocalDateTime.now()
        _chatMessageLog.add(
            ChatMessage(
                now.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                now.format(DateTimeFormatter.ofPattern("HH:mm")),
                chatMessage
            )
        )

        // remove old message if it exceeds the allowed size
        if(_chatMessageLog.size > MAX_SIZE) _chatMessageLog.removeAt(0)
    }

    fun clear() = _chatMessageLog.clear()

    suspend fun encryptChatLog(): String? = withContext(Dispatchers.Default) {
        val encodedChatLog = _chatMessageLog.joinToString(DELIMITER) {
            base64Encoder.encodeToString(json.encodeToString(it).toByteArray())
        }

        Encryption.encryptString(context, encodedChatLog.toByteArray())
    }

    suspend fun decryptChatLog(encryptedChatLog: String) = withContext(Dispatchers.Default) {
        val decryptedValue = Encryption.decryptString(context, encryptedChatLog).toString()

        clear()
        String(base64Decoder.decode(decryptedValue))
            .split(DELIMITER)
            .forEach {
                _chatMessageLog.add(
                    json.decodeFromString(base64Decoder.decode(it).toString())
                )
            }
    }

    companion object {
        private val base64Encoder = Base64.getEncoder()
        private val base64Decoder = Base64.getDecoder()
        private val json = Json { ignoreUnknownKeys = true }

        private const val DELIMITER = ","
    }
}