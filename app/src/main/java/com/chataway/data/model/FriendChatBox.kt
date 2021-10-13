package com.chataway.data.model

import android.net.Uri

data class FriendChatBox(
    val displayName: String,
    val latestMessage: String,
    val photoURL: Uri
)
