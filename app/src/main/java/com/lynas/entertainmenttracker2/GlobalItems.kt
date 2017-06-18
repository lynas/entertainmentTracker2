package com.lynas.entertainmenttracker2

import android.content.Context
import android.widget.EditText
import io.realm.Realm
import java.io.Closeable
import java.util.*

/**
 * Created by lynas
 * on 5/27/2017..
 */

enum class ListType {
    TV_SHOW, TV_SEASON, TV_EPISODE, ANIME, MOVIE
}

val TV_SHOW = "TVShow"
val ANIME = "Anime"
val MOVIE = "Movie"
val TV_SEASON = "TVSeason"
val TV_EPISODE = "TVEpisode"

fun closing(obj: Closeable, block: () -> Unit) {
    obj.use { obj ->
        if (obj is Realm) {
            if (!obj.isInTransaction) {
                obj.beginTransaction()
            }
            block()
        } else {
            block()
        }
    }
}


fun validateEditTexts(vararg testObj: EditText): Boolean {
    return testObj.indices.none { i -> testObj[i].text.toString().trim { it <= ' ' } == "" }
}

fun getRealm(context: Context): Realm {
    Realm.init(context)
    return Realm.getDefaultInstance()
}

fun dateToCalendar(date: Date?): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = date
    return calendar
}

fun calendarToDate(calendar: Calendar): Date {
    return calendar.time
}

fun miliSecondToDate(timeStamp: Long): Date {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    return calendar.time

}

fun milliSecondToCalender(timeStamp: Long): Calendar {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeStamp
    return calendar

}