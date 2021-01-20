package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.chatapp.databinding.ActivityLoginBinding

const val PHONE_NUMBER = "phone"

class loginActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityLoginBinding
    private lateinit var countryCode: String
    private lateinit var phoneNumber: String
    private lateinit var phoneEdt: EditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        phoneEdt = _binding.phoneNumberEt
        button = _binding.nextBtn

        phoneEdt.addTextChangedListener { button.isEnabled = !(it.isNullOrEmpty() || it.length < 10) }
        button.setOnClickListener { checkNumber() }
    }

    private fun checkNumber() {
        countryCode = _binding.ccp.selectedCountryCodeWithPlus
        phoneNumber = countryCode + _binding.phoneNumberEt.text.toString()

        startActivity(Intent(this, AuthActivity::class.java).putExtra(PHONE_NUMBER, phoneNumber))
        finish()
    }
}