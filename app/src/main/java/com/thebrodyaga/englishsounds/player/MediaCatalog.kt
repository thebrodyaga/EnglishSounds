package com.thebrodyaga.englishsounds.player

import android.net.Uri
import android.support.v4.media.MediaDescriptionCompat

class MediaCatalog {
    val mediaCatalog: List<MediaDescriptionCompat> = listOf(
        with(MediaDescriptionCompat.Builder()) {
            setDescription("MP4 loaded over HTTP")
            setMediaId("1")
            // License - https://peach.blender.org/download/
            setMediaUri(Uri.parse("https://www.youtube.com/watch?v=6TnIWRoZctg"))
            setTitle("Short film Big Buck Bunny")
            setSubtitle("Streaming video")
            build()
        },
        with(MediaDescriptionCompat.Builder()) {
            setDescription("MP4 loaded over HTTP")
            setMediaId("2")
            // License - https://archive.org/details/ElephantsDream
            setMediaUri(Uri.parse("https://archive.org/download/ElephantsDream/ed_hd.mp4"))
            setTitle("Short film Elephants Dream")
            setSubtitle("Streaming video")
            build()
        },
        with(MediaDescriptionCompat.Builder()) {
            setDescription("MOV loaded over HTTP")
            setMediaId("3")
            // License - https://mango.blender.org/sharing/
            setMediaUri(Uri.parse("https://ftp.nluug.nl/pub/graphics/blender/demo/movies/ToS/ToS-4k-1920.mov"))
            setTitle("Short film Tears of Steel")
            setSubtitle("Streaming audio")
            build()
        }
    )
}