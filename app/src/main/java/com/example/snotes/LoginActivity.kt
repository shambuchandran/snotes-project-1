package com.example.snotes

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snotes.databinding.ActivityLoginpageBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginpageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginpageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val savedUsername = "username"
        val savedPassword = "12345"
        val editor = getSharedPreferences("LOGINDETAILS", MODE_PRIVATE)
        binding.etusername.setText(editor.getString("USERNAME", null))
        binding.etpassword.setText(editor.getString("PASSWORD", null))

        binding.loginbutton.setOnClickListener {
            val enteredUsername = binding.etusername.text.toString()
            val enteredPassword = binding.etpassword.text.toString()
            if (isEmpty(enteredUsername) || isEmpty(enteredPassword)) {
                Toast.makeText(this, "Username or password cannot be empty", Toast.LENGTH_LONG)
                    .show()
            } else if (enteredUsername != savedUsername) {
                Toast.makeText(this, "Username mismatch", Toast.LENGTH_LONG).show()
            } else if (enteredPassword != savedPassword) {
                Toast.makeText(this, "Wrong password", Toast.LENGTH_LONG).show()
            } else {
                val editor = getSharedPreferences("LOGINDETAILS", MODE_PRIVATE).edit()
                editor.putString("USERNAME", enteredUsername)
                editor.apply()

                Intent(this, MainActivity::class.java).also { intent ->
                    startActivity(intent)
                    binding.etusername.text.clear()
                    binding.etpassword.text.clear()
                }
            }

        }
    }
}