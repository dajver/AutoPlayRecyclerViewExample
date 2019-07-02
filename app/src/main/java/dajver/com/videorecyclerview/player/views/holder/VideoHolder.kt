package dajver.com.videorecyclerview.player.views.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import dajver.com.videorecyclerview.adapter.holder.ViewHolder

abstract class VideoHolder(itemView: View, mOnPlayerVisibleListener: ViewHolder.OnPlayerVisibleListener) : RecyclerView.ViewHolder(itemView) {

    abstract val videoLayout: View

    abstract fun playVideo()

    abstract fun stopVideo()
}