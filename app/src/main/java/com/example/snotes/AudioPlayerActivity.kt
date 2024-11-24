package com.example.snotes

import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat

class AudioPlayerActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var btnPlay:ImageButton
    private lateinit var btnback:ImageButton
    private lateinit var btnforward:ImageButton
    private lateinit var speed:Chip
    private lateinit var seekBar: SeekBar
    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private var delay=1000L
    private var jumpvalue=1000
    private var playBackSpeed=1.0f
    private lateinit var toolbar:MaterialToolbar
    private lateinit var tvFilename:TextView
    private lateinit var tvTrackProgress:TextView
    private lateinit var tvTrackDuration:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var filePath = intent.getStringExtra("audioPath")
        var fileName = intent.getStringExtra("audioName")
        toolbar=findViewById(R.id.toolbar)
        tvFilename=findViewById(R.id.tv_filename)
        tvTrackDuration=findViewById(R.id.tvTrackDuration)
        tvTrackProgress=findViewById(R.id.tvTrackProgress)


        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        tvFilename.text=fileName
        mediaPlayer= MediaPlayer()
        mediaPlayer.apply {
            try {
                setDataSource(filePath)
                prepare()
            }catch (e: IOException){
                e.printStackTrace()
                Toast.makeText(this@AudioPlayerActivity, "Error: Unable to play audio", Toast.LENGTH_SHORT).show()
            }
        }
        tvTrackDuration.text=dateFormat(mediaPlayer.duration)
        btnback=findViewById(R.id.btn_back)
        btnforward=findViewById(R.id.btn_forward)
        btnPlay=findViewById(R.id.btn_play)
        speed=findViewById(R.id.chip)
        seekBar=findViewById(R.id.seekbar)
        handler= Handler(Looper.getMainLooper())
        runnable = Runnable {
            seekBar.progress=mediaPlayer.currentPosition
            tvTrackProgress.text=dateFormat(mediaPlayer.currentPosition)
            handler.postDelayed(runnable,delay)
        }
        btnPlay.setOnClickListener{
            playPausePlayer()
        }
        //playPausePlayer()
        seekBar.max =mediaPlayer.duration
        mediaPlayer.setOnCompletionListener {
            btnPlay.background=ResourcesCompat.getDrawable(resources,R.drawable.rounded_play,theme)
            seekBar.progress=mediaPlayer.duration
            handler.removeCallbacks(runnable)
        }
        btnforward.setOnClickListener {
            mediaPlayer.seekTo(mediaPlayer.currentPosition + jumpvalue)
            seekBar.progress += jumpvalue
        }
        btnback.setOnClickListener {
            mediaPlayer.seekTo(mediaPlayer.currentPosition - jumpvalue)
            seekBar.progress -= jumpvalue
        }
        speed.setOnClickListener {
            if (playBackSpeed !=2.0f){
                playBackSpeed+=0.5f
            }else{
                playBackSpeed=0.5f
            }
            mediaPlayer.playbackParams= PlaybackParams().setSpeed(playBackSpeed)
            speed.text="x $playBackSpeed"
        }
        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser){
                    mediaPlayer.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

    }
    private fun playPausePlayer() {
        if(!mediaPlayer.isPlaying){
            mediaPlayer.start()
            btnPlay.background=ResourcesCompat.getDrawable(resources,R.drawable.round_pause_24,theme)
            handler.postDelayed(runnable,delay)
        }else{
            mediaPlayer.pause()
            btnPlay.background=ResourcesCompat.getDrawable(resources,R.drawable.rounded_play,theme)
            handler.removeCallbacks(runnable)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mediaPlayer.stop()
        mediaPlayer.release()
        handler.removeCallbacks(runnable)
    }
    private fun dateFormat(duration: Int):String{
        var d=duration/1000
        var s=d%60
        var m=(d/60%60)
        var h=((d-m*60)/360).toInt()
        val f:NumberFormat =DecimalFormat("00")
        var str="$m:${f.format(s)}"
        if (h>0) str="$h:$str"
        return str
    }

}