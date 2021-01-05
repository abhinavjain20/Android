package io.realworld.android.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import io.realworld.android.AuthViewModel
import io.realworld.android.R
import io.realworld.android.databinding.ActivityMainBinding
import io.realworld.android.ui.extensions.loadImage
import io.realworld.api.models.entities.User

class MainActivity : AppCompatActivity() {

    companion object {
        const val PREF_FILE_AUTH = "prefs_auth"
        const val PREF_KEY_TOKEN = "token"
    }

    private lateinit var _appBarConfiguration: AppBarConfiguration
    private lateinit var _authViewModel: AuthViewModel
    private lateinit var _binding: ActivityMainBinding
    private lateinit var _sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _sharedPreferences = getSharedPreferences(PREF_FILE_AUTH, Context.MODE_PRIVATE)
        _authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)


        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        setSupportActionBar(_binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = _binding.drawerLayout
        val navView: NavigationView = _binding.navView
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        _appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_auth,
                R.id.nav_my_feed,
                R.id.nav_feed,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, _appBarConfiguration)
        navView.setupWithNavController(navController)

        val hView: View = navView.getHeaderView(0)

        val navUser = hView.findViewById(R.id.textView) as TextView
        val navImage = hView.findViewById(R.id.imageView) as ImageView
        val navHeader = hView.findViewById(R.id.navUserhead) as LinearLayout
        val navHeaderSplash = hView.findViewById(R.id.navHeadSplash) as LinearLayout
        val navBio = hView.findViewById(R.id.bioText) as TextView

        _sharedPreferences.getString(PREF_KEY_TOKEN, null)?.let { t ->
            _authViewModel.getCurrentUser(t)
        }

//        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
//            _authViewModel.logout()
//            return@setOnMenuItemClickListener true
//        }

        _authViewModel.user.observe({ lifecycle }) {
            updateMenu(it)
            it?.token?.let { t ->
                navHeaderSplash.isVisible = false
                navHeader.isVisible = true
                navUser.text = it.username
                navImage.loadImage(it.image.toString(), true)
                navBio.text = it.bio
                _sharedPreferences.edit {
                    putString(PREF_KEY_TOKEN, t)
                }
            } ?: run {
                _sharedPreferences.edit {
                    remove(PREF_KEY_TOKEN)
                }
                navHeader.isVisible = false
                navHeaderSplash.isVisible = true
            }
            navController.navigateUp()
        }
    }

    private fun updateMenu(user: User?) {
        when (user) {
            is User -> {
                _binding.navView.menu.clear()
                _binding.navView.inflateMenu(R.menu.menu_main_user)
            }
            else -> {
                _binding.navView.menu.clear()
                _binding.navView.inflateMenu(R.menu.menu_main_guest)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                _authViewModel.logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(_appBarConfiguration) || super.onSupportNavigateUp()
    }
}