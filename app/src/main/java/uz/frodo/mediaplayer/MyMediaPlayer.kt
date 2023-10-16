package uz.frodo.mediaplayer

import android.media.MediaPlayer
import uz.frodo.mediaplayer.models.Song

object MyMediaPlayer {
    var instance: MediaPlayer? = null
        get() {
            if (field == null) {
                field = MediaPlayer()
            }
            return field
        }

    var currentIndex = -1
    var list:ArrayList<Song> = ArrayList()
}
