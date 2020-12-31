package com.example.flightstatsm2

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrackViewModel : ViewModel(), RequestsManager.RequestListener {

    val trackLiveData: MutableLiveData<TrackModel> = MutableLiveData()
    val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun search(icao: String, time: Long) {
        val searchTrackDataModel = SearchTrackDataModel(
            icao,
            time
        )

        val baseUrl: String = "https://opensky-network.org/api/tracks/all"

        viewModelScope.launch {
            //start loading
            isLoadingLiveData.value = true
            val result = withContext(Dispatchers.IO) {
                RequestsManager.getSuspended(baseUrl, getRequestTrackParams(searchTrackDataModel))
            }
            // end loading
            isLoadingLiveData.value = false
            if (result == null) {
                Log.e("Request", "problem")

            } else {
                val track = Utils.getTrackFromString(result)
                Log.d("models track", track.toString())
                trackLiveData.value = track
            }
        }
    }

    private fun getRequestTrackParams(searchModel: SearchTrackDataModel?): Map<String, String>? {
        val params = HashMap<String, String>()
        if (searchModel != null) {
            params["icao24"] = searchModel.icao
            params["time"] = searchModel.time.toString()
        }
        return params
    }

    override fun onRequestSuccess(result: String?) {
        TODO("Not yet implemented")
    }

    override fun onRequestFailed() {
        TODO("Not yet implemented")
    }
}