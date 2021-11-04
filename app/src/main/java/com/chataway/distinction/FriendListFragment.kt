package com.chataway.distinction

import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import com.chataway.R

//import com.chataway.distinction.firstApproach.FriendListRecyclerViewAdapter
import com.chataway.distinction.secondApproach.FriendListRecyclerViewAdapter

/**
 * A fragment representing a list of Items.
 */
class FriendListFragment : Fragment() {
    private var nameFilter = ""
    private var deleteIcon: Bitmap? = null

    private val compatibleColorFilter =
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            BlendModeColorFilter(Color.WHITE, BlendMode.SRC_ATOP)
        } else {
            PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        }

    private val touchHelper = ItemTouchHelper(object: ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int = makeFlag(ACTION_STATE_SWIPE, START)

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if(direction == START){
                Placeholder.removeItemAt(viewHolder.bindingAdapterPosition)
                viewHolder.bindingAdapter?.notifyItemRemoved(viewHolder.bindingAdapterPosition)
                Log.i("Distinction", "Called onSwiped")
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            if(actionState == ACTION_STATE_SWIPE){
                val view = viewHolder.itemView
                val minLeft = view.width - 60
                val minTop = view.top + view.height / 2
                if(deleteIcon != null){
                    c.apply {
                        drawColor(Color.RED)
                        drawBitmap(
                            deleteIcon!!,
                            minLeft - deleteIcon!!.width / 2f,
                            minTop - deleteIcon!!.height / 2f,
                            Paint().apply {
                                colorFilter = compatibleColorFilter
                            }
                        )
                    }
                }
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // risk of running this twice
        if(nameFilter.isEmpty()) Placeholder.initialize()

        requireArguments().also {
            // check if name filter is unique for filtering feature
            it.getString(NAME_FILTER)?.also { filterString ->
                if(filterString != nameFilter){
                    Placeholder.initialize(filterString)
                    nameFilter = filterString
                }
            }
        }

        deleteIcon = activity?.getDrawable(R.drawable.ic_delete_black_24dp)?.toBitmap()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friend_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            touchHelper.attachToRecyclerView(view)
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = FriendListRecyclerViewAdapter()
        }
        return view
    }

    companion object {
        const val NAME_FILTER = "name_filter"
    }
}