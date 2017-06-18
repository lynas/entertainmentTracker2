package com.lynas.entertainmenttracker2.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by lynas
 * on 5/22/2017...
 */

@JsonIgnoreProperties(ignoreUnknown = true)
open class TVShow() : RealmObject() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var tvShowName: String = ""

    constructor(id: String, tvShowName: String) : this() {
        this.id = id
        this.tvShowName = tvShowName
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
open class TVSeason() : RealmObject() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var seasonName: String = ""
    var tvShowId: String = ""

    constructor(id: String, seasonName: String, tvShowId: String) : this() {
        this.id = id
        this.seasonName = seasonName
        this.tvShowId = tvShowId
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
open class TVEpisode() : RealmObject() {
    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var episodeName: String = ""
    var tvSeasonId: String = ""
    var isWatched: Boolean = false

    constructor(id: String, episodeName: String, tvSeasonId: String, isWatched: Boolean) : this() {
        this.id = id
        this.episodeName = episodeName
        this.tvSeasonId = tvSeasonId
        this.isWatched = isWatched
    }
}