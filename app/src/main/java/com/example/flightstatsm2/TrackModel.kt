package com.example.flightstatsm2

import com.google.gson.JsonArray
import org.json.JSONArray

data class TrackModel ( val icao24: String,
                        val startTime: Long,
                        val endTime: Long,
                        val callsign: String,
                        val path: JsonArray
)