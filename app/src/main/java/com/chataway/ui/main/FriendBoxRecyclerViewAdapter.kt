package com.chataway.ui.main

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.chataway.ChatMessagesActivity
import com.chataway.ProfileViewerActivity
import com.chataway.data.FirebaseConstants
import com.chataway.data.firebase.AvatarOperations
import com.chataway.data.firebase.Operations
import com.chataway.data.firebase.UserOperations
import com.chataway.data.model.FriendChatRow

import com.chataway.databinding.FragmentFriendItemBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.ktx.Firebase

const val CURRENT_USER_INTENT_KEY = "current_user_uid"
const val FRIEND_PROFILE_VIEWER_INTENT_KEY = "friend_profile_uid_viewer"

/**
 * [RecyclerView.Adapter] that can display a [FriendChatRow].
 */
class FriendBoxRecyclerViewAdapter(
    private val vm: FriendRowsViewModel,
    private val newActivity: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<FriendBoxRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentFriendItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = vm.friendList.value?.get(position)
        holder.bind(item)
    }

    override fun getItemCount(): Int = vm.friendList.value?.size ?: 0

    inner class ViewHolder(binding: FragmentFriendItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        private val nameView: TextView = binding.displayName
        private val latestView: TextView = binding.latestMessage
        private val avatarView: ImageView = binding.chatAvatar
        private val deleteButton: CardView = binding.deleteButton
        private val moreButton: CardView = binding.moreButton
        private val rowContainer: ConstraintLayout = binding.friendBoxItem

        fun bind(data: FriendChatRow?){
            nameView.text = data?.displayName ?: "[No name]"
            latestView.text = data?.latestMessage ?: "[No message]"
            AvatarOperations.fetchAvatar(data?.uid ?: "", avatarView)

            // bind listeners
            avatarView.setOnClickListener {
                newActivity.launch(Intent(
                    nameView.context,
                    ProfileViewerActivity::class.java
                ).apply{
                    putExtra(FRIEND_PROFILE_VIEWER_INTENT_KEY, data?.uid)
                })
            }

            // to the chat messages activity
            rowContainer.setOnClickListener {
                newActivity.launch(Intent(
                    nameView.context,
                    ChatMessagesActivity::class.java
                ).apply{
                    putExtra(FRIEND_PROFILE_VIEWER_INTENT_KEY, data?.uid)
                    putExtra(CURRENT_USER_INTENT_KEY, vm.currentUserUID)
                })
            }

            // delete friend
            deleteButton.setOnClickListener Listener@{
                if(data == null) return@Listener

                Operations.getUserDatabase(vm.currentUserUID)
                    .child(FirebaseConstants.USER_FRIEND_LIST)
                    .child(data.uid)
                    .removeValue()

                // update list
                vm.deleteFriend(data.uid)

                // do some animation here

            }

            // more button
            moreButton.setOnClickListener Listener@{
                // animation?
            }
        }

        private fun animation(){

        }
    }

}