package uz.frodo.mediaplayer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import uz.frodo.mediaplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), MediaPlayer.OnPreparedListener {
    lateinit var binding: ActivityMainBinding
    lateinit var handler: Handler
    var mediaPlayer: MediaPlayer? = null
    val url = "https://www.learningcontainer.com/wp-content/uploads/2020/02/Kalimba.mp3"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handler = Handler(mainLooper)
        binding.play.setOnClickListener {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.rema)

                binding.seekBar.max = mediaPlayer!!.duration
                handler.postDelayed(runnable, 100)
//                mediaPlayer!!.setDataSource(url)
//                mediaPlayer?.setOnPreparedListener(this)
//                mediaPlayer?.prepareAsync()
            }
            mediaPlayer!!.start()
        }

        binding.resume.setOnClickListener {
            if (!mediaPlayer!!.isPlaying)
                mediaPlayer?.start()
        }

        binding.pause.setOnClickListener {
            if (mediaPlayer!!.isPlaying)
                mediaPlayer?.pause()
        }

        binding.stop.setOnClickListener {
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            binding.seekBar.progress = mediaPlayer!!.currentPosition
            mediaPlayer = null
        }

        binding.back.setOnClickListener {
            mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition.minus(10_000))
        }

        binding.forward.setOnClickListener {
            mediaPlayer?.seekTo(mediaPlayer!!.currentPosition.plus(10_000))
        }

        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser)
                    mediaPlayer?.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

    }

    var runnable = object : Runnable {
        override fun run() {
            if (mediaPlayer != null) {
                binding.seekBar.progress = mediaPlayer!!.currentPosition
                handler.postDelayed(this, 100)
            }

        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        mp?.start()
    }

    private fun releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.release()
                mediaPlayer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMP()
    }
}