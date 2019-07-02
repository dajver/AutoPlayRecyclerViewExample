package dajver.com.videorecyclerview.player.bootstrap

import android.content.Context
import android.net.Uri
import android.os.Handler
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS
import com.google.android.exoplayer2.DefaultLoadControl.DEFAULT_TARGET_BUFFER_BYTES
import com.google.android.exoplayer2.Player.REPEAT_MODE_ALL
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection.*
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultAllocator
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dajver.com.videorecyclerview.player.bootstrap.factory.StartupTrackSelectionFactory
import dajver.com.videorecyclerview.player.bootstrap.enums.VideoPlayerQuality

open class ExoPlayerVideoPlayerBootstrap(context: Context, videoPlayerQuality: VideoPlayerQuality) {

    var exoPlayer: SimpleExoPlayer
    private var mContext: Context

    var isVideoPrepared = false
    var isVideoPaused = false;
    var isAppStopped = false
    var isBuffering = false

    var isVideoFinished = false

    init {
        val trackSelector = DefaultTrackSelector()
        if (videoPlayerQuality == VideoPlayerQuality.LOWEST) {
            // low quality
            trackSelector.setParameters(lowQualityTrackSelectorFactory())
        } else {
            // high quality
            trackSelector.setParameters(highQualityTrackSelectorFactory())
        }

        val loadControl = DefaultLoadControl(
            DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE),
            DEFAULT_MIN_BUFFER_SIZE_MS,
            DEFAULT_MAX_BUFFER_SIZE_MS,
            DEFAULT_BUFFER_FOR_PLAYBACK_MS,
            DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS,
            DEFAULT_TARGET_BUFFER_BYTES,
            DEFAULT_PRIORITZE_TIME_OVER_THREADS
        )
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context, DefaultRenderersFactory(context), trackSelector, loadControl)
        exoPlayer.setRepeatMode(REPEAT_MODE_ALL)

        mContext = context
    }

    private fun lowQualityTrackSelectorFactory(): DefaultTrackSelector.ParametersBuilder {
        val bandwidthMeter = DefaultBandwidthMeter()
        val adaptiveVideoTrackSelection =
            StartupTrackSelectionFactory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(adaptiveVideoTrackSelection)
        val parameters: DefaultTrackSelector.ParametersBuilder

        parameters = trackSelector.parameters.buildUpon()
        parameters.setForceLowestBitrate(true)
        parameters.setAllowNonSeamlessAdaptiveness(true)
        return parameters
    }

    private fun highQualityTrackSelectorFactory(): DefaultTrackSelector.ParametersBuilder {
        val parameters: DefaultTrackSelector.ParametersBuilder
        val bandwidthMeter = DefaultBandwidthMeter()

        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter,
            DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS,
            DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS,
            DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS,
            DEFAULT_BANDWIDTH_FRACTION)

        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        parameters = trackSelector.parameters.buildUpon()
        parameters.setForceHighestSupportedBitrate(true)
        parameters.setAllowNonSeamlessAdaptiveness(true)
        return parameters
    }

    protected fun promoVideoSource(uri: Uri): MediaSource {
        val playerInfo = Util.getUserAgent(mContext, "ExoPlayerInfo")
        val dataSourceFactory = DefaultDataSourceFactory(mContext, playerInfo)
        return ExtractorMediaSource.Factory(dataSourceFactory)
            .setExtractorsFactory(DefaultExtractorsFactory())
            .createMediaSource(uri)
    }

    fun buildMediaSource(uri: Uri): HlsMediaSource {
        val defaultBandwidthMeter = DefaultBandwidthMeter()
        val dataSourceFactory = DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "Exo2"), defaultBandwidthMeter)
        return HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(uri, Handler(), null)
    }

    fun release() {
        exoPlayer.release()
    }

    companion object {
        private const val DEFAULT_MIN_BUFFER_SIZE_MS = 2600
        private const val DEFAULT_MAX_BUFFER_SIZE_MS = 3000
        private const val DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 2000
        private const val DEFAULT_PRIORITZE_TIME_OVER_THREADS = true
    }
}