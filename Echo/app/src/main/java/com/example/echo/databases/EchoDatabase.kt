package com.example.echo.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.echo.Songs
import com.example.echo.databases.EchoDatabase.Staticated.COLUMN_ID
import com.example.echo.databases.EchoDatabase.Staticated.COLUMN_SONG_ARTIST
import com.example.echo.databases.EchoDatabase.Staticated.COLUMN_SONG_PATH
import com.example.echo.databases.EchoDatabase.Staticated.COLUMN_SONG_TITLE
import com.example.echo.databases.EchoDatabase.Staticated.TABLE_NAME
import com.example.echo.databases.EchoDatabase.Staticated._songList
import java.lang.Exception

/*
* SQL Lite is a relational database which can store info in forms of table. (rows : colums)
* We can insert,delete and upgrade the data inside it using  SQL commands
*
* Here we define our data base for storing the favorite Songs.
* We also use shared preferences database which we used earlier but Shared preferences used
* KEY:VALUE pair to store the data
*
* But for a songs there are various factors that get store so we cannot do it with keys and
* there is no only 1 Song. So we used more advanced data base SQL LIte.
*
*
* Here we define our data using SQL helper which provide the utilities for creating the database
* if it not create earlier. And easy our task for modify and updating the database when the app
* get updated.
*
* */

class EchoDatabase : SQLiteOpenHelper {
    constructor(
        context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int
    ) : super(context, name, factory, version)

    // Define the database name and column name

    object Staticated {
        var _songList = ArrayList<Songs>()
        var DB_VERSION = 1
        val DB_NAME = "FavoriteDataBase"
        val TABLE_NAME = "FavoriteTable"
        val COLUMN_ID = "SongID"
        val COLUMN_SONG_TITLE = "SongTitle"
        val COLUMN_SONG_ARTIST = "SongArtist"
        val COLUMN_SONG_PATH = "SongPath"
    }

    /*This function is called when we create table and name the column*/
    override fun onCreate(sqliteDatabase: SQLiteDatabase?) {
        // We need to write query for creating TABLE and pass it into our sqlitedatabase using exec method means execution
        sqliteDatabase?.execSQL(
            "CREATE TABLE " + TABLE_NAME + "( " + COLUMN_ID +
                    " INTEGER," + COLUMN_SONG_ARTIST + " STRING," + COLUMN_SONG_TITLE + " STRING," + COLUMN_SONG_PATH + " STRING);"
        )
    }

    /* This function is called when the app is upgrading and we add or delete somethong from database*/
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    constructor(
        context: Context?
    ) : super(context, Staticated.DB_NAME, null, Staticated.DB_VERSION)


    /* We make a function to store our favourite Song */
    fun storeAsfavorite(id: Int?, artist: String?, songTitle: String?, path: String?) {

        // Now we make our database open so that we can add our songs to it
        val db = this.writableDatabase
        /* Now we make a content value object
        *  Content value are made to add content in database
        * This contain the set of values which content resolver process
        * to fetch the data
        * */

        var contentvalues = ContentValues()
        // Put some values in this object. So that we insert in our database
        contentvalues.put(EchoDatabase.Staticated.COLUMN_ID, id)
        contentvalues.put(EchoDatabase.Staticated.COLUMN_SONG_ARTIST, artist)
        contentvalues.put(EchoDatabase.Staticated.COLUMN_SONG_TITLE, songTitle)
        contentvalues.put(EchoDatabase.Staticated.COLUMN_SONG_PATH, path)

        // Insert these values in database
        db.insert(EchoDatabase.Staticated.TABLE_NAME, null, contentvalues)
        // Second parameter is nullColumnHAck means when their is null value in
        // column then it is not inserted in the database.

        // At last we close our database
        db.close()
    }

    // Now we make a function to read the songs from our database
    // This fun return a song

    fun queryDBList(): ArrayList<Songs>? {

        try {
            // Make this database readable
            val db = this.readableDatabase

            // We make a query to fetch the data from database
            val query_params = "SELECT * FROM " + TABLE_NAME

            // Now we make a cursor
            // Second parameter is for adding additional data dynamically.
            // We are not adding it so we pass null
            var cSor = db.rawQuery(query_params, null)
            if (cSor.moveToFirst()) {
                do {
                    var _id =
                        cSor.getInt(cSor.getColumnIndexOrThrow(EchoDatabase.Staticated.COLUMN_ID))
                    var _artist =
                        cSor.getString(cSor.getColumnIndexOrThrow(EchoDatabase.Staticated.COLUMN_SONG_ARTIST))
                    var _title =
                        cSor.getString(cSor.getColumnIndexOrThrow(EchoDatabase.Staticated.COLUMN_SONG_TITLE))
                    var _songPath =
                        cSor.getString(cSor.getColumnIndexOrThrow(EchoDatabase.Staticated.COLUMN_SONG_PATH))
                    _songList.add(Songs(_id.toLong(), _title, _artist, _songPath, 0))
                } while (cSor.moveToNext())
            } else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return _songList
    }

    // Now we make a function to check whether a Song is already in the list or not
    fun checkifIdExists(_id: Int): Boolean {
        var storeId = -1090
        val db = this.readableDatabase
        val query_params =  "SELECT * FROM " + TABLE_NAME + " WHERE SongID = '$_id'"
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()) {
            do {
                storeId = cSor.getInt(cSor.getColumnIndexOrThrow(EchoDatabase.Staticated.COLUMN_ID))
            } while (cSor.moveToNext())
        } else {
            return false
        }
        return storeId != -1090
        // Always true because store id is always changed
    }

    // Fun to delete songs from favorite
    fun deleteFavourite(_id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, COLUMN_ID + " = " + _id, null)
        db.close()
    }

    /*The function checkSize() is used to calculate whether we have any song as favorite or not*/
    fun checkSize(): Int {
        var counter = 0
        val db = this.readableDatabase
        var query_params =   "SELECT * FROM " + TABLE_NAME
        val cSor = db.rawQuery(query_params, null)
        if (cSor.moveToFirst()) {
            do {
                counter = counter + 1
            } while (cSor.moveToNext())
        } else {
            return 0
        }
        return counter
    }
}
