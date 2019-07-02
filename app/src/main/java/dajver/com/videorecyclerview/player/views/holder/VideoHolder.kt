package dajver.com.videorecyclerview.player.views.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class VideoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract val videoLayout: View

    abstract fun playVideo()

    abstract fun stopVideo()
}