package com.example.snotes

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snotes.databinding.ActivityAudiorecdBinding

class audiorecd : AppCompatActivity() {

    private var permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)
    private var permissionGranted =false
    val REQUEST_CODE=5
    private lateinit var binding: ActivityAudiorecdBinding
    private lateinit var recordbutton:ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityAudiorecdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        permissionGranted=ActivityCompat.checkSelfPermission(this,permissions[0]) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted){
            ActivityCompat.requestPermissions(this,permissions,REQUEST_CODE)
        }
        recordbutton=binding.btnrecord
        recordbutton.setOnClickListener {
            startRecording()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE){
            permissionGranted =grantResults[0] ==PackageManager.PERMISSION_GRANTED
        }
    }
    private fun startRecording(){
        if (!permissionGranted){
            ActivityCompat.requestPermissions(this,permissions,REQUEST_CODE)
        }

    }
}