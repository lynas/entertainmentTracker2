package com.lynas.entertainmenttracker2.service

import android.content.Context
import com.lynas.entertainmenttracker2.MOVIE
import com.lynas.entertainmenttracker2.closing
import com.lynas.entertainmenttracker2.getRealm
import com.lynas.entertainmenttracker2.model.Movie
import io.realm.Realm
import java.util.*

/**
 * Created by lynas on 6/4/2017..
 */

class MovieService {
    fun create(context: Context, name: String, releaseDate: Long, result: () -> Unit) {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        closing(realm) {
            val movie = realm.createObject(Movie::class.java, UUID.randomUUID().toString())
            movie.name = name
            movie.releaseDate = releaseDate
            realm.commitTransaction()
            FireBaseService.createOrUpdateNode(MOVIE, movie.id, movie)
            result()
        }
    }

    fun getAll(context: Context, result: (movieList: List<Movie>) -> Unit) {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        closing(realm) {
            val listOfMovies = realm.where(Movie::class.java)
                    .findAll()
                    .map { Movie(it.id, it.name, it.releaseDate, it.watched) }
                    .sortedBy { it.releaseDate }
            realm.commitTransaction()
            result(listOfMovies)
        }
    }

    fun update(context: Context, movieId: String, isWatched: Boolean, result: () -> Unit) {
        val realm = getRealm(context)
        closing(realm) {
            val movie = realm.where(Movie::class.java).equalTo("id", movieId).findFirst()
            movie.watched = isWatched
            realm.commitTransaction()
            FireBaseService.createOrUpdateNode(MOVIE, movie.id, movie)
            result()
        }
    }

    fun delete(context: Context, movieId: String, result: () -> Unit) {
        val realm = getRealm(context)
        closing(realm) {
            realm.where(Movie::class.java).equalTo("id", movieId).findFirst().deleteFromRealm()
            FireBaseService.deleteNodeBy(MOVIE, movieId)
            result()
        }
    }
}

























