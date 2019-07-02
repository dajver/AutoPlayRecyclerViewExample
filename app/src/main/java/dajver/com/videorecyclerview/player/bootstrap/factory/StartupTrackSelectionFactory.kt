package dajver.com.videorecyclerview.player.bootstrap.factory

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.source.TrackGroup
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.util.Clock

class StartupTrackSelectionFactory(private val bandwidthMeter: BandwidthMeter) : TrackSelection.Factory {

    override fun createTrackSelection(
        group: TrackGroup,
        bandwidthMeter: BandwidthMeter,
        vararg tracks: Int
    ): TrackSelection {
        val adaptiveTrackSelection = AdaptiveTrackSelection(
            group,
            tracks,
            bandwidthMeter,
            AdaptiveTrackSelection.DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS.toLong(),
            AdaptiveTrackSelection.DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS.toLong(),
            AdaptiveTrackSelection.DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS.toLong(),
            AdaptiveTrackSelection.DEFAULT_BANDWIDTH_FRACTION,
            AdaptiveTrackSelection.DEFAULT_BUFFERED_FRACTION_TO_LIVE_EDGE_FOR_QUALITY_INCREASE,
            AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS,
            Clock.DEFAULT
        )

        var lowestBitrate = Integer.MAX_VALUE
        var lowestBitrateTrackIndex = C.INDEX_UNSET
        for (i in tracks.indices) {
            val format = group.getFormat(tracks[i])
            if (format.bitrate < lowestBitrate) {
                lowestBitrateTrackIndex = i
                lowestBitrate = format.bitrate
            }
            adaptiveTrackSelection.blacklist(tracks[i],
                BLACKLIST_DURATION
            )
        }
        if (lowestBitrateTrackIndex != C.INDEX_UNSET) {
            adaptiveTrackSelection.blacklist(tracks[lowestBitrateTrackIndex], 0)
        }
        return adaptiveTrackSelection
    }

    companion object {

        // end blacklisting after ten seconds earliest
        private val BLACKLIST_DURATION = (10 * 1000).toLong()
    }
}