package com.chataway.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chataway.data.Encryption
import com.chataway.data.FirebaseConstants
import com.chataway.data.firebase.Operations
import com.chataway.data.firebase.UserOperations
import com.chataway.data.model.FriendChatRow
import com.google.firebase.database.DataSnapshot

class FriendRowsViewModel(private val uid: String): ViewModel() {
    private val _friendList = MutableLiveData<List<FriendChatRow>>()
    val friendList: LiveData<List<FriendChatRow>> = _friendList
    val currentUserUID = uid

    fun deleteFriend(uid: String){
        _friendList.value = _friendList.value?.filter { it.uid != uid }
    }

    fun fetchAllUsers(context: Context, nameFilter: String = ""){
        _friendList.value?.toMutableList()?.clear()
        Operations.iterateThroughAllUsers("$uid/${FirebaseConstants.USER_FRIEND_LIST}")
        { dataSnapshot ->
            dataSnapshot.children.forEach { userSnapshot ->
                val targetUID = userSnapshot.getValue(String::class.java) ?: return@forEach
                getFriendInformation(context, targetUID, nameFilter)
            }
        }
    }

    private fun getFriendInformation(context: Context, targetUID: String, nameFilter: String)
        = UserOperations.getUser(targetUID) Task@{
            if (it.isSuccessful) {
                val targetSnapshot = it.result ?: return@Task
                val name = targetSnapshot
                    .child(FirebaseConstants.USER_DISPLAY_NAME)
                    .getValue(String::class.java)
                if (name == null || !name.contains(nameFilter)) return@Task

                // fetch last message and add new row to recyclerview's list
                targetSnapshot.ref
                    .child(FirebaseConstants.USER_CHAT_LOG)
                    .orderByKey()
                    .limitToLast(1)
                    .get()
                    .addOnSuccessListener { messageSnapshot ->
                        _friendList.value?.toMutableList()?.add(
                            FriendChatRow(
                                uid,
                                name,
                                getLatestMessage(context, messageSnapshot)
                            )
                        )
                    }
            }
        }

    private fun getLatestMessage(context: Context, messageSnapshot: DataSnapshot): String {
        val lastMessage = messageSnapshot.getValue(String::class.java) ?: return ""
        return Encryption.decrypt(context, lastMessage.toByteArray())?.toString() ?: ""
    }
}