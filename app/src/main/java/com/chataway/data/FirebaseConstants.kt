package com.chataway.data

object FirebaseConstants {
    const val DB_ENTRY_KEY = "users"
    const val DB_AVATAR_KEY = "avatars"
    // chat log section (better this way because you don't want to intersect two chatlogs into
    // one every 1 second)
    const val USER_CHAT_LOG = "chat_log"
    const val USER_IS_TYPING = "is_typing"
    const val USER_CHAT_LOG_CONTENT = "content"
    // users sub-sections
    const val USER_DISPLAY_NAME = "display_name"
    const val USER_EMAIL = "email"
    const val USER_DOB = "dob"
    const val USER_FRIEND_LIST = "friends"
}