package com.lynas.entertainmenttracker2.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.lynas.entertainmenttracker2.ListType
import com.lynas.entertainmenttracker2.R
import com.lynas.entertainmenttracker2.milliSecondToCalender
import com.lynas.entertainmenttracker2.model.*
import org.jetbrains.anko.backgroundColor
import java.util.*

/**
 * Created by lynas
 * on 5/26/2017..
 */

open class GeneralAdapter<T>(context: Context, objects: List<T>, val type: ListType)
    : ArrayAdapter<T>(context, R.layout.tv_list_item, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val viewHolder: ViewHolder
        val result: View
        var cv = convertView

        if (cv == null) {
            val inflater = LayoutInflater.from(context)
            cv = inflater.inflate(R.layout.tv_list_item, parent, false)
            viewHolder = ViewHolder()

            if (type == ListType.MOVIE) {
                cv = inflater.inflate(R.layout.movie_list_item, parent, false)
                viewHolder.text = cv.findViewById(R.id.textViewMovieItem) as TextView
            } else {
                viewHolder.text = cv.findViewById(R.id.textViewTvItem) as TextView
            }

            result = cv!!
            cv.tag = viewHolder
        } else {
            viewHolder = cv.tag as ViewHolder
            result = cv
        }

        when (type) {
            ListType.TV_SHOW -> {
                val item = getItem(position) as TVShow
                viewHolder.text?.text = item.tvShowName
            }
            ListType.TV_SEASON -> {
                val item = getItem(position) as TVSeason
                viewHolder.text?.text = item.seasonName
            }
            ListType.TV_EPISODE -> {
                val item = getItem(position) as TVEpisode
                viewHolder.text?.text = item.episodeName
                if (item.isWatched) {
                    setBg(viewHolder, true)
                } else {
                    setBg(viewHolder, false)
                }
            }
            ListType.ANIME -> {
                val item = getItem(position) as? Anime
                viewHolder.text?.text = "[${item?.episodeLastWatched}] ${item?.animeName}"
            }
            ListType.MOVIE -> {
                val item = getItem(position) as? Movie
                val releaseDate = milliSecondToCalender(item?.releaseDate ?: 0L)
                val day = releaseDate.get(Calendar.DAY_OF_MONTH)
                val month = releaseDate.get(Calendar.MONTH)
                val year = releaseDate.get(Calendar.YEAR)
                viewHolder.text?.text = "${item?.name} \n\nRelease Date: $day-$month-$year"
                if (item?.watched as Boolean) {
                    setBg(viewHolder, true)
                } else {
                    setBg(viewHolder, false)
                }
                if (item.releaseDate < Date().time && !item.watched) {
                    viewHolder.text?.backgroundColor = Color.LTGRAY
                }
            }
        }
        return result
    }

    private fun setBg(viewHolder: ViewHolder, isWatched: Boolean) {
        if (isWatched) {
            viewHolder.text?.backgroundColor = Color.rgb(86, 200, 113)
        } else {
            viewHolder.text?.backgroundColor = Color.WHITE
        }
    }
}

internal class ViewHolder {
    var text: TextView? = null
}

