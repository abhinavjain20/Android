package com.example.echo.fragments

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.CaseMap
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.echo.CurrentSongHelper
import com.example.echo.R
import com.example.echo.Songs
import com.example.echo.databases.EchoDatabase
import com.example.echo.fragments.SongPlayingFragment.Staticated.onSongComplete
import com.example.echo.fragments.SongPlayingFragment.Staticated.playNext
import com.example.echo.fragments.SongPlayingFragment.Staticated.playPrevious
import com.example.echo.fragments.SongPlayingFragment.Staticated.processInformation
import com.example.echo.fragments.SongPlayingFragment.Staticated.updateTextViews
import com.example.echo.fragments.SongPlayingFragment.Statified.audioVisualization
import com.example.echo.fragments.SongPlayingFragment.Statified.currentPosition
import com.example.echo.fragments.SongPlayingFragment.Statified.currentSongHelper
import com.example.echo.fragments.SongPlayingFragment.Statified.endTimeText
import com.example.echo.fragments.SongPlayingFragment.Statified.fab
import com.example.echo.fragments.SongPlayingFragment.Statified.favoriteContent
import com.example.echo.fragments.SongPlayingFragment.Statified.fetchSongs
import com.example.echo.fragments.SongPlayingFragment.Statified.glView
import com.example.echo.fragments.SongPlayingFragment.Statified.loopImageButton
import com.example.echo.fragments.SongPlayingFragment.Statified.mSensorListener
import com.example.echo.fragments.SongPlayingFragment.Statified.mSensorManager
import com.example.echo.fragments.SongPlayingFragment.Statified.mediaPlayer
import com.example.echo.fragments.SongPlayingFragment.Statified.myActivity
import com.example.echo.fragments.SongPlayingFragment.Statified.nextImageButton
import com.example.echo.fragments.SongPlayingFragment.Statified.playPauseImageButton
import com.example.echo.fragments.SongPlayingFragment.Statified.previousImageButton
import com.example.echo.fragments.SongPlayingFragment.Statified.seekBar
import com.example.echo.fragments.SongPlayingFragment.Statified.shuffleImageButton
import com.example.echo.fragments.SongPlayingFragment.Statified.songArtistView
import com.example.echo.fragments.SongPlayingFragment.Statified.songTitleView
import com.example.echo.fragments.SongPlayingFragment.Statified.startTimeText
import com.example.echo.fragments.SongPlayingFragment.Statified.updateSongTime
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * A simple [Fragment] subclass.
 */

class SongPlayingFragment : Fragment() {

    /* We make our variable static by creating them in an object so that when we use them in another
    * fragment it does not create another instance of the same function
    * And we do same for our function also
    *
    * Here we made staticated for function and statified for variable to make our code clean
    * staticated and statified are just name.
    * */

    object Statified {
        var myActivity: Activity? = null

        /*This is the media player variable. We would be using this to play/pause the music*/
        var mediaPlayer: MediaPlayer? = null

        /*The different variables defined will be used for their respective purposes*/
        /*Depending on the task they do we name the variables as such so that it gets easier to
         identify the task they perform*/

        var startTimeText: TextView? = null
        var endTimeText: TextView? = null
        var playPauseImageButton: ImageButton? = null
        var previousImageButton: ImageButton? = null
        var nextImageButton: ImageButton? = null
        var loopImageButton: ImageButton? = null
        var shuffleImageButton: ImageButton? = null
        var seekBar: SeekBar? = null
        var songArtistView: TextView? = null
        var songTitleView: TextView? = null
        var currentPosition: Int = 0
        var fetchSongs: ArrayList<Songs>? = null

        // We make use of sensor class to make shake to change feature
        // Sensor Variables
        var mSensorManager: SensorManager? = null

        // A listener which notify that sensor value changed
        var mSensorListener: SensorEventListener? = null
        var MY_PREFS_NAME = "ShakeFeature"

        // Intialization of favourite button
        var fab: ImageButton? = null

        // Declaring the variable for using the visulizer

        var audioVisualization: AudioVisualization? = null
        var glView: GLAudioVisualizationView? = null

        // Make a object of Echodatabse class
        var favoriteContent: EchoDatabase? = null

        /*The current song helper is used to store the details of the current song being played*/
        var currentSongHelper: CurrentSongHelper? = null

        // Thread are used to do task which is of lightweight. They independently used perform the task
        // so that User experience doen not effect .
        /*  We use thread for update our time at every seconds using runnable class */

        var updateSongTime = object : Runnable {
            override fun run() {
                /*Retrieving the current time position of the media player*/
                val getCurrent = mediaPlayer?.currentPosition

                /*The start time is set to the current position of the song
                * * The TimeUnit class changes the units to minutes and milliseconds and applied to the string
                * * The %d:%d is used for formatting the time strings as 03:45 so that it appears like time
                * */

                startTimeText?.setText(
                    String.format(
                        "%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                        TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong()) -
                                TimeUnit.MILLISECONDS.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(
                                        getCurrent?.toLong() as Long
                                    )
                                )
                    )
                )
                seekBar?.setProgress(getCurrent?.toInt() as Int)
                Handler().postDelayed(this, 1000)
            }
        }
    }

    /* Shared preferences and SQL lite  are the method to store the data and reterive it anywhere
    * Now we are declaring the preferences  for the shuffle abd loop feature
    * The object is created as we need them outside of scope of this class
     */

    object Staticated {
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP = "Loop feature"

        /*The playNext() function is used to play the next song*/
        fun playNext(check: String) {
            // Now We check the condition of shuffle and loop and do the function accordingly
            if (check.equals("PlayNextNormal", true)) {
                currentPosition = currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                // If shuffle is on we make a random selection from list

                var randomObject = Random
                var randomPosition = randomObject.nextInt(fetchSongs?.size?.plus(1) as Int)
                currentPosition = randomPosition
            }
            // Now a condition arises when random position is out of the list
            if (currentPosition == fetchSongs?.size) {
                currentPosition = 0
            }
            currentSongHelper?.isLoop = false
            // Now set the song info
            var nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.songId = nextSong?.songID as Long


            // updating the text views and artist name
            updateTextViews(
                currentSongHelper?.songTitle as String,
                currentSongHelper?.songArtist as String
            )

            // Before Playing set it to reset
            mediaPlayer?.reset()

            try {
                myActivity?.let {
                    mediaPlayer?.setDataSource(
                        it,
                        Uri.parse(currentSongHelper?.songPath)
                    )
                }
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                // Calling the fun
                processInformation(mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Now if the song id is present in databse then chamge the favourite icon
            if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                // Instead of using R.drawable we use SetImage drawable
                // Because we have to change heart icon only not background
                fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        myActivity!!,
                        R.drawable.favorite_on
                    )
                )
            } else {
                fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        myActivity!!,
                        R.drawable.favorite_off
                    )
                )
            }
        }

        // Now create a function when song is about to end

        fun onSongComplete() {
            /*If shuffle was on then play a random next song*/
            if (currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
                currentSongHelper?.isPlaying = true
            } else {
                /*If loop was ON, then play the same song again*/
                if (currentSongHelper?.isLoop as Boolean) {
                    currentSongHelper?.isPlaying = true
                    var nextSong = fetchSongs?.get(currentPosition)
                    currentSongHelper?.currentPosition = currentPosition
                    currentSongHelper?.songPath = nextSong?.songData
                    currentSongHelper?.songTitle = nextSong?.songTitle
                    currentSongHelper?.songArtist = nextSong?.artist
                    currentSongHelper?.songId = nextSong?.songID as Long
                    // update the text views

                    updateTextViews(
                        currentSongHelper?.songTitle as String,
                        currentSongHelper?.songArtist as String
                    )
                    mediaPlayer?.reset()

                    try {
                        myActivity?.let {
                            mediaPlayer?.setDataSource(
                                it,
                                Uri.parse(currentSongHelper?.songPath)
                            )
                        }
                        mediaPlayer?.prepare()
                        mediaPlayer?.start()
                        processInformation(mediaPlayer as MediaPlayer)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    /*If loop was OFF then normally play the next song*/
                    playNext("PlayNextNormal")
                    currentSongHelper?.isPlaying = true
                }
            }

            // Now if the song id is present in database then change the favourite icon
            if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                // Instead of using R.drawable we use SetImage drawable
                // Because we have to change heart icon only not background
                fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        myActivity!!,
                        R.drawable.favorite_on
                    )
                )
            } else {
                fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        myActivity!!,
                        R.drawable.favorite_off
                    )
                )
            }
        }

        // Function to update the the views of songs and their artist names

        fun updateTextViews(songTitle: String, songArtist: String) {
            // To make UI more pretty
            var songTitleUpdated = songTitle
            var songArtistUpdated = songArtist
            if (songTitle.equals("<unknown>", true)) {
                songTitleUpdated = "unknown"
            }
            if (songArtist.equals("<unknown>", true)) {
                songArtistUpdated = "unknown"
            }
            songTitleView?.setText(songTitleUpdated)
            songArtistView?.setText(songArtistUpdated)
        }

        /* function used to update the time */
        fun processInformation(mediaPlayer: MediaPlayer) {

            // Obtaining the final time
            val finalTime = mediaPlayer.duration

            // Obtaining the start time
            val startTime = mediaPlayer.currentPosition
            /*Here we format the time and set it to the start time text*/

            // Setting the seekbar  duration
            seekBar?.max = finalTime
            startTimeText?.setText(
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long))
                )
            )
            endTimeText?.setText(
                String.format(
                    "%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                    TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))
                )
            )
            // Assigned seek bar to its time
            seekBar?.setProgress(startTime)

            /* Now time is synced with the update song time object */
            Handler().postDelayed(updateSongTime, 1000)
        }

        fun playPrevious() {
            currentPosition = currentPosition - 1

            if (currentPosition == -1) {
                currentPosition = fetchSongs?.size?.minus(1) as Int
            }
            if (currentSongHelper?.isPlaying as Boolean) {
                playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            } else {
                playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }
            currentSongHelper?.isLoop = false
            // Set the info recieved

            var nextSong = fetchSongs?.get(currentPosition)
            currentSongHelper?.songPath = nextSong?.songData
            currentSongHelper?.songTitle = nextSong?.songTitle
            currentSongHelper?.songArtist = nextSong?.artist
            currentSongHelper?.songId = nextSong?.songID as Long

            // updating the text views for title and artist name
            Staticated.updateTextViews(
                currentSongHelper?.songTitle as String,
                currentSongHelper?.songArtist as String
            )
            mediaPlayer?.reset()

            try {
                myActivity?.let {
                    mediaPlayer?.setDataSource(
                        it,
                        Uri.parse(currentSongHelper?.songPath)
                    )
                }
                mediaPlayer?.prepare()
                mediaPlayer?.start()
                Staticated.processInformation(mediaPlayer as MediaPlayer)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Now if the song id is present in database then chamge the favourite icon
            if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                // Instead of using R.drawable we use SetImage drawable
                // Because we have to change heart icon only not background
                fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        myActivity!!,
                        R.drawable.favorite_on
                    )
                )
            } else {
                fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        myActivity!!,
                        R.drawable.favorite_off
                    )
                )
            }
        }
    }

    var mAcceleration: Float = 0f
    var mAccelerationCurrent: Float = 0f
    var mAccelerationLast: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        /*Sensor service is activate when the fragment is created*/
        mSensorManager = myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        /*Default values*/
        mAcceleration = 0.0f
        /*We take earth's gravitational value to be default, this will give us good results*/
        mAccelerationCurrent = SensorManager.GRAVITY_EARTH
        mAccelerationLast = SensorManager.GRAVITY_EARTH
        /*Here we call the function*/
        bindShakeListener()
    }

    /*Similar onCreateView() method of the fragment, which we used for the MainScreenFragment*/
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)
        /*Linking views with their ids  or XML file */
        seekBar = view?.findViewById(R.id.seekBar)
        startTimeText = view?.findViewById(R.id.startTime)
        endTimeText = view?.findViewById(R.id.endTime)
        playPauseImageButton = view?.findViewById(R.id.playPauseButton)
        nextImageButton = view?.findViewById(R.id.nextButton)
        previousImageButton = view?.findViewById(R.id.previousButton)
        loopImageButton = view?.findViewById(R.id.loopButton)
        shuffleImageButton = view?.findViewById(R.id.shuffleButton)
        songArtistView = view?.findViewById(R.id.songArtist)
        songTitleView = view?.findViewById(R.id.songTitle)
        glView = view?.findViewById(R.id.visualizer_view)
        fab = view?.findViewById(R.id.favoriteicon)
        fab?.alpha = 0.8f
        mSensorManager = myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (myActivity as AppCompatActivity).supportActionBar?.title = "Now Playing"

        /*Connecting the audio visualization with the view */
        audioVisualization = glView as AudioVisualization
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onResume() {
        super.onResume()
        /*When the fragment resumes, it resumes the visualization process */
        audioVisualization?.onResume()
        /* Register the sensor listener */
        mSensorManager?.registerListener(
            mSensorListener, mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause() {
        super.onPause()
        audioVisualization?.onPause()
        /*When fragment is paused, we remove the sensor to prevent the battery drain*/
        mSensorManager?.unregisterListener(mSensorListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioVisualization?.release()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /*This function is used to manage all the custom menu that we made */
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
        val item2: MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible = false
    }

    /*This is function is used when user click on Back to list icon*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            // This id is equal to action redirect id
            // If user click the condition is true
            R.id.action_redirect -> {
                myActivity?.onBackPressed()
                return false
            }
        }
        return false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        /*Initialising the params of the current song helper object*/
        currentSongHelper = CurrentSongHelper()
        currentSongHelper?.isPlaying = true
        currentSongHelper?.isLoop = false
        currentSongHelper?.isShuffle = false
        favoriteContent = EchoDatabase(myActivity)

        /*These are the variables used for retrieving the Bundle items sent from the main screen
        * The names of these Bundle items, they will be used here*/
        var path: String? = null
        var _songTitle: String? = null
        var _songArtist: String? = null
        var songId: Long = 0

        /* We make a try and Catch Statement.
        * The reason for doing so is that, it may happen that the bundle object does not have these in it and the app may crash
        * * So in order to prevent the crash we use try-catch block. This block is known as the error-handling block
        * */
        try {
            /*path is retrieved using the same key (path) which was used to send it*/
            path = arguments?.getString("path")

            /*song title retrieved with its key songTitle*/
            _songTitle = arguments?.getString("songTitle")
            /*song artist with the key songArtist*/
            _songArtist = arguments?.getString("songArtist")
            /*song id with the key SongId*/
            songId = arguments?.getInt("songId")?.toLong()!!

            /*Here we fetch the received bundle data for current position and the list of all songs*/
            currentPosition = arguments?.getInt("position")!!
            fetchSongs = arguments?.getParcelableArrayList("songData")

            /*Now store the song details to the current song helper object so that they can be used later*/
            currentSongHelper?.songPath = path
            currentSongHelper?.songTitle = _songTitle
            currentSongHelper?.songArtist = _songArtist
            currentSongHelper?.songId = songId
            currentSongHelper?.currentPosition = currentPosition

            updateTextViews(
                currentSongHelper?.songTitle as String,
                currentSongHelper?.songArtist as String
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Receiving the data from fav fragment
        /*Here we check whether we came to the song playing fragment via tapping on a song or by bottom bar*/
        var FromFavBottomBar = arguments?.get("FavBottomBar") as? String
        // The above expression is null also when user does not click at fav fragment
        // song and click on the songs which is present at Main Screen song fragment

        if (FromFavBottomBar != null) {
            // To maintaiin media player consistency
            /*If we came via bottom bar then the already playing media player object is used*/
            Statified.mediaPlayer = FavouriteFragment.Statified.mediaPlayer
        } else {
            /*Else we use the default way*/
            // Media player reset
            mediaPlayer?.reset()
            // Intialise the Media player object
            mediaPlayer = MediaPlayer()
            /*here we tell the media player object that we would be streaming the music*/
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            /*Here also we use the error-handling as the path we sent may return a null object*/

            try {
                /*The data source set the song to the media player object*/
                myActivity?.let { mediaPlayer?.setDataSource(it, Uri.parse(path)) }
                /*Before plaing the music we prepare the media player for playback*/

                mediaPlayer?.prepare()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            /*If all of the above goes well we start the music using the start() method*/
            mediaPlayer?.start()
        }

        // Call the process info function
        processInformation(mediaPlayer as MediaPlayer)
        if (currentSongHelper?.isPlaying as Boolean) {
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else {
            playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
        }
        mediaPlayer?.setOnCompletionListener {
            onSongComplete()
        }
        /* Making the click action function */
        clickHandler()


        /*Intialization the handler the visual effects*/
        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context, 0)

        /*Linking the audio visualization with the handler */
        audioVisualization?.linkTo(visualizationHandler)

        /*Now we want that when if user has turned shuffle or loop ON, then these settings should persist
        * even if the app is restarted after closing
        * This is done with the help of Shared Preferences or SQL lite
        * Shared preferences are capable of storing small amount of data in the form of key-value pair HAshMAp
        * */

        /*
        * SQL is more advanced structure to store the data. We used it in storing our favourite songs
        *
        */

        /* Here we initialize the preferences for shuffle in a private mode
        * * Private mode is chosen so that so other app us able to read the preferences apart from our app*/

        var prefsForShuffle =
            myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)

        /* Here we extract the value of preferences and check if shuffle was on
        * or not
        */

        var isShuffleAllowed = prefsForShuffle?.getBoolean("feature", false)
        if (isShuffleAllowed as Boolean) {
            // activated then
            currentSongHelper?.isShuffle = true
            currentSongHelper?.isLoop = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
            currentSongHelper?.isShuffle = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }

        /*Similarly for Loop */

        var prefsForLoop =
            myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
        var isLoopAllowed = prefsForLoop?.getBoolean("feature", false)
        if (isLoopAllowed as Boolean) {
            // activated then
            currentSongHelper?.isShuffle = false
            currentSongHelper?.isLoop = true
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
            currentSongHelper?.isLoop = false
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }

        // Now if the song id is present in database then change the favourite icon
        if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean) {
            // Instead of using R.drawable we use SetImage drawable
            // Because we have to change heart icon only not background
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity!!, R.drawable.favorite_on))
        } else {
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity!!, R.drawable.favorite_off))
        }
    }

    /*A new click handler function is created to handle all the click functions in the song
    playing fragment*/

    fun clickHandler() {
        // Making the click listener of all the buttons

        // Favourite icon click listener

        fab?.setOnClickListener({
            // Now if the song id is present in database then change the favourite icon
            if (favoriteContent?.checkifIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean) {
                // Instead of using R.drawable we use SetImage drawable
                // Because we have to change heart icon only not background
                fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        myActivity!!,
                        R.drawable.favorite_off
                    )
                )

                /* Now user enter in this condition when the song is already in favourite
                * and he wants to delete it */
                favoriteContent?.deleteFavourite(currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(myActivity, " ' Removed from Favorites!' ", Toast.LENGTH_SHORT).show()

            } else {
                fab?.setImageDrawable(
                    ContextCompat.getDrawable(
                        myActivity!!,
                        R.drawable.favorite_on
                    )
                )
                favoriteContent?.storeAsfavorite(
                    currentSongHelper?.songId?.toInt() as Int,
                    currentSongHelper?.songArtist,
                    currentSongHelper?.songTitle,
                    currentSongHelper?.songPath
                )
                Toast.makeText(myActivity, " 'Added to Favorites!' ", Toast.LENGTH_SHORT).show()
            }
        })

        shuffleImageButton?.setOnClickListener({

            /*Intializing the shared preferences in private mode
        * edit() used so that we can overwrite the preferences */

            var editorShuffle =
                myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
                    ?.edit()
            var editorLoop =
                myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
                    ?.edit()

            if (currentSongHelper?.isShuffle as Boolean) {
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                currentSongHelper?.isShuffle = false

                /*If shuffle was activated previously, then we deactivate it*/
                /*The putBoolean() method is used for saving the boolean value against the key which is feature here*/

                /*Now the preferences agains the block Shuffle feature will have a key: feature and its value: false*/
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else {
                currentSongHelper?.isShuffle = true
                currentSongHelper?.isLoop = false
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

                /* Else shuffle is activated and if loop was activated then loop is
                * deactivated  */

                editorShuffle?.putBoolean("feature", true)
                editorShuffle?.apply()

                /*Similar to shuffle, the loop feature has a key:feature and its value:false*/

                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()

            }

        })

        nextImageButton?.setOnClickListener({
            currentSongHelper?.isPlaying = true
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            // First we check if the shuffle button was enabled or not

            if (currentSongHelper?.isShuffle as Boolean) {
                playNext("PlayNextLikeNormalShuffle")
            } else {
                playNext("PlayNextNormal")
            }
        })
        previousImageButton?.setOnClickListener({

            currentSongHelper?.isPlaying = true

            // Here we check the loop
            if (currentSongHelper?.isLoop as Boolean) {
                // if loop is open , we turn it into off
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            // After it turn off we call the previous function
            playPrevious()
        })
        loopImageButton?.setOnClickListener({

            /*Intializing the shared preferences in private mode
        * edit() used so that we can overwrite the preferences */

            var editorShuffle =
                myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
                    ?.edit()
            var editorLoop =
                myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
                    ?.edit()

            if (currentSongHelper?.isLoop as Boolean) {
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                currentSongHelper?.isLoop = false

                /*If loop was activated previously, then we deactivate it*/
                /*The putBoolean() method is used for saving the boolean value against the key which is feature here*/

                /*Now the preferences agains the block Shuffle feature will have a key: feature and its value: false*/
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                currentSongHelper?.isShuffle = false
                currentSongHelper?.isLoop = true
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)

                /* Else shuffle is activated and if loop was activated then loop is
                * deactivated  */

                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()

                /*Similar to shuffle, the loop feature has a key:feature and its value:false*/

                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }
        })

        /*Here we handle the click event on the play/pause button*/
        playPauseImageButton?.setOnClickListener({
            /*if the song is already playing and then play/pause button is tapped
            * then we pause the media player and also change the button to play button*/
            if (mediaPlayer?.isPlaying as Boolean) {
                mediaPlayer?.pause()
                currentSongHelper?.isPlaying = false
                playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else {
                mediaPlayer?.start()
                currentSongHelper?.isPlaying = true
                playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }

    /*This function handles the shake events in order to change the songs when we shake the phone*/
    fun bindShakeListener() {
        /*The sensor listener has two methods used for its implementation i.e. OnAccuracyChanged() and onSensorChanged*/
        mSensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                /*We do not need to check or work with the accuracy changes for the sensor*/
            }

            override fun onSensorChanged(event: SensorEvent?) {
                /*We need this onSensorChanged function
                *  * This function is called when there is a new sensor event*/
                /*The sensor event has 3 dimensions i.e. the x, y and z in which the changes can occur*/
                val x = event?.values?.get(0)
                val y = event?.values?.get(1)
                val z = event?.values?.get(2)
                /*Now lets see how we calculate the changes in the acceleration*/
                /*Now we shook the phone so the current acceleration will be the first to start with*/
                mAccelerationLast = mAccelerationCurrent
                /*Since we could have moved the phone in any direction, we calculate the Euclidean distance to get the normalized distance*/
                mAccelerationCurrent =
                    Math.sqrt(((x!! * x!! + y!! * y!! + z!! * z!!).toDouble())).toFloat()
                /*Delta gives the change in acceleration*/
                val delta = mAccelerationCurrent - mAccelerationLast
                /*Here we calculate thelower filter
                * * The written below is a formula to get it*/
                mAcceleration = mAcceleration * 0.9f + delta
                /*We obtain a real number for acceleration
                * * and we check if the acceleration was noticeable, considering 12 here*/
                if (mAcceleration > 12) {
                    /*If the acceleration  was greater than 12 we change the song, given the fact our shake to change was active*/
                    val prefs = myActivity?.getSharedPreferences(
                        Statified.MY_PREFS_NAME,
                        Context.MODE_PRIVATE
                    )
                    val isAllowed = prefs?.getBoolean("feature", false)
                    if (isAllowed as Boolean) {
                        playNext("PlayNextNormal")
                    }
                }
            }
        }
    }
}
