package dajver.com.videorecyclerview.player.views.manager

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AutoPlayLinearLayoutManager(context: Context, orientation: Int, reverseLayout: Boolean) :
    LinearLayoutManager(context, orientation, reverseLayout) {

    private val isScrollEnabled = true

    init {
        initialPrefetchItemCount = 10
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("Error", "IndexOutOfBoundsException in RecyclerView happens")
        }

    }

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled
    }
}