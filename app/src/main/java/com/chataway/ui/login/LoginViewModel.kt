package com.chataway.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.chataway.R
import com.google.firebase.auth.FirebaseAuth

const val TAG = "login-task"

class LoginViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult: MutableLiveData<LoginResult> by lazy {
        if(firebaseAuth.currentUser != null && firebaseAuth.currentUser?.isAnonymous == false)
            MutableLiveData(LoginResult(success = firebaseAuth.currentUser))
        else MutableLiveData()
    }
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(email: String, password: String) {
        // either a guest session or a logged in session
        val result = firebaseAuth.signInWithEmailAndPassword(email, password)

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
        if (!Utility.isEmailValid(email)) {
            _loginForm.value = LoginFormState(emailError = R.string.invalid_username)
        } else if (!Utility.isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }
}