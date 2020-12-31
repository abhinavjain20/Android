package com.example.echo.fragments

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.echo.R
import com.example.echo.Songs
import com.example.echo.adapters.FavoriteAdapter
import com.example.echo.databases.EchoDatabase
import kotlinx.android.synthetic.main.fragment_favourite.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class FavouriteFragment : Fragment() {

    var myActivity: Activity? = null
    var noFavoritesSongs: RelativeLayout? = null
    var getSongsList: ArrayList<Songs>? = null
    var nowPlayingBottomBar: RelativeLayout? = null
    var playPauseButton: ImageButton? = null
    var songTitle: TextView? = null
    var recyclerView: RecyclerView? = null
    var trackPosition: Int = 0
    var favoriteContent: EchoDatabase? = null

    var refreshList: ArrayList<Songs>? = null
    var getListFromDataBase: ArrayList<Songs>? = null

    /* We make a media player for this fragment in order to tell the song playing
    * fragment that trigger is from this maintain so that it maintain it maintain its mediaplayer */

    object Statified {
        var mediaPlayer: MediaPlayer? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_favourite, container, false)

        noFavoritesSongs = view?.findViewById(R.id.noFavoritesSongs)
        nowPlayingBottomBar = view?.findViewById(R.id.hiddenBarFavScreen)
        songTitle = view?.findViewById(R.id.songTitleFavScreen)
        playPauseButton = view?.findViewById(R.id.playPauseButtonfav)
        recyclerView = view?.findViewById(R.id.favoriteRecycler)
        favoriteContent = EchoDatabase(myActivity)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (myActivity as AppCompatActivity).supportActionBar?.title = "Favorites"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        favoriteContent = EchoDatabase(myActivity)
        getSongsList = getSongsFromPhone()
        if (getSongsList == null) {
            recyclerView?.visibility = View.INVISIBLE
            noFavoritesSongs?.visibility = View.VISIBLE
        } else {
            var favoriteAdapter =
                FavoriteAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)
            val mLayoutManager = LinearLayoutManager(activity)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = favoriteAdapter
            recyclerView?.setHasFixedSize(true)
        }
        display_favorites_by_searching()
        bottomBarSetup()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val item = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

    // function to fetch Songs from Phone
    fun getSongsFromPhone(): ArrayList<Songs>? {
        var arrayList = ArrayList<Songs>()

        /*A content resolver is used to access the data present in your phone
        ** In this case it is used for obtaining the songs present your phone*/
        var contentResolver = myActivity?.contentResolver

        /*Here we are accessing the Media class of Audio class which in turn a class of Media Store,
        which contains information about all the media files present on our mobile device*/
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        /*Here we make the request of songs to the content resolver to get the music files from our device
        *
        * A query is a method used to fetch data from data base like SQL etc
        *
        */
        var songCursor = contentResolver?.query(songUri, null, null, null, null)

        // Cursor is like a pointer which is ponting to the colums of the data

        /*In the if condition we check whether the number of music files are null or not.
        The moveToFirst() function returns the first row of the results*/

        if (songCursor != null && songCursor.moveToFirst()) {
            val songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val dateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)
            /*moveToNext() returns the next row of the results. It returns null if there is no row after the current row*/
            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentTitle = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData = songCursor.getString(songData)
                var currentDate = songCursor.getLong(dateIndex)
                /*Adding the fetched songs to the arraylist*/
                arrayList.add(
                    Songs(
                        currentId,
                        currentTitle,
                        currentArtist,
                        currentData,
                        currentDate
                    )
                )
            }
        }
        else{
            return null
        }
        return arrayList
    }

    /*MAking a function for Bottom Bar Setup */
    fun bottomBarSetup() {
        try {
            bottomBarClickHandler()
            // First thing we do is to set the title track
            songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)

            // When the song gets completed we need to change the title
            // So we need media player class for this
            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener({
                // Update the Text

                songTitle?.setText(SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                // Change the Song
                SongPlayingFragment.Staticated.onSongComplete()
            })

            // Now setup the visibilty for bottombar
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                nowPlayingBottomBar?.visibility = View.VISIBLE
            } else {
                nowPlayingBottomBar?.visibility = View.INVISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // function for setting up click events
    fun bottomBarClickHandler() {
        nowPlayingBottomBar?.setOnClickListener({
            // When the user tap the bottom bar it will navigate to the
            // song playing fragment xml file
            // To maintain the state of media player
            Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
            val songPlayingFragment = SongPlayingFragment()
            var args = Bundle()

            // This work like hashmap where key are unique and values may be same.
            // Now we are entering the song artist, Title value and so on..

            args.putString(
                "songArtist",
                SongPlayingFragment.Statified.currentSongHelper?.songArtist
            )
            args.putString("songTitle", SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putString("path", SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putInt(
                "songID",
                SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int
            )
            args.putInt(
                "songPosition",
                SongPlayingFragment.Statified.currentSongHelper?.currentPosition?.toInt() as Int
            )
            args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)

            // This is to tell the click is done by fav fragment class
            args.putString("FavBottomBar", "success")

            songPlayingFragment.arguments = args
            fragmentManager?.beginTransaction()
                ?.replace(R.id.details_fragment, songPlayingFragment)
                // It means that this fragment is store in stack not destroy
                ?.addToBackStack("SongPlayingFragment")
                ?.commit()
        })

        playPauseButton?.setOnClickListener({
            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean) {
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
            } else {
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        })
    }

    // Function to referesh data when a deletion is happen
    // So that it info does not pass further

    /*The below function is used to search the favorites and display*/
    fun display_favorites_by_searching() {
        /*Checking if database has any entry or not*/
        if (favoriteContent?.checkSize() as Int > 0) {

            /* New list for storing the favorites */
            refreshList = ArrayList<Songs>()

            /*Getting the list of songs from database*/
            getListFromDataBase = favoriteContent?.queryDBList()

            /* Getting the list of songs from phone */
            val fetchListfromDevice = getSongsFromPhone()

            /*If there are no songs in phone then there cannot be any favorites*/
            if (fetchListfromDevice != null) {
                /*Then we check all the songs in the phone*/
                for (i in 0..fetchListfromDevice?.size as Int - 1) {
                    /*We iterate through every song in database*/
                    for (j in 0..getListFromDataBase?.size as Int - 1) {
                        /* While iterating through all the songs we check for the songs which are in both the lists
                         * i.e. the favorites songs*/
                        if ((getListFromDataBase?.get(j)?.songID) == (fetchListfromDevice?.get(i)?.songID)) {
                            // Add the song to refersh list also
                            refreshList?.add((getListFromDataBase as ArrayList<Songs>)[j])
                        } else {
                            /*If refresh list is null we display that there are no favorites*/
                            if (refreshList == null) {
                                recyclerView?.visibility = View.INVISIBLE
                                noFavoritesSongs?.visibility = View.VISIBLE
                            } else {
                                /*Else we setup our recycler view for displaying the favorite songs*/
                                val favoriteAdapter = FavoriteAdapter(
                                    refreshList as ArrayList<Songs>,
                                    myActivity as Context
                                )
                                val mLayoutManager = LinearLayoutManager(activity)
                                recyclerView?.layoutManager = mLayoutManager
                                recyclerView?.itemAnimator = DefaultItemAnimator()
                                recyclerView?.adapter = favoriteAdapter
                                recyclerView?.setHasFixedSize(true)
                            }
                        }
                    }
                }
            } else {
                /*If initially the checkSize() function returned 0 then also we display the no favorites present message*/
                recyclerView?.visibility = View.INVISIBLE
                noFavoritesSongs?.visibility = View.VISIBLE
            }
        }
    }
}
