package com.chataway.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns

import com.chataway.R
import com.google.firebase.auth.FirebaseAuth

const val TAG = "login-task"

class LoginViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult: MutableLiveData<LoginResult> by lazy {
        if(firebaseAuth.currentUser != null)
            MutableLiveData(LoginResult(success = firebaseAuth.currentUser))
        else MutableLiveData()
    }
    val loginResult: LiveData<LoginResult> = _loginResult

    val isLoggedIn: Boolean
        get() = loginResult.value != null

    fun login(email: String, password: String, isGuestSession: Boolean) {
        // either a guest session or a logged in session
        val result =
            if(isGuestSession) firebaseAuth.signInAnonymously()
            else firebaseAuth.signInWithEmailAndPassword(email, password)

        result.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Log.i(TAG, "Login successfully")
                _loginResult.value = LoginResult(success = firebaseAuth.currentUser)
            } else {
                Log.i(TAG, "Login failed")
                _loginResult.value = LoginResult(error = R.string.login_failed)
            }
        }
    }

    fun loginDataChanged(email: String, password: String) {
        if (!isEmailValid(email)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isEmailValid(email: String): Boolean {
        return if (email.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            email.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}