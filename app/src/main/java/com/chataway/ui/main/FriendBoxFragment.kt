package com.chataway.ui.main

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.chataway.R

/**
 * A fragment representing a list of Items.
 */
class FriendBoxFragment : Fragment() {
    private var nameFilter = ""
    private var uid = ""
    private var isNewNameFilter = false
    private lateinit var friendRowsViewModel: FriendRowsViewModel

    private val newActivity
        = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_CANCELED) {
                // output a result for failed user addition from profile viewer activity
                Toast.makeText(
                    this.context,
                    "An unexpected error happened. Please try again!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.also {
            // check if name filter is unique for filtering feature
            it.getString(NAME_FILTER)?.also { filterString ->
                if(filterString != nameFilter){
                    isNewNameFilter = true
                    nameFilter = filterString
                }
            }

            // force all friends retrieval since uid should never be changed
            // except for when this fragment is first initialized
            it.getString(UID)?.also { newUID ->
                if(newUID != uid){
                    isNewNameFilter = true
                    uid = newUID
                }
            }
        }

        friendRowsViewModel = ViewModelProvider(this, FriendRowsViewModelFactory(uid))
            .get(FriendRowsViewModel::class.java)

        // very expensive, better gate-keeping this line
        if(isNewNameFilter) friendRowsViewModel.fetchAllUsers(this.requireContext(), nameFilter)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friend_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            view.layoutManager = LinearLayoutManager(context)

            friendRowsViewModel.friendList.observe(viewLifecycleOwner) { _ ->
                view.adapter = FriendBoxRecyclerViewAdapter(
                    friendRowsViewModel,
                    newActivity
                )
            }
        }
        return view
    }

    companion object {
        private const val UID = "uid"
        private const val NAME_FILTER = "name_filter"

        fun newInstance(uid: String, nameFilter: String) =
            FriendBoxFragment().apply {
                arguments = Bundle().apply {
                    putString(UID, uid)
                    putString(NAME_FILTER, nameFilter)
                }
            }
    }
}