package com.chataway.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.chataway.R
import com.chataway.ui.login.Utility
import com.chataway.ui.login.LoginResult
import com.google.firebase.auth.FirebaseAuth

const val TAG = "register-task"

class RegisterViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    private val _registerForm = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerForm

    private val _registerResult: MutableLiveData<LoginResult> by lazy {
        if(firebaseAuth.currentUser != null && firebaseAuth.currentUser?.isAnonymous == false)
            MutableLiveData(LoginResult(success = firebaseAuth.currentUser))
        else MutableLiveData()
    }
    val registerResult: LiveData<LoginResult> = _registerResult

    fun register(email: String, password: String) {
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password)

        result.addOnCompleteListener { task ->
            if(task.isSuccessful) {
                Log.i(TAG, "Register successfully")
                _registerResult.value = LoginResult(success = firebaseAuth.currentUser)
            } else {
                Log.i(TAG, "Register failed")
                _registerResult.value = LoginResult(error = R.string.register_failed)
            }
        }
    }

    fun registerDataChanged(email: String, password: String) {
        if (!Utility.isEmailValid(email)) {
            _registerForm.value = RegisterFormState(emailError = R.string.invalid_username)
        } else if (!Utility.isPasswordValid(password)) {
            _registerForm.value = RegisterFormState(passwordError = R.string.invalid_password)
        } else {
            _registerForm.value = RegisterFormState(isDataValid = true)
        }
    }
}