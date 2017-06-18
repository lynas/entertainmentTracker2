package com.lynas.entertainmenttracker2.service

import android.content.Context
import com.lynas.entertainmenttracker2.ANIME
import com.lynas.entertainmenttracker2.model.Anime
import com.lynas.entertainmenttracker2.closing
import io.realm.Realm
import java.util.*

/**
 * Created by sazzad on 6/1/17
 */

class AnimeService {
    fun create(context: Context, animeName: String, complete: () -> Unit) {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        closing(realm) {
            val newAnime = realm.createObject(Anime::class.java, UUID.randomUUID().toString())
            newAnime.animeName = animeName
            newAnime.episodeLastWatched = 1
            realm.commitTransaction()
            FireBaseService.createOrUpdateNode(ANIME, newAnime.id, newAnime)
        }
        complete()
    }

    fun update(context: Context, animeId: String, lastWatched: Int, complete: () -> Unit) {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        closing(realm) {
            val anime = realm.where(Anime::class.java).equalTo("id", animeId).findFirst()
            anime.episodeLastWatched = lastWatched
            realm.commitTransaction()
            FireBaseService.createOrUpdateNode(ANIME, anime.id, anime)
        }
        complete()
    }

    fun getAll(context: Context, complete: (animeList: List<Anime>) -> Unit) {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        closing(realm) {
            val listOfAnime = realm.where(Anime::class.java)
                    .findAll()
                    .map { Anime(it.id, it.animeName, it.episodeLastWatched) }
                    .sortedBy { it.animeName }
            realm.commitTransaction()
            complete(listOfAnime)
        }
    }


    fun delete(context: Context, animeId: String, complete: () -> Unit) {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        closing(realm) {
            val anime = realm.where(Anime::class.java).equalTo("id", animeId).findFirst()
            anime.deleteFromRealm()
            realm.commitTransaction()
            FireBaseService.deleteNodeBy(ANIME, animeId)
            complete()
        }
    }


}













