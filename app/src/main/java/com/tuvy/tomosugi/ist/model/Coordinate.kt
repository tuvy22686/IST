package com.tuvy.tomosugi.ist.model

import com.google.gson.annotations.SerializedName

/**
 * Created by tomosugi on 2017/09/16.
 */

data class Coordinate(
        @SerializedName("x") val x: Double,
        @SerializedName("y") val y: Double
)