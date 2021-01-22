package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.chatapp.databinding.ActivityLoginBinding

const val PHONE_NUMBER = "phone"

class LoginActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityLoginBinding
    private lateinit var countryCode: String
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _binding.phoneNumberEt.addTextChangedListener {
            _binding.nextBtn.isEnabled = !(it.isNullOrEmpty() || it.length < 10)
        }
        _binding.nextBtn.setOnClickListener { checkNumber() }
    }

    private fun checkNumber() {
        countryCode = _binding.ccp.selectedCountryCodeWithPlus
        phoneNumber = countryCode + _binding.phoneNumberEt.text.toString()

        // TODO LOGIC TO CHECK NUMBER
        startActivity(Intent(this, AuthActivity::class.java).putExtra(PHONE_NUMBER, phoneNumber))
        finish()
    }
}