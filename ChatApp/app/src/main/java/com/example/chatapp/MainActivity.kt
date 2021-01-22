package com.example.chatapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Pair
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class MainActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMainBinding
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        _binding.loginBtn.setOnClickListener {
            auth.createUserWithEmailAndPassword(
                _binding.email.text.takeIf { it!!.isNotBlank() }.toString(),
                _binding.pass.text.takeIf { it!!.isNotBlank() }.toString()
            )
                .addOnSuccessListener {
                    Toast.makeText(this, "Account Created Successfully ", Toast.LENGTH_SHORT).show()
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(_binding.username.toString())
                        .build()
                    it.user?.updateProfile(profileUpdate)
                }
                .addOnFailureListener {
                    if (it.localizedMessage.contains("already", true)) {
                        signUp(_binding.email.text.toString(), _binding.pass.text.toString())
                    } else {
                        when {
                            _binding.email.text.toString().isEmpty() -> {
                                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            _binding.pass.text.toString().isEmpty() -> {
                                Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            else -> {
                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    }
                }
        }

        auth.addAuthStateListener {
            if (it.currentUser != null) {
                startActivity(Intent(this, ChatActivity::class.java))
                finish()
            }
        }
    }

    private fun signUp(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    it.localizedMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "Logged In Successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}