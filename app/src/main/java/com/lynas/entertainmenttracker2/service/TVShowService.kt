package com.lynas.entertainmenttracker2.service

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.lynas.entertainmenttracker2.TV_EPISODE
import com.lynas.entertainmenttracker2.TV_SEASON
import com.lynas.entertainmenttracker2.TV_SHOW
import com.lynas.entertainmenttracker2.closing
import com.lynas.entertainmenttracker2.model.*
import io.realm.Realm
import java.util.*

class TVShowService {
    fun create(context: Context, tvShowName: String, complete: () -> Unit) {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        closing(realm) {
            val newTvShow = realm.createObject(TVShow::class.java, UUID.randomUUID().toString())
            newTvShow.tvShowName = tvShowName
            realm.commitTransaction()
            FireBaseService.createOrUpdateNode(TV_SHOW, newTvShow.id, newTvShow)
            complete()
        }
    }

    fun getByName(context: Context, name: String, complete: (tvShow: TVShow?) -> Unit) {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        closing(realm) {
            val tvShow: TVShow? = realm.where(TVShow::class.java).equalTo("tvShowName", name).findFirst()
            complete(tvShow)
        }
    }

    fun createSeason(context: Context, tvShowId: String, season: String, episode: Int, complete: () -> Unit) {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        closing(realm) {
            val newTvSeason = realm.createObject(TVSeason::class.java, UUID.randomUUID().toString())
            newTvSeason.seasonName = "Season $season"
            newTvSeason.tvShowId = tvShowId
            FireBaseService.createOrUpdateNode(TV_SEASON, newTvSeason.id, newTvSeason)
            for (i in 1..episode) {
                val newTvEpisode = realm.createObject(TVEpisode::class.java, UUID.randomUUID().toString())
                newTvEpisode.episodeName = "Episode $i"
                newTvEpisode.tvSeasonId = newTvSeason.id
                FireBaseService.createOrUpdateNode(TV_EPISODE, newTvEpisode.id, newTvEpisode)
            }
            realm.commitTransaction()
            complete()
        }
    }

    fun getAll(context: Context,
               complete: (
                       tvShowList: List<TVShow>,
                       tvSeasonList: List<TVSeason>,
                       tvEpisodeList: List<TVEpisode>) -> Unit) {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        closing(realm) {
            val tvShowList = realm.where(TVShow::class.java).findAll().map { TVShow(it.id, it.tvShowName) }
            val tvSeasonList = realm.where(TVSeason::class.java).findAll().map { TVSeason(it.id, it.seasonName, it.tvShowId) }
            val tvEpisodeList = realm.where(TVEpisode::class.java).findAll().map { TVEpisode(it.id, it.episodeName, it.tvSeasonId, it.isWatched) }
            realm.commitTransaction()
            complete(tvShowList, tvSeasonList, tvEpisodeList)
        }
    }

    fun updateEpisode(context: Context, episodeId: String, isWatched: Boolean, complete: () -> Unit) {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        closing(realm) {
            val tvEpisode = realm.where(TVEpisode::class.java).equalTo("id", episodeId).findFirst()
            tvEpisode.isWatched = isWatched
            realm.commitTransaction()
            FireBaseService.dbRef()?.child("TVEpisode")?.child(episodeId)?.setValue(tvEpisode)
            complete()
        }
    }

    fun deleteSeason(context: Context, seasonId: String, complete: () -> Unit) {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        closing(realm) {
            val tvSeason = realm.where(TVSeason::class.java).equalTo("id", seasonId).findFirst()
            tvSeason.deleteFromRealm()
            val tvEpisodeList = realm.where(TVEpisode::class.java).equalTo("tvSeasonId", seasonId).findAll()
            tvEpisodeList.deleteAllFromRealm()
            realm.commitTransaction()
            FireBaseService.deleteNodeBy(TV_SEASON, seasonId)
            deleteFromFireBaseDB(TV_EPISODE, "tvSeasonId", seasonId)
            complete()
        }
    }

    fun deleteTVShow(context: Context, tvShowId: String, complete: () -> Unit) {
        Realm.init(context)
        val realm = Realm.getDefaultInstance()
        closing(realm) {
            val tvShow = realm.where(TVShow::class.java).equalTo("id", tvShowId).findFirst()
            tvShow.deleteFromRealm()
            val tvSeasonList = realm.where(TVSeason::class.java).equalTo("tvShowId", tvShowId).findAll()
            deleteTvShowAndConnectedSeasonAndEpisodeFromFirebase(tvShowId, tvSeasonList.filter { true })
            tvSeasonList
                    .map { realm.where(TVEpisode::class.java).equalTo("tvSeasonId", it.id).findAll() }
                    .forEach { it.deleteAllFromRealm() }
            tvSeasonList.deleteAllFromRealm()
            realm.commitTransaction()
            complete()
        }
    }

    private fun deleteTvShowAndConnectedSeasonAndEpisodeFromFirebase(tvShowId: String, seasonList: List<TVSeason>) {
        FireBaseService.dbRef()?.child("TVShow")?.child(tvShowId)?.removeValue()
        deleteFromFireBaseDB("TVSeason", "tvShowId", tvShowId)
        seasonList.forEach {
            deleteFromFireBaseDB("TVEpisode", "tvSeasonId", it.id)
        }
    }

    private fun deleteFromFireBaseDB(root: String, searchBy: String, value: String) {
        FireBaseService.dbRef()?.child(root)
                ?.orderByChild(searchBy)
                ?.equalTo(value)
                ?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        error("Firebase db error")
                    }

                    override fun onDataChange(snapshot: DataSnapshot?) {
                        if (snapshot != null) {
                            if (snapshot.value != null) {
                                val map = snapshot.value as HashMap<*, *>
                                for (key in map.keys) {
                                    FireBaseService.dbRef()?.child(root)?.child(key as? String)?.removeValue()
                                }
                            }
                        }

                    }
                })
    }

    fun sync(context: Context, complete: () -> Unit) {
        val mapper = ObjectMapper()
        FireBaseService.dbRef()
                ?.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        error("Firebase db error")
                    }

                    override fun onDataChange(snapshot: DataSnapshot?) {
                        if (snapshot != null) {
                            if (snapshot.value != null) {
                                val rootMap = snapshot.value as HashMap<String, *>








                                Realm.init(context)
                                val realm = Realm.getDefaultInstance()
                                closing(realm) {
                                    rootMap
                                            .filter { it.key == "TVShow" }.values
                                            .map { it as HashMap<*, *> }
                                            .flatMap { it.values }
                                            .map { mapper.convertValue(it, TVShow::class.java) }
                                            .forEach {
                                                tvShow ->
                                                val tv: TVShow? = realm.where(TVShow::class.java).equalTo("id", tvShow.id).findFirst()
                                                if (tv == null) {
                                                    val newTvShow = realm.createObject(TVShow::class.java, tvShow.id)
                                                    newTvShow.tvShowName = tvShow.tvShowName
                                                }
                                            }
                                    rootMap
                                            .filter { it.key == "TVSeason" }.values
                                            .map { it as HashMap<*, *> }
                                            .flatMap { it.values }
                                            .map { mapper.convertValue(it, TVSeason::class.java) }
                                            .forEach {
                                                tvSeason ->
                                                val season: TVSeason? = realm.where(TVSeason::class.java).equalTo("id", tvSeason.id).findFirst()
                                                if (season == null) {
                                                    val newSeason = realm.createObject(TVSeason::class.java, tvSeason.id)
                                                    newSeason.seasonName = tvSeason.seasonName
                                                    newSeason.tvShowId = tvSeason.tvShowId
                                                }
                                            }


                                    rootMap
                                            .filter { it.key == "TVEpisode" }.values
                                            .map { it as HashMap<*, *> }
                                            .flatMap { it.values }
                                            .map { mapper.convertValue(it, TVEpisode::class.java) }
                                            .forEach {
                                                tvEpisode ->
                                                val episode: TVEpisode? = realm.where(TVEpisode::class.java).equalTo("id", tvEpisode.id).findFirst()
                                                if (episode == null) {
                                                    val newEpisode = realm.createObject(TVEpisode::class.java, tvEpisode.id)
                                                    newEpisode.episodeName = tvEpisode.episodeName
                                                    newEpisode.tvSeasonId = tvEpisode.tvSeasonId
                                                    newEpisode.isWatched = tvEpisode.isWatched
                                                }

                                            }
                                    rootMap
                                            .filter { it.key == "Anime" }.values
                                            .map { it as HashMap<*, *> }
                                            .flatMap { it.values }
                                            .map { mapper.convertValue(it, Anime::class.java) }
                                            .forEach {
                                                anime ->
                                                val animeDB: Anime? = realm.where(Anime::class.java).equalTo("id", anime.id).findFirst()
                                                if (animeDB == null) {
                                                    val newAnime = realm.createObject(Anime::class.java, anime.id)
                                                    newAnime.animeName = anime.animeName
                                                    newAnime.episodeLastWatched = anime.episodeLastWatched
                                                }
                                            }
                                    rootMap
                                            .filter { it.key == "Movie" }.values
                                            .map { it as HashMap<*, *> }
                                            .flatMap { it.values }
                                            .map { mapper.convertValue(it, Movie::class.java) }
                                            .forEach {
                                                movie ->
                                                val movieDB = realm.where(Movie::class.java).equalTo("id", movie.id).findFirst()
                                                if (movieDB == null) {
                                                    val newMovie = realm.createObject(Movie::class.java, movie.id)
                                                    newMovie.releaseDate = movie.releaseDate
                                                    newMovie.name = movie.name
                                                    newMovie.watched = movie.watched
                                                }

                                            }

                                    realm.commitTransaction()
                                    complete()
                                }

                            }
                        }

                    }
                })
    }

}



















