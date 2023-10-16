package uz.frodo.mediaplayer

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import uz.frodo.mediaplayer.databinding.ActivityMediaPlayBinding
import uz.frodo.mediaplayer.models.Song
import java.util.concurrent.TimeUnit


class MediaPlayActivity : AppCompatActivity() {
    lateinit var binding: ActivityMediaPlayBinding
    lateinit var currentSong: Song
    lateinit var list: ArrayList<Song>
    var mediaPlayer = MyMediaPlayer.instance
    lateinit var handler: Handler
    var again: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaPlayBinding.inflate(layoutInflater)


        setContentView(binding.root)

        handler = Handler(mainLooper)
        list = MyMediaPlayer.list
        again = intent.getBooleanExtra("again", false)
        println("Again: $again")
        setResources()

        binding.seekB.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer!!.seekTo(progress)
                    binding.currentTime.text = convertTo(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        binding.listButton.setOnClickListener {
            finish()
        }

    }

    fun setResources() {

        currentSong = list[MyMediaPlayer.currentIndex]

        binding.songName.text = currentSong.name
        binding.songArtist.text = currentSong.artist
        val ln = "${MyMediaPlayer.currentIndex + 1}/${list.size}"
        binding.listNumber.text = ln

        if (again && !mediaPlayer!!.isPlaying) {
            binding.pause.visibility = View.INVISIBLE
            binding.play.visibility = View.VISIBLE
        } else {
            binding.pause.visibility = View.VISIBLE
            binding.play.visibility = View.INVISIBLE
        }

        playMusic()


        binding.playPause.setOnClickListener { pausePlay() }
        binding.next.setOnClickListener { playNextSong() }
        binding.previous.setOnClickListener { playPreviousSong() }
        binding.forward30.setOnClickListener {
            mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition.plus(30_000))
            binding.currentTime.text = convertTo(mediaPlayer!!.currentPosition.toLong())
        }
        binding.replay30.setOnClickListener {
            mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition.minus(30_000))
            binding.currentTime.text = convertTo(mediaPlayer!!.currentPosition.toLong())
        }
    }

    fun playMusic() {
        try {
            if (again) {
                again = false
            } else {
                mediaPlayer!!.reset()
                mediaPlayer!!.setDataSource(currentSong.data)
                mediaPlayer!!.prepare()
                mediaPlayer!!.setOnPreparedListener {
                    mediaPlayer!!.start()
                }

            }

            val total = " / " + convertTo(mediaPlayer!!.duration.toLong())
            binding.totalTime.text = total
            binding.seekB.max = mediaPlayer!!.duration
            handler.postDelayed(runnable, 100)
            mediaPlayer!!.setOnCompletionListener {
                playNextSong()
            }
        } catch (e: Exception) {
            println("Exception")
            e.printStackTrace()
        }


    }

    fun playNextSong() {
        if (MyMediaPlayer.currentIndex == list.lastIndex) {
            MyMediaPlayer.currentIndex = 0
        } else
            MyMediaPlayer.currentIndex += 1

        mediaPlayer!!.reset()
        setResources()
    }

    fun playPreviousSong() {
        if (MyMediaPlayer.currentIndex == 0) {
            MyMediaPlayer.currentIndex = list.lastIndex
        } else
            MyMediaPlayer.currentIndex -= 1

        mediaPlayer!!.reset()
        setResources()
    }

    fun pausePlay() {
        if (mediaPlayer!!.isPlaying) {
            binding.pause.visibility = View.INVISIBLE
            binding.play.visibility = View.VISIBLE
            mediaPlayer!!.pause()
        } else {
            binding.play.visibility = View.INVISIBLE
            binding.pause.visibility = View.VISIBLE
            mediaPlayer!!.start()
        }
    }

    fun convertTo(duration: Long): String {
        val str: String = if (duration / (1000 * 60 * 60) >= 1) {
            String.format(
                "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
            )
        } else {
            String.format(
                "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
            )
        }
        return str
    }

    var runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null) {
                binding.seekB.progress = mediaPlayer!!.currentPosition
                binding.currentTime.text = convertTo(mediaPlayer!!.currentPosition.toLong())
                handler.postDelayed(this, 100)
            }
        }
    }
}