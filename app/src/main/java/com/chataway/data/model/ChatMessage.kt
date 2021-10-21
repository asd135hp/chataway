package com.chataway.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val date: String,
    val time: String,
    val message: String)
