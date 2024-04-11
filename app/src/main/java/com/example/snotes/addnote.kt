package com.example.snotes

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snotes.databinding.ActivityAddnoteBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Locale


class addnote : AppCompatActivity() {
    private lateinit var binding: ActivityAddnoteBinding
    private lateinit var addImageView: ImageButton
    private lateinit var addVoicenote: ImageButton
    private lateinit var addreminder: ImageButton
    private lateinit var savenote: FloatingActionButton
    private lateinit var showalarm: TextView
    private lateinit var showDateTime: TextView
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddnoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        addImageView = binding.imageBtnaddimage
        addImageView.setOnClickListener {
            val imagepopupmenu = PopupMenu(this, it)
            imagepopupmenu.menuInflater.inflate(R.menu.imagepopupmenu, imagepopupmenu.menu)
            imagepopupmenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_camera -> {

                        true
                    }

                    R.id.menu_gallery -> {
                        true
                    }

                    else -> false
                }
            }
            imagepopupmenu.show()
        }
        addVoicenote = binding.imageBtnvoicerecord
        addVoicenote.setOnClickListener {
            val voicepopupmenu = PopupMenu(this, it)
            voicepopupmenu.menuInflater.inflate(R.menu.recoptions, voicepopupmenu.menu)
            voicepopupmenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.speechtotext -> {
                        true
                    }

                    R.id.recordaudio -> {
                        true
                    }

                    else -> false
                }
            }
            voicepopupmenu.show()
        }
        val currentDateTime = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        val formattedDateTime = dateFormat.format(currentDateTime.time)
        showDateTime=binding.showdatetime
        showDateTime.text = formattedDateTime

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        addreminder = binding.imageBtnalarm
        addreminder.setOnClickListener {
            showDateTimePicker()
        }
        savenote = binding.savenote
        savenote.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, monthofyear: Int, dayOfMonth: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthofyear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val timePickerDialog = TimePickerDialog(
                    this,
                    TimePickerDialog.OnTimeSetListener { view: TimePicker, hourOfDay: Int, minute: Int ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
                        val formattedDateTime = dateFormat.format(calendar.time)
                        showalarm = binding.alarm
                        showalarm.text = formattedDateTime
                        setReminder(calendar.timeInMillis)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() + 1000
        datePickerDialog.show()
    }

    private fun setReminder(timeInMillis: Long) {
        val intent=Intent(this, ReminderReceiver::class.java)
        pendingIntent=PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP,timeInMillis,pendingIntent)
        Toast.makeText(this, "Reminder set successfully", Toast.LENGTH_SHORT).show()
        

    }

}