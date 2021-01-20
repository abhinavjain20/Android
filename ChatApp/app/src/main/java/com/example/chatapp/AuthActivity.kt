package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        _binding.msgText.text = "Enter the Code sent to ${intent.getStringExtra(PHONE_NUMBER)}"

        _binding.otpTxt.setOnClickListener {
            startActivity(Intent(this,ChatActivity::class.java))
        }
    }
}