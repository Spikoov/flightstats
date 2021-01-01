package com.example.flightstatsm2

import com.google.gson.JsonArray

data class StateModel (
    val time: Long,
    val states: JsonArray
)