package io.realworld.android

import android.os.Bundle
import android.view.Menu
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import io.realworld.android.databinding.ActivityMainBinding
import io.realworld.api.models.entities.User

class MainActivity : AppCompatActivity() {

    private lateinit var _appBarConfiguration: AppBarConfiguration
    private lateinit var _authViewModel: AuthViewModel
    private lateinit var _binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        _authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

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
                R.id.nav_feed
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, _appBarConfiguration)
        navView.setupWithNavController(navController)

        _authViewModel.user.observe({ lifecycle }) {
            updateMenu(it)
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