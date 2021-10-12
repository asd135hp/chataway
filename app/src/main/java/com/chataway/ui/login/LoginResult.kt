package com.chataway.ui.login

import com.google.firebase.auth.FirebaseUser

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    val success: FirebaseUser? = null,
    val error: Int? = null
)