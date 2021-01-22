package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private var _binding: ActivitySplashBinding? = null
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(_binding?.root)

        _binding?.emailBtn?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        _binding?.phoneBtn?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        auth.addAuthStateListener {
            if (it.currentUser != null) {
                startActivity(Intent(this, ChatActivity::class.java))
            }
        }
    }
}