package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private var _binding: ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        _binding?.emailBtn?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        _binding?.phoneBtn?.setOnClickListener {
            startActivity(Intent(this, loginActivity::class.java))
            finish()
        }
    }
}