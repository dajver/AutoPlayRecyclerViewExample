package dajver.com.videorecyclerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dajver.com.videorecyclerview.adapter.VideoRecyclerAdapter
import dajver.com.videorecyclerview.models.VideosModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var videoRecyclerAdapter: VideoRecyclerAdapter? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var videosList = ArrayList<VideosModel>()
        videosList.add(VideosModel("The redbull party", "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"))
        videosList.add(VideosModel("The mountains","https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"))
        videosList.add(VideosModel("Tableronde", "https://mnmedias.api.telequebec.tv/m3u8/29880.m3u8"))
        videosList.add(VideosModel("Bunny", "http://184.72.239.149/vod/smil:BigBuckBunny.smil/playlist.m3u8"))
        videosList.add(VideosModel("The redbull party", "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"))
        videosList.add(VideosModel("The mountains", "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"))
        videosList.add(VideosModel("Bunny", "http://184.72.239.149/vod/smil:BigBuckBunny.smil/playlist.m3u8"))

        videoRecyclerAdapter = VideoRecyclerAdapter()
        videoRecyclerAdapter!!.addItems(videosList)
        recyclerView.adapter = videoRecyclerAdapter
    }

    public override fun onPause() {
        super.onPause()
        videoRecyclerAdapter?.pause()
    }

    public override fun onDestroy() {
        super.onDestroy()
        videoRecyclerAdapter?.stop()
        recyclerView.adapter = null
    }

    public override fun onStop() {
        super.onStop()
        videoRecyclerAdapter?.pause()
    }

    public override fun onResume() {
        super.onResume()
        videoRecyclerAdapter?.resumeLastPlayer()
    }
}
