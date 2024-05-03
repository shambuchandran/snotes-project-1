package com.example.snotes

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snotes.database.Notedatabase
import com.example.snotes.databinding.ActivityAudiorecdBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import java.io.File
import java.io.IOException
import java.util.Date

class audiorecd : AppCompatActivity() ,Timer.OnTimerChangeListener{


    private lateinit var amplitudes: ArrayList<Float>
    private var permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private var permissionGranted = false
    val REQUEST_CODE = 5
    private lateinit var binding: ActivityAudiorecdBinding
    private lateinit var recordbutton: ImageButton
    private lateinit var listButton:ImageButton
    private lateinit var recorder: MediaRecorder
    private lateinit var stopButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private var dirpath = ""
    private var filename = ""
    private var isrecording = false
    private var ispaused = false
    private  lateinit var vibrator: Vibrator
    private lateinit var timer: Timer
    private lateinit var waveFormView: waveFormView
    private lateinit var showTimer: TextView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottom_Sheet_layout:LinearLayout
    private lateinit var bottomsheetbg:View
    private lateinit var filenameInput:TextInputEditText
    private lateinit var cancelButton:Button
    private lateinit var okeyButton:Button
    var filePath=""
    private lateinit var dbaudio:Notedatabase
    var audioDuration=""
    //private var keyboardHeight by Delegates.notNull<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAudiorecdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            //keyboardHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        bottomsheetbg=findViewById(R.id.bottomsheetbackground)
        filenameInput=findViewById(R.id.filenameinput)
        bottom_Sheet_layout=findViewById(R.id.bottom_Sheet_layout)
        bottomSheetBehavior=BottomSheetBehavior.from(bottom_Sheet_layout)
        bottomSheetBehavior.peekHeight=0
        bottomSheetBehavior.state =BottomSheetBehavior.STATE_COLLAPSED
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
        listButton=binding.btnlist
        listButton.setOnClickListener {
            Toast.makeText(this, "List Button", Toast.LENGTH_SHORT).show()
        }
        stopButton=binding.btndone
        stopButton.setOnClickListener {
            stopRecording()
            Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomsheetbg.visibility =View.VISIBLE
            //bottomSheetBehavior.peekHeight = keyboardHeight+bottom_Sheet_layout.height
            filenameInput.setText(filename)

        }
        cancelButton=findViewById(R.id.btncancel)
        cancelButton.setOnClickListener {
            File("$dirpath$filename.mp3").delete()
            dismiss()
        }
        okeyButton= findViewById(R.id.btnok)
        okeyButton.setOnClickListener {
            save()
            dismiss()
            val intent = Intent(this, addnote::class.java)
            intent.putExtra("audioduration", audioDuration)
            intent.putExtra("filepath", filePath)
            setResult(Activity.RESULT_OK, intent)
            startActivity(intent)
            finish()
        }
        bottomsheetbg.setOnClickListener {
            File("$dirpath$filename.mp3").delete()
            dismiss()
        }
        deleteButton=binding.btndelete
        deleteButton.setOnClickListener {
            stopRecording()
            File("$dirpath$filename.mp3").delete()
            Toast.makeText(this, "Record deleted", Toast.LENGTH_SHORT).show()
        }

        timer= Timer(this)
        vibrator= getSystemService(Context.VIBRATOR_SERVICE)as Vibrator
        waveFormView = binding.waveFormView
        deleteButton.isClickable=false
        showTimer=binding.tvtimer

    }
    private fun save(){
        //bottomSheetBehavior.peekHeight = keyboardHeight
        val newFilename =filenameInput.text.toString()
        if (newFilename != filename){
            var newfile= File("$dirpath$newFilename.mp3")
            File("$dirpath$filename.mp3").renameTo(newfile)
            Toast.makeText(this, "Recording saved", Toast.LENGTH_SHORT).show()
        }
         filePath= "$newFilename.mp3"
    }
    private fun dismiss(){
        bottomsheetbg.visibility =View.GONE
        hidekeyboard(filenameInput)
        Handler(Looper.getMainLooper()).postDelayed({
            bottomSheetBehavior.state=BottomSheetBehavior.STATE_COLLAPSED
        },100)

    }
    private fun hidekeyboard(view: View){
        val inputMethodManager =getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
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
        recorder.apply {
            stop()
            release()
        }
        ispaused=false
        isrecording=false
        listButton.visibility=View.VISIBLE
        stopButton.visibility=View.GONE
        deleteButton.isClickable=false
        deleteButton.setImageResource(R.drawable.rounded_close_disabled)
        recordbutton.setImageResource(R.drawable.ic_record)
        showTimer.text="00:00:00"
        amplitudes=waveFormView.clearamps()

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
        deleteButton.isClickable=true
        deleteButton.setImageResource(R.drawable.rounded_close)
        listButton.visibility =View.GONE
        stopButton.visibility= View.VISIBLE

    }
    override fun onTimerChange(duration: String) {
      binding.tvtimer.text=duration;
        audioDuration=duration.dropLast(3)
        waveFormView.addAmplitude(recorder.maxAmplitude.toFloat())

    }
}