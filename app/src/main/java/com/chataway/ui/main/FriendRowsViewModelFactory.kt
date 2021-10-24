package com.chataway.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FriendRowsViewModelFactory(private val uid: String): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendRowsViewModel::class.java)) {
            return FriendRowsViewModel(uid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}