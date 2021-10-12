package com.chataway.data.model

import java.net.URL

data class UserProfile(
    val photoURL: URL,
    val email: String,
    val DOB: String
)