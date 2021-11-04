package com.chataway.distinction.thirdApproach

import android.animation.ObjectAnimator
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.chataway.data.model.FriendChatRow
import com.chataway.databinding.FragmentFriendItem1Binding
import com.chataway.distinction.Placeholder

class FriendListRecyclerViewAdapter
    : RecyclerView.Adapter<FriendListRecyclerViewAdapter.ViewHolder>() {
    private var clickedPosition = -1
    private lateinit var _recyclerView: RecyclerView
    private val animationTime = 500L

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        _recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            FragmentFriendItem1Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = Placeholder.ITEMS[position]

        holder.bind(data)
        if(position == clickedPosition) holder.openRow(true)
        else holder.closeRow(true)

        // delete friend
        holder.deleteButton.setOnClickListener {
            clickedPosition = -1
            Placeholder.removeItemAt(position)
            notifyItemRemoved(position)

            ObjectAnimator.ofFloat(
                holder.rowContainer,
                "translationX",
                -1 * holder.rowContainer.width.toFloat()
            ).apply {
                duration = 1000
                start()
            }
        }

        // more button
        holder.moreButton.setOnClickListener Listener@{
            // toggle the same row
            if(clickedPosition == holder.bindingAdapterPosition){
                if(holder.toggleRow()) clickedPosition = -1
                return@Listener
            }

            // force close the previously opened row
            if(clickedPosition != -1) {
                val childViewHolder = _recyclerView.getChildAt(clickedPosition)
                Log.i("Distinction", "??")
                if(childViewHolder != null) {
                    Log.i("Distinction", "Have child view holder")
                    val dynamicHolder = _recyclerView.getChildViewHolder(
                        childViewHolder
                    ) as com.chataway.distinction.firstApproach.FriendListRecyclerViewAdapter.ViewHolder
                    dynamicHolder.toggleRow()
                }
            }

            // toggle newly selected row
            holder.toggleRow()

            clickedPosition = holder.bindingAdapterPosition
        }
    }

    override fun getItemCount(): Int = Placeholder.ITEMS.size

    inner class ViewHolder(binding: FragmentFriendItem1Binding)
        : RecyclerView.ViewHolder(binding.root) {
        private val nameView = binding.displayName
        private val latestView = binding.latestMessage

        private val avatarView = binding.chatAvatar
        val deleteButton = binding.deleteButton
        val moreButton = binding.moreButton
        val rowContainer = binding.friendBoxItem

        private var moreButtonClickedCount = 0

        fun bind(data: FriendChatRow){
            nameView.text = data.displayName
            latestView.text = data.latestMessage

            // avatar view
            avatarView.setOnClickListener {
                Toast.makeText(
                    it.context,
                    "View ${data.displayName}'s profile",
                    Toast.LENGTH_LONG
                ).show()
            }

            // to the chat messages activity
            rowContainer.setOnClickListener {
                Toast.makeText(
                    it.context,
                    "View messages to ${data.displayName}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        fun openRow(noAnimation: Boolean = false) {
            ObjectAnimator.ofFloat(rowContainer, "translationX", -120f)
                .apply {
                    duration = if(noAnimation) 0 else animationTime
                    start()
                }
        }

        fun closeRow(noAnimation: Boolean = false) {
            ObjectAnimator.ofFloat(rowContainer, "translationX", 0f)
                .apply {
                    duration = if(noAnimation) 0 else animationTime
                    start()
                }
        }

        // return true if the result of this function call makes the row open, else false
        fun toggleRow(noAnimation: Boolean = false): Boolean {
            val isClosed = moreButtonClickedCount % 2 == 0
            if(isClosed) openRow(noAnimation)
            else closeRow(noAnimation)

            moreButtonClickedCount += 1
            return !isClosed
        }
    }
}