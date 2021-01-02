package io.realworld.android.ui.extensions

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.util.*

@SuppressLint("ConstantLocale", "NewApi")
val isoDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())

@SuppressLint("ConstantLocale", "NewApi")
val appDateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

var TextView.timeStamp: String
    @RequiresApi(Build.VERSION_CODES.N)
    set(value) {
        val date = isoDateFormat.parse(value)
        text = appDateFormat.format(date)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    get() {
        val date = appDateFormat.parse(text.toString())
        return isoDateFormat.format(date)
    }