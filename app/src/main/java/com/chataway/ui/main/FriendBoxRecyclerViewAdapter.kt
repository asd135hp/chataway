package com.chataway.ui.main

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.chataway.ui.main.placeholder.PlaceholderContent.PlaceholderItem
import com.chataway.databinding.FragmentFriendItemBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class FriendBoxRecyclerViewAdapter(
    private val values: List<PlaceholderItem>
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
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.content
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentFriendItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.displayName
        val contentView: TextView = binding.latestMessage

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}