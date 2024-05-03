package com.example.snotes

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.provider.MediaStore
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snotes.alladaptors.AudioAdapter
import com.example.snotes.alladaptors.ImageAdapter
import com.example.snotes.database.Notedatabase
import com.example.snotes.database.Notesdao
import com.example.snotes.database.Notesdata
import com.example.snotes.databinding.ActivityAddnoteBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
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
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    private val REQUEST_AUDIO_RECORD = 4
    private val MAX_IMAGES = 10
    private val MY_PERMISSIONS_REQUEST_CAMERA = 3
    private var imageList = listOf<String>()
    private var audioList = mutableListOf<String>()
    private var audioFilename = mutableListOf<String>()
    private var audiodurationlist = mutableListOf<String>()
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var audioRecyclerView: RecyclerView
    private lateinit var imgAdaptor: ImageAdapter
    private lateinit var audAdapter: AudioAdapter
    private lateinit var database: Notedatabase
    private lateinit var notesDao: Notesdao


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

        val intent = intent
        val audioduration = intent.getStringExtra("audioduration")
        val filepath = intent.getStringExtra("filepath")
        audioList.addAll(listOf(filepath?: ""))
        audioFilename.addAll(listOf(filepath?: ""))
        audiodurationlist.addAll(listOf(audioduration?: ""))
        //audAdapter = AudioAdapter(this,this)
        //audAdapter.notifyItemInserted(audioList.size - 1)
//        audioList = audioList.toMutableList().apply { add(filepath ?: "") }.toList()
//        audioFilename=audioList.toMutableList().apply { add(filepath ?: "") }.toList()
//        audiodurationlist = audiodurationlist.toMutableList().apply { add(audioduration ?: "") }.toList()


        database = Notedatabase.getDatabase(this)
        notesDao = database.notesdao()

        addImageView = binding.imageBtnaddimage
        addImageView.setOnClickListener {
            val imagePopUpMenu = PopupMenu(this, it)
            imagePopUpMenu.menuInflater.inflate(R.menu.imagepopupmenu, imagePopUpMenu.menu)
            imagePopUpMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_camera -> {
                        //dispatchTakePictureIntent()
                        checkCameraPermission()
                        true
                    }

                    R.id.menu_gallery -> {
                        openGallery()
                        true
                    }

                    else -> false
                }
            }
            imagePopUpMenu.show()
        }
        addVoicenote = binding.imageBtnvoicerecord
        addVoicenote.setOnClickListener {
            val voicePopUpMenu = PopupMenu(this, it)
            voicePopUpMenu.menuInflater.inflate(R.menu.recoptions, voicePopUpMenu.menu)
            voicePopUpMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.speechtotext -> {
                        true
                    }

                    R.id.recordaudio -> {
                        startActivity(Intent(this, audiorecd::class.java))
                        true
                    }

                    else -> false
                }
            }
            voicePopUpMenu.show()
        }
        val currentDateTime = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
        val formattedDateTime = dateFormat.format(currentDateTime.time)
        showDateTime = binding.showdatetime
        showDateTime.text = formattedDateTime

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        addreminder = binding.imageBtnalarm
        addreminder.setOnClickListener {
            showDateTimePicker()
        }
        savenote = binding.savenote
        savenote.setOnClickListener {

            saveNoteToDatabase()
            //startActivity(Intent(this, MainActivity::class.java))
        }

        imageRecyclerView = binding.rvimage
        imageRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imgAdaptor = ImageAdapter(this)
        imageRecyclerView.adapter = imgAdaptor

        audioRecyclerView = binding.rvaudio
        audioRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        audAdapter = AudioAdapter(this)
        audAdapter.setAudioPaths(audioList,audiodurationlist,audioFilename )
        audioRecyclerView.adapter = audAdapter
        audAdapter.notifyDataSetChanged()

    }

    private fun saveNoteToDatabase() {
        val title = binding.etTitle.text.toString() // Get title from UI
        val subTitle = binding.etsubtitle.text.toString() // Get subtitle from UI
        val text = binding.etNoteContent.getMD() // Get text from UI

        // Construct NoteData object
        val noteData = Notesdata(
            title = title,
            subTitle = subTitle,
            text = text,
            date = SimpleDateFormat(
                "yyyy-MM-dd hh:mm a",
                Locale.getDefault()
            ).format(Calendar.getInstance().time),
            imagePaths = imageList,
            audioPaths = audioList,
            audioDuration = audiodurationlist
        )

        // Insert noteData into database using coroutines
        GlobalScope.launch(Dispatchers.IO) {
            notesDao.insert(noteData)
        }
           //audAdapter.notifyDataSetChanged()
//        audAdapter.setAudioPaths(audioList,audiodurationlist,audioFilename )
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            MY_PERMISSIONS_REQUEST_CAMERA
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_CAMERA
            )
        } else {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        // Intent to capture image from camera
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.resolveActivity(packageManager)?.also {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CAMERA -> {
                // If request is cancelled, the result arrays are empty
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    // Permission granted, proceed with capturing image
                    dispatchTakePictureIntent()
                } else {
                    // Permission denied, show a message or handle accordingly
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun openGallery() {
        // Intent to pick image from gallery
        val galleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE)
        galleryIntent.type = "image/*"
        if (galleryIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE)
        } else {
            Toast.makeText(this, "No gallery app found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            // Image captured from camera
            val imageBitmap = data?.extras?.get("data") as Bitmap
            addImage(imageBitmap)
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            // Image picked from gallery
            val selectedImageUri = data?.data
            selectedImageUri?.let {
                val imageBitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
                addImage(imageBitmap)
            }
//        }else if (requestCode == REQUEST_AUDIO_RECORD && resultCode == Activity.RESULT_OK) {
//                // Audio recorded
//                val audioduration = intent.getStringExtra("audioduration")
//                val filepath = intent.getStringExtra("filepath")
//                filepath
//                audioList.add(filepath ?: "")
//                audiodurationlist.add(audioduration ?: "")
//                audioFilename.add(filepath ?: "")
//            Log.d("AudioAdapter", "Audio list size: ${audioList.size}")
//            Log.d("AudioAdapter", "Audio duration list size: ${audiodurationlist.size}")
//            Log.d("AudioAdapter", "Audio filename list size: ${audioFilename.size}")
//                //audAdapter.notifyItemInserted(audioList.size - 1)
//                audAdapter.setAudioPaths(audioList, audiodurationlist, audioFilename)
//                audAdapter.notifyDataSetChanged()
//            }

        }
    }


    private fun addImage(imageBitmap: Bitmap) {
        if (imageList.size < MAX_IMAGES) {
            val imagePath = saveImageToInternalStorage(imageBitmap)
            //updateHorizontalScrollView()
            imageList = imageList + imagePath
            imgAdaptor.setImagePaths(imageList)
        } else {
            Toast.makeText(this, "Maximum limit reached", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val filename = "${System.currentTimeMillis()}.jpg" // Generate unique filename
        val directory = getFilesDir() // Get app's internal storage directory

        // Create a new File object representing the image file
        val file = File(directory, filename)

        // Open an output stream to write the image data to the file
        val outputStream = FileOutputStream(file)
        bitmap.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            outputStream
        ) // Compress image (quality 100 for lossless)
        outputStream.flush()
        outputStream.close()

        // Return the absolute path of the saved image file
        return file.absolutePath
    }

//    private fun updateHorizontalScrollView() {
//        val horizontalScrollView =
//            findViewById<HorizontalScrollView>(R.id.horizontalScrollViewImages)
//        horizontalScrollView.removeAllViews()
//        for (bitmap in imageList) {
//            val imageView = ImageView(this)
//            imageView.layoutParams = LinearLayout.LayoutParams(
//                resources.getDimension(R.dimen.image_width).toInt(),
//                resources.getDimension(R.dimen.image_height).toInt()
//            )
//            imageView.setImageBitmap(bitmap)
//            horizontalScrollView.addView(imageView)
//        }
//    }

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
        val intent = Intent(this, ReminderReceiver::class.java)
        pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
        Toast.makeText(this, "Reminder set successfully", Toast.LENGTH_SHORT).show()


    }


}