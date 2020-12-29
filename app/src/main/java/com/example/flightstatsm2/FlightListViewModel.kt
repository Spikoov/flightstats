package com.example.flightstatsm2

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by sergio on 19/11/2020
 * All rights reserved GoodBarber
 */
class FlightListViewModel : ViewModel(), RequestsManager.RequestListener {


    val flightListLiveData: MutableLiveData<List<FlightModel>> = MutableLiveData()
    val trackLiveData: MutableLiveData<List<TrackModel>> = MutableLiveData()
    val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val selectedFlightNameLiveData: MutableLiveData<String> = MutableLiveData()
    private val selectedIcaoLiveData: MutableLiveData<String> = MutableLiveData()
    private val selectedArrivalTimeLiveData: MutableLiveData<Long> = MutableLiveData()

    fun getSelectedArrivalTimeLiveData(): LiveData<Long> {
        return selectedArrivalTimeLiveData
    }

    fun getSelectedIcaoLiveData(): LiveData<String> {
        return selectedIcaoLiveData
    }

    fun getSelectedFlightNameLiveData(): LiveData<String> {
        return selectedFlightNameLiveData
    }

    fun search(icao: String, isArrival: Boolean, begin: Long, end: Long) {

        val searchDataModel = SearchDataModel(
            isArrival,
            icao,
            begin,
            end
        )
        val baseUrl: String = if (isArrival) {
            "https://opensky-network.org/api/flights/arrival"
        } else {
            "https://opensky-network.org/api/flights/departure"
        }

        viewModelScope.launch {
            //start loading
            isLoadingLiveData.value = true
            val result = withContext(Dispatchers.IO) {
                RequestsManager.getSuspended(baseUrl, getRequestParams(searchDataModel))
            }
            //end loading
            isLoadingLiveData.value = false
            if (result == null) {
                Log.e("Request", "problem")

            } else {
                val flightList = Utils.getFlightListFromString(result)
                Log.d("models list", flightList.toString())
                flightListLiveData.value = flightList
            }

        }
        // SearchFlightsAsyncTask(this).execute(searchDataModel)
    }

    fun getSelectedFlightInfo(icao: String, time: Long) {
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
            //end loading
            isLoadingLiveData.value = false
            if (result == null) {
                Log.e("Request", "problem")

            } else {
                val track = Utils.getTrackFromString(result)
                Log.d("models list", track.toString())
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

    private fun getRequestParams(searchModel: SearchDataModel?): Map<String, String>? {
        val params = HashMap<String, String>()
        if (searchModel != null) {
            params["airport"] = searchModel.icao
            params["begin"] = searchModel.begin.toString()
            params["end"] = searchModel.end.toString()
        }
        return params
    }

    override fun onRequestSuccess(result: String?) {
        TODO("Not yet implemented")
    }

    override fun onRequestFailed() {
        TODO("Not yet implemented")
    }

    fun updateSelectedFlightName(flightName: String) {
        selectedFlightNameLiveData.value = flightName
    }

    fun updateSelectedFlightArrival(flightArrival: Long) {
        selectedArrivalTimeLiveData.value = flightArrival
    }

    fun updateSelectedIcao(icao: String) {
        selectedIcaoLiveData.value = icao
    }
}