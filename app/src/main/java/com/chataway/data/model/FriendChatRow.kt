package com.chataway.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FriendChatRow(
    val uid: String,
    val displayName: String,
    val latestMessage: String
) : Parcelable
