package com.dat.map_android.models

import java.io.Serializable

/**
 * A Data Model Class for Happy Place details. We will you this data class in all over the project even when
 * dealing with local SQLite database.
 */
data class HappyPlaceModel(
    val id: Int,
    val title: String,
    val image: String,
    val description: String,
    var location: String,
    val latitude: Double,
    val longitude: Double
) : Serializable{
    var distance : Long? = null
}