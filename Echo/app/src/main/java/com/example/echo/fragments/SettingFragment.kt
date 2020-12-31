package com.example.echo.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.echo.R
import com.example.echo.fragments.MainScreenFragment.Statified.nowPlayingBottomBar
import com.example.echo.fragments.MainScreenFragment.Statified.playPauseButton
import com.example.echo.fragments.MainScreenFragment.Statified.songTitle
import com.example.echo.fragments.MainScreenFragment.Statified.trackPosition1
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class SettingFragment : Fragment() {

    var myActivity: Activity? = null
    var shakeswitch: Switch? = null

    // We use shared preferences to persist the change in switch 
    object Statified {
        var MY_PREFS_NAME = "ShakeFeature"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_setting, container, false)

        // Linking switch to its view 
        shakeswitch = view?.findViewById(R.id.switchShake)
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (myActivity as AppCompatActivity).supportActionBar?.title = "Settings"
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val prefs = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
        // If the key == feature then it is assigned otherwise false
        var isAllowed = prefs?.getBoolean("feature", false)
        // Checking the value of the feature is off or on

        if (isAllowed as Boolean) {
            // If the feature is on then we make the switch to checked
            shakeswitch?.isChecked = true
        } else {
            shakeswitch?.isChecked = false
        }
        /*Now we handle the change events i.e. when the switched is turned ON or OFF*/
        shakeswitch?.setOnCheckedChangeListener({ CompoundButton, b ->
            if (b) {
                /*If the switch is turned on we then make the feature to be true*/
                val editor =
                    myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                        ?.edit()
                editor?.putBoolean("feature", true)
                editor?.apply()
            } else {
                val editor =
                    myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
                        ?.edit()
                editor?.putBoolean("feature", false)
                editor?.apply()
            }
        })
    }
}
