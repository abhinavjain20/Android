package com.example.echo.activities

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationBuilderWithBuilderAccessor
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.echo.R
import com.example.echo.adapters.NavigationDrawerAdapter
import com.example.echo.fragments.MainScreenFragment
import com.example.echo.fragments.SongPlayingFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    /*The list for storing the names of the items in list of navigation drawer*/
    var navigationDrawerIconsList: ArrayList<String> = arrayListOf()

    // Notification class
    // This tells how the notification will look like.
    var trackNotificationBuilder: Notification? = null

    /*Images which will be used inside navigation drawer*/
    var images_for_navdrawer = intArrayOf(
        R.drawable.navigation_allsongs,
        R.drawable.navigation_favorites,
        R.drawable.navigation_settings,
        R.drawable.navigation_aboutus
    )

    object Statified {
        // Intialize the drawer layout object
        var drawerLayout: DrawerLayout? = null

        // Intiliaze the notification manager
        var notificationManager: NotificationManager? = null
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Link the Activity to the main xml  file
        setContentView(R.layout.activity_main)
        // Set the toolbar/appbar/actionbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        // Set this to default toolbar
        setSupportActionBar(toolbar)

        /*This syntax is used to access the objects inside the class*/
        MainActivity.Statified.drawerLayout = findViewById(R.id.drawer_layout)

        /*Adding names of the titles using the add() function of ArrayList*/
        navigationDrawerIconsList.add("All Songs")
        navigationDrawerIconsList.add("Favorites")
        navigationDrawerIconsList.add("Settings")
        navigationDrawerIconsList.add("About Me")

        // The Humburger icon is appeared because of ActionBArToggle class
        val toggle = ActionBarDrawerToggle(
            this@MainActivity,
            Statified.drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        //  Set up drawer layout to listener
        MainActivity.Statified.drawerLayout?.addDrawerListener(toggle)
        // To sync the state : Convert from 3 lines to 1 (Animation)
        toggle.syncState()

        /*
        * When we are defining fragments over an activity we must initialize it over an activity
        * with the main Fragment.
        *
        *
        * We need fragment manager to manage all the fragments, their layouts and animations
        * Fragment Manager is also needed to display fragments on the screen
        *
        * */
        val mainScreenFragment = MainScreenFragment()
        this.supportFragmentManager
            .beginTransaction()
            .add(R.id.details_fragment, mainScreenFragment, "MainScreenFragment")
            .commit()

        val _navigationAdapter =
            NavigationDrawerAdapter(navigationDrawerIconsList, images_for_navdrawer, this)
        /*Here the function notifyDataSetChanged() tells the adapter that the data you were holding
        has been changed and thus you should refresh the list*/

        _navigationAdapter.notifyDataSetChanged()

        // Setting the navigation recycler View
        val navigation_recycler_view = findViewById<RecyclerView>(R.id.navigation_recycler_view)
        /*
        * Layout Manager is responsible for managing items in  the recycler View.
        * By changing recycler View we can make grid, linear and various other layouts
        * of our items in the recycler view.
        * */

        navigation_recycler_view.layoutManager = LinearLayoutManager(this)
        // Adding the default animator to the layout manager
        navigation_recycler_view.itemAnimator = DefaultItemAnimator()

        /*
        * As we can see there is pattern in our recycler View. So we define the structure
        * of one and then add all the other items onr by one in the list. This process is
        * called INFLATION and this is done by adapter class.
        *
        * We define our single layout in row_custom_navigation_drawer xml file
        */

        /*Now we set the adapter to our recycler view to the adapter we created*/
        navigation_recycler_view.adapter = _navigationAdapter
        /*As the code setHasFixedSize() suggests, the number of items present in the recycler
         view are fixed and won't change any time
         */
        navigation_recycler_view.setHasFixedSize(true)

        /*
        * We create a intent for notification bar so that when ever a user a click
        * on it Its is redirect to the main activity, which contain all the other code
        *
        * We also create a pending intent.
        * A pending Intent is Intent which allow foreign app to user the permissions or code of
        * the current app by giving a token
        * */

        val intent = Intent(this@MainActivity, MainActivity::class.java)
        val pIntent = PendingIntent.getActivity(
            this@MainActivity, System.currentTimeMillis().toInt(),
            intent, 0
        )
        // Now setup how notification bar look like
        trackNotificationBuilder = Notification.Builder(this)
            .setContentTitle("A track is playing in background")
            .setSmallIcon(R.drawable.echo_icon)
            .setContentIntent(pIntent)
            .setOngoing(true)
            .setAutoCancel(true)
            .build()

        Statified.notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    }

    /*Called when app launches
    * When app launches we delete the notification bar
    * */
    override fun onStart() {
        super.onStart()
        try {
            Statified.notificationManager?.cancel(1978)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /* This function is call when app is out of bound
    * That is when home button is pressed then we push the notification if
    * song is played
    * */
    override fun onStop() {
        super.onStop()
        try {
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                Statified.notificationManager?.notify(1918, trackNotificationBuilder)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    override fun onResume() {
        super.onResume()
        try {
            Statified.notificationManager?.cancel(1978)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
