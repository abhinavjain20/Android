package com.example.chatapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivityAuthBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit


class AuthActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityAuthBinding
    private lateinit var phoneNumber: String
    private lateinit var verificationId: String
    private val auth by lazy { FirebaseAuth.getInstance() }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        phoneNumber = intent.getStringExtra(PHONE_NUMBER).toString()
        _binding.msgText.text = "Enter the Code sent to ${intent.getStringExtra(PHONE_NUMBER)}"
        sendVerificationCode(phoneNumber)

        // Check Manually
        _binding.verify.setOnClickListener {
            val code: String =
                _binding.digit1.text.toString() + _binding.digit2.text.toString() + _binding.digit3.text.toString() +
                        _binding.digit4.text.toString() + _binding.digit5.text.toString() + _binding.digit6.text.toString()

            if (code.isEmpty() || code.length < 6) {
                _binding.digit6.requestFocus()
                _binding.digit5.requestFocus()
                _binding.digit4.requestFocus()
                _binding.digit3.requestFocus()
                _binding.digit2.requestFocus()
                _binding.digit1.requestFocus()
                return@setOnClickListener
            }
            verifyCode(code)
        }
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Signed in Successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ChatActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    // Sign in failed
                    Log.d("Status", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            this,
                            (task.exception as FirebaseAuthInvalidCredentialsException).message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
    }

    private fun sendVerificationCode(phoneNumber: String) {
        // Sending Code to User Phone Number
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            val code: String = credential.smsCode.toString()
            if (code != null) {
                verifyCode(code)
            } else {
                signInWithPhoneAuthCredential(credential)
            }
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Toast.makeText(this@AuthActivity, e.message, Toast.LENGTH_SHORT).show()
        }

        override fun onCodeSent(
            verficationID: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(verficationID, token)
            verificationId = verficationID
        }
    }
}