package com.chataway.distinction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.chataway.databinding.ActivityLoggedInBinding
import com.chataway.ui.utility.afterTextChanged
import com.chataway.R

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoggedInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoggedInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchIcon = binding.searchIcon
        val searchBar = binding.searchBar

        // search bar
        searchIcon.setOnClickListener { searchBar.requestFocus() }
        searchBar.afterTextChanged { setFragment(it) }

        // initialization of the fragment
        setFragment("")
    }

    private fun setFragment(nameFilter: String) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<FriendListFragment>(
                R.id.friend_list,
                null,
                Bundle().apply {
                    putString(FriendListFragment.NAME_FILTER, nameFilter)
                }
            )
        }
    }
}