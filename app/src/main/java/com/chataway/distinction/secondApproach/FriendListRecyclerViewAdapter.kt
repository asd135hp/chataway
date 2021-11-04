package com.chataway.distinction.secondApproach

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.chataway.data.model.FriendChatRow
import com.chataway.databinding.FragmentFriendItem2Binding
import com.chataway.distinction.Placeholder

class FriendListRecyclerViewAdapter
    : RecyclerView.Adapter<FriendListRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentFriendItem2Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(Placeholder.ITEMS[position])
    }

    override fun getItemCount(): Int = Placeholder.ITEMS.size

    inner class ViewHolder(private val binding: FragmentFriendItem2Binding)
        : RecyclerView.ViewHolder(binding.root) {
        private val nameView: TextView = binding.displayName
        private val latestView: TextView = binding.latestMessage
        private val avatarView: ImageView = binding.chatAvatar
        private val rowContainer: ConstraintLayout = binding.friendBoxItem

        fun bind(data: FriendChatRow){
            nameView.text = data.displayName
            latestView.text = data.latestMessage

            // bind listeners
            avatarView.setOnClickListener {
                Toast.makeText(
                    binding.root.context,
                    "View ${data.displayName}'s profile",
                    Toast.LENGTH_LONG
                ).show()
            }

            // to the chat messages activity
            rowContainer.setOnClickListener {
                Toast.makeText(
                    binding.root.context,
                    "View messages to ${data.displayName}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}