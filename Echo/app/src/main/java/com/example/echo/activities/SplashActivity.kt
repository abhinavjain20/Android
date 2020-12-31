package com.example.echo.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.echo.R

class SplashActivity : AppCompatActivity() {

    /*
     * First we want to take all the permissions to start the app.
     * So, we make array of permissions and pass into the function and
     * check whether the permissions is granted or not.
     *
     * Permission of android are written in manifest file so we need to add it into our array
     *
     * We want our splash screen to View only for 1 second then disappear so we use handler class
     * for this and a Postdelayed function of this class which accept two inputs:
     *   One the activity which is going to execute
     *   Second time in which the activity active after that it kill it
     *
     *  We take the permission through context class function checkcallingorselfpermission
     *  and verifying it with the help of PackageManger Class and its function permission
     *  granted or not and finally verifying it and return to our main function i.e
     *  Oncreate function.
     *
     *
     *  WE ALSO NEED TO DEFINE THE PERMISSIONS WE ARE USING IN Manifest file.
     *
     * */
    var permissionsString: Array<String> = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.PROCESS_OUTGOING_CALLS,
        android.Manifest.permission.RECORD_AUDIO
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        /*We have to ask for permissions
        Here We make a function called on request function to ask for permissions
        Intent is a method in android which tells that some activity is going to execute now !
        If the Permission is not Granted

        Here we call the request permisssion function
        The Request code must be unique for all the different request so that it can be distinguisable
        what tyoe of request we wanna access*/


        if (!hasPermissions(this@SplashActivity, *permissionsString)) {

            ActivityCompat.requestPermissions(this@SplashActivity, permissionsString, 131)
        } else {
            // If the Permission is Granted
            Handler().postDelayed({
                // Intent is a method in android which tells that some activity is going to execute now !
                val startAct = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(startAct)
                this.finish()

            }, 1000)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            131 -> {
                // Check the grant result array whether it has empty or not
                // If it is empty then the user has not granted any permission to the app

                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED
                ) {

                    // If all the permissions are granted then do the same process of Showing splash Screen

                    Handler().postDelayed({
                        // Intent is a method in android which tells that some activity is going to execute now !
                        val startAct = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(startAct)
                        this.finish()

                    }, 1000)
                } else {
                    // If user allow one or two permission then it will again show the toast message '
                    Toast.makeText(
                        this@SplashActivity,
                        "Please Grant all the Permissions !",
                        Toast.LENGTH_SHORT
                    ).show()
                    this.finish()
                }
                return
            }

            else -> {
                // Here we create a Toast message because permissions is not granted by the user
                Toast.makeText(this@SplashActivity, "Something Went Wrong!", Toast.LENGTH_SHORT)
                    .show()
                this.finish()
                // a return statement is added to make functioning faster
                return
            }
        }
    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        //vararg is a method to pass array as basic array
        var hasallPermissions = true
        for (permission in permissions) {
            val res = context.checkCallingOrSelfPermission(permission)
            /* We have to check permission is granted or not
               using context object of context class */
            if (res != PackageManager.PERMISSION_GRANTED) {
                hasallPermissions = false
            }
        }
        return hasallPermissions
    }
}
