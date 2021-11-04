package com.chataway.distinction

import com.chataway.data.model.FriendChatRow

object Placeholder {
    fun initialize(filterString: String = "", rowCount: Int = 200){
        items.clear()
        for(i in 0..rowCount){
            val displayName =
                firstName[firstName.indices.random()] + ' ' +
                lastName[lastName.indices.random()]
            if(filterString.isNotEmpty() && !displayName.contains(filterString)) continue

            items.add(FriendChatRow("", displayName, latestChatMessage))
        }
    }

    fun removeItemAt(position: Int) = items.removeAt(position)

    private val firstName = listOf(
        "Carlton", "Luella", "Erik", "Janyce", "Yoshie",
        "Michaela", "Nelly Raney", "Ettie", "Ed", "Sue", "Cherri",
        "Carmelita", "Venessa", "Serina", "Evon", "Kenia",
        "Chas", "Sabra", "Romeo", "Danny"
    )

    private val lastName = listOf(
        "Cheshire", "Chambless", "Room", "Poplawski", "Dion",
        "Lang", "Raney", "Scranton", "Farias", "Barriere", "Arizmendi",
        "Whitmore", "Gigliotti", "Ulmer", "Couvillion", "Braziel",
        "Cappelli", "Ramos", "Branner", "Drysdale"
    )

    private const val latestChatMessage = "Hello, how are you?"

    private val items: MutableList<FriendChatRow> = mutableListOf()
    val ITEMS: List<FriendChatRow> = items
}