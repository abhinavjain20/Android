package io.realworld.android.activities

import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import io.realworld.android.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var _binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        if (!hasConnected()) {
            _binding.connectionErrorMsg.isVisible = true
            _binding.splashScreen.isVisible = false
            _binding.tryAgainBtn.setOnClickListener {
                Toast.makeText(this, "Give Internet Permission", Toast.LENGTH_LONG).show()
            }
        } else {
            Handler().postDelayed({
                val startAct = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(startAct)
                this.finish()
            }, 1000)
        }
    }

    private fun hasConnected(): Boolean {
        val cm = this.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting)
    }
}