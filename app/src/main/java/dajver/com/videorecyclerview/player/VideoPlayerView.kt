package dajver.com.videorecyclerview.player

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.customview.widget.ViewDragHelper.STATE_IDLE
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.Player.STATE_READY
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import dajver.com.videorecyclerview.adapter.holder.ViewHolder
import dajver.com.videorecyclerview.models.VideosModel
import dajver.com.videorecyclerview.player.bootstrap.ExoPlayerVideoPlayerBootstrap
import dajver.com.videorecyclerview.player.bootstrap.enums.VideoPlayerQuality
import dajver.com.videorecyclerview.player.listener.PlayerStateChangedListener
import kotlinx.android.synthetic.main.view_video_player.view.*

open class VideoPlayerView : LinearLayout, PlayerStateChangedListener.OnPlayerStateChangedListener {

    private lateinit var model: VideosModel
    private lateinit var exoPlayerVideoPlayerBootstrap: ExoPlayerVideoPlayerBootstrap

    private var player: SimpleExoPlayer? = null
    private var quality: VideoPlayerQuality? = null

    val currentPosition: Long get() = if (player != null) player!!.currentPosition else 0

    val videoHeight: Int get() {
        val metrics = context.resources.displayMetrics
        (context as Activity).windowManager.defaultDisplay.getRealMetrics(metrics)
        val ratio = metrics.widthPixels * metrics.density / (metrics.heightPixels * metrics.density)
        val screenHeight = (metrics.widthPixels * ratio).toInt()
        return screenHeight + PLAYER_RATIO_HEIGHT_OFFSET
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(dajver.com.videorecyclerview.R.layout.view_video_player, this)
        setup(VideoPlayerQuality.LOWEST)
    }

    private fun setup(quality: VideoPlayerQuality) {
        this.quality = quality
    }

    fun initPlayer(videosModel: VideosModel, onPlayerVisibleListener: ViewHolder.OnPlayerVisibleListener?) {
        this.model = videosModel

        exoPlayerVideoPlayerBootstrap = ExoPlayerVideoPlayerBootstrap(context, quality!!)
        exoPlayerVideoPlayerBootstrap.isVideoPrepared = false

        if (player == null) {
            player = exoPlayerVideoPlayerBootstrap.exoPlayer
            if (exoVideoPlayer != null) {
                exoVideoPlayer!!.setPlayer(player)
                exoVideoPlayer!!.keepScreenOn
                exoVideoPlayer!!.setKeepContentOnPlayerReset(true)
                exoVideoPlayer!!.useController = false
                exoVideoPlayer!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
            }

            val mediaSource: MediaSource
            mediaSource = exoPlayerVideoPlayerBootstrap.buildMediaSource(Uri.parse(videosModel.videoUrl))

            player!!.prepare(mediaSource, true, true)
            player!!.addListener(PlayerStateChangedListener(this))

            onPlayerVisibleListener?.onActivePlayerView(this)
        }
    }

    fun playVideo() {
        progressBar!!.visibility = View.VISIBLE

        exoPlayerVideoPlayerBootstrap.isAppStopped = false

        if (player != null) {
            player!!.playWhenReady = true
            player!!.playbackState
        }
    }

    fun pauseVideo() {
        if (player != null) {
            player!!.playWhenReady = false
            player!!.playbackState
        }

        exoPlayerVideoPlayerBootstrap.isVideoPaused = true
    }

    fun resumeVideo() {
        if (player != null) {
            player!!.playWhenReady = true
            player!!.playbackState
        }

        exoPlayerVideoPlayerBootstrap.isVideoPaused = false
    }

    fun stopVideo() {
        progressBar!!.visibility = View.GONE

        exoPlayerVideoPlayerBootstrap.isVideoPrepared = false
        exoPlayerVideoPlayerBootstrap.isAppStopped = true

        if (player != null) {
            releasePlayer()
        }
    }

    fun releasePlayer() {
        if (player != null) {
            player!!.playWhenReady = false
            player!!.removeListener(null)
            player!!.stop()
            player!!.release()
            player = null

            exoPlayerVideoPlayerBootstrap.release()
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int, status: Boolean) {
        if (!exoPlayerVideoPlayerBootstrap.isVideoPrepared && playbackState == STATE_READY) {
            exoPlayerVideoPlayerBootstrap.isVideoPrepared = true
        }

        if (playbackState == STATE_READY) {
            progressBar!!.visibility = View.GONE
            exoPlayerVideoPlayerBootstrap.isBuffering = false
        } else if (playbackState == Player.STATE_BUFFERING) {
            progressBar!!.visibility = View.VISIBLE
            exoPlayerVideoPlayerBootstrap.isBuffering = true
        }

        if (playbackState == STATE_IDLE || playbackState == STATE_ENDED) {
            progressBar!!.visibility = View.GONE
        }
    }

    companion object {
        const val PLAYER_RATIO_HEIGHT_OFFSET = 100
    }
}