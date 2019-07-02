package dajver.com.videorecyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dajver.com.videorecyclerview.R
import dajver.com.videorecyclerview.adapter.holder.ViewHolder
import dajver.com.videorecyclerview.models.VideosModel
import dajver.com.videorecyclerview.player.VideoPlayerView
import dajver.com.videorecyclerview.player.views.AutoPlayVideoRecyclerView
import java.util.*

open class VideoRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(), ViewHolder.OnPlayerVisibleListener {

    private var videoPlayerView: VideoPlayerView? = null
    private var videosModels: MutableList<VideosModel> = ArrayList()

    private var mRecyclerView: AutoPlayVideoRecyclerView? = null
    private val isInternetConnected = true

    fun addItems(blinkEdgeModels: List<VideosModel>) {
        this.videosModels.addAll(blinkEdgeModels)
        notifyDataSetChanged()
    }

    override fun onActivePlayerView(videoPlayerView: VideoPlayerView) {
        this.videoPlayerView = videoPlayerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.item_video_view_holder, parent, false)
        return ViewHolder(cardView, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as ViewHolder
        viewHolder.bind(videosModels[position])
        holder.setIsRecyclable(true)
    }

    override fun getItemCount(): Int {
        return videosModels.size
    }

    fun stop() {
        if (videoPlayerView != null) {
            videoPlayerView!!.stopVideo()
        }
    }

    fun pause() {
        if (videoPlayerView != null) {
            videoPlayerView!!.pauseVideo()
        }
    }

    fun resumeLastPlayer() {
        if (isInternetConnected && videoPlayerView != null && mRecyclerView != null) {
            videoPlayerView!!.resumeVideo()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView as AutoPlayVideoRecyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mRecyclerView = null
    }
}