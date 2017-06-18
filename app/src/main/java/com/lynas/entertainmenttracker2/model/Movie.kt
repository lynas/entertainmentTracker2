package com.lynas.entertainmenttracker2.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by lynas on 6/2/2017..
 */

@JsonIgnoreProperties(ignoreUnknown = true)
open class Movie() : RealmObject() {

    @PrimaryKey
    var id: String = UUID.randomUUID().toString()
    var name: String = ""
    var releaseDate: Long = 0L
    var watched: Boolean = false

    constructor(id: String, name: String, releaseDate: Long, watched: Boolean) : this() {
        this.id = id
        this.name = name
        this.releaseDate = releaseDate
        this.watched = watched
    }
}