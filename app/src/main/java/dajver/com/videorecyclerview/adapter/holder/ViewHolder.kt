package dajver.com.videorecyclerview.adapter.holder

import android.view.View
import android.widget.FrameLayout
import dajver.com.videorecyclerview.models.VideosModel
import dajver.com.videorecyclerview.player.VideoPlayerView
import dajver.com.videorecyclerview.player.views.holder.VideoHolder
import kotlinx.android.synthetic.main.item_video_view_holder.view.*

class ViewHolder(itemView: View, mOnPlayerVisibleListener: OnPlayerVisibleListener) : VideoHolder(itemView, mOnPlayerVisibleListener) {

    private var mVideosModel: VideosModel? = null
    private var onPlayerVisibleListener: OnPlayerVisibleListener? = null

    init {
        this.onPlayerVisibleListener = mOnPlayerVisibleListener
    }

    override val videoLayout: View
        get() = itemView.video

    fun bind(videosModel: VideosModel) {
        mVideosModel = videosModel
        itemView.video.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, itemView.video.videoHeight)
        itemView.title.text = videosModel.title
    }

    override fun playVideo() {
        itemView.video.initPlayer(mVideosModel!!, onPlayerVisibleListener!!)
        itemView.video.playVideo()
    }

    override fun stopVideo() {
        itemView.video.stopVideo()
    }

    interface OnPlayerVisibleListener {
        fun onActivePlayerView(videoPlayerView: VideoPlayerView)
    }
}
