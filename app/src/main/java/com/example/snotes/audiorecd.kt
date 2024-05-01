package com.example.snotes

import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.media.MediaRecorder
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snotes.databinding.ActivityAudiorecdBinding
import java.io.IOException
import java.util.Date

class audiorecd : AppCompatActivity() ,Timer.OnTimerChangeListener{

    private var permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false
    val REQUEST_CODE = 5
    private lateinit var binding: ActivityAudiorecdBinding
    private lateinit var recordbutton: ImageButton
    private lateinit var recorder: MediaRecorder
    private var dirpath = ""
    private var filename = ""
    private var isrecording = false
    private var ispaused = false
    private  lateinit var vibrator: Vibrator
    private lateinit var timer: Timer
    private lateinit var waveFormView: waveFormView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAudiorecdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        permissionGranted = ActivityCompat.checkSelfPermission(
            this,
            permissions[0]
        ) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        }
        recordbutton = binding.btnrecord
        recordbutton.setOnClickListener {
            when {
                ispaused -> resumeRecording()
                isrecording -> pauseRecording()
                else -> startRecording()
            }
            vibrator.vibrate(VibrationEffect.createOneShot(50,VibrationEffect.DEFAULT_AMPLITUDE))
        }
        timer= Timer(this)
        vibrator= getSystemService(Context.VIBRATOR_SERVICE)as Vibrator
        waveFormView = binding.waveFormView

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun resumeRecording() {
        recorder.resume()
        ispaused = false
        recordbutton.setImageResource(R.drawable.round_pause_24)
        timer.start()
    }

    private fun pauseRecording() {
        recorder.pause()
        ispaused = true
        recordbutton.setImageResource(R.drawable.ic_record)
        timer.pause()
    }
    private fun stopRecording() {

        timer.stop()
    }

    private fun startRecording() {
        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        }
        recorder = MediaRecorder()
        dirpath = "${externalCacheDir?.absolutePath}/"
        var simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")
        var date = simpleDateFormat.format(Date())
        filename = "audio_rec_$date"
        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirpath$filename.mp3")
            try {
                prepare()
            } catch (e: IOException) {
            }
            start()
        }
        recordbutton.setImageResource(R.drawable.round_pause_24)
        isrecording = true
        ispaused = false
        timer.start()

    }
    override fun onTimerChange(duration: String) {
      binding.tvtimer.text=duration
        waveFormView.addAmplitude(recorder.maxAmplitude.toFloat())

    }
}