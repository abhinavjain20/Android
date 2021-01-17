package com.example.chatapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setContentView(R.layout.activity_main)
        _binding.loginBtn.setOnClickListener {
            auth.createUserWithEmailAndPassword(_binding.email.text.toString(), _binding.pass.text.toString())
                .addOnSuccessListener { Toast.makeText(this,"Logged in Successfully",Toast.LENGTH_LONG).show()}
                .addOnFailureListener { Toast.makeText(this,"${it.localizedMessage}",Toast.LENGTH_LONG).show() }
        }
    }
}