package com.lynas.entertainmenttracker2.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by sazzad on 6/1/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
open class Anime() : RealmObject() {

    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var animeName: String = ""
    var episodeLastWatched: Int = 1

    constructor(id: String, animeName: String, episodeLastWatched: Int) : this() {
        this.id = id
        this.animeName = animeName
        this.episodeLastWatched = episodeLastWatched
    }
}