package dajver.com.videorecyclerview.player.listener

import android.util.Log
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray

class PlayerStateChangedListener(private val onPlayerStateChangedListener: OnPlayerStateChangedListener) :
    Player.EventListener {

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {}

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}

    override fun onLoadingChanged(isLoading: Boolean) {}

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        onPlayerStateChangedListener.onPlayerStateChanged(playWhenReady, playbackState, false)
    }

    override fun onRepeatModeChanged(repeatMode: Int) {}

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}

    override fun onPlayerError(error: ExoPlaybackException?) {
        when (error!!.type) {
            ExoPlaybackException.TYPE_SOURCE -> Log.e(TAG, "TYPE_SOURCE: " + error.sourceException.message)
            ExoPlaybackException.TYPE_RENDERER -> Log.e(TAG, "TYPE_RENDERER: " + error.rendererException.message)
            ExoPlaybackException.TYPE_UNEXPECTED -> Log.e(TAG, "TYPE_UNEXPECTED: " + error.unexpectedException.message)
        }
    }

    override fun onPositionDiscontinuity(reason: Int) {}

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}

    override fun onSeekProcessed() {}

    interface OnPlayerStateChangedListener {
        fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int, status: Boolean)
    }

    companion object {
        val TAG = PlayerStateChangedListener::class.java.simpleName
    }
}