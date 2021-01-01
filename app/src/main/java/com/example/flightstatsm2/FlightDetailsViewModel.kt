package com.example.flightstatsm2

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap

class FlightDetailsViewModel : ViewModel(), RequestsManager.RequestListener {
    val previousFlightsLiveData: MutableLiveData<List<FlightModel>> = MutableLiveData()
    val stateLiveData: MutableLiveData<StateModel> = MutableLiveData()

    val isLoadingLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun searchFlightInfos(icao: String, time: Long){
        val searchDetailsDataModel = SearchStateDataModel(
            icao,
            time
        )

        val baseUrl : String = "https://opensky-network.org/api/states/all"

        viewModelScope.launch {
            //start loading
            isLoadingLiveData.value = true
            val result = withContext(Dispatchers.IO) {
                RequestsManager.getSuspended(baseUrl, getRequestStateParams(searchDetailsDataModel))
            }
            // end loading
            isLoadingLiveData.value = false
            if (result == null) {
                Log.e("Request", "problem")

            } else {
                val state = Utils.getStateFromString(result)
                Log.d("models state", state.toString())
                stateLiveData.value = state
            }
        }
    }

    private fun getRequestStateParams(searchModel: SearchStateDataModel?): Map<String, String>? {
        val params = HashMap<String, String>()
        if (searchModel != null) {
            params["icao24"] = searchModel.icao
            params["time"] = searchModel.time.toString()
        }
        return params
    }

    fun searchPreviousFlights(icao: String){
        val searchDetailsDataModel = SearchPreviousFlightsDataModel(
            icao
        )

        val baseUrl: String = "https://opensky-network.org/api/flights/aircraft"

        viewModelScope.launch {
            //start loading
            isLoadingLiveData.value = true
            val result = withContext(Dispatchers.IO) {
                RequestsManager.getSuspended(baseUrl, getRequestDetailsParams(searchDetailsDataModel))
            }
            // end loading
            isLoadingLiveData.value = false
            if (result == null) {
                Log.e("Request", "problem")

            } else {
                val details = Utils.getFlightListFromString(result)
                Log.d("models previous flights", details.toString())
                previousFlightsLiveData.value = details
            }
        }
    }

    private fun getRequestDetailsParams(searchModel: SearchPreviousFlightsDataModel?): Map<String, String>? {
        val params = HashMap<String, String>()
        if (searchModel != null) {
            params["icao24"] = searchModel.icao
            params["begin"] = ((Calendar.getInstance().timeInMillis / 1000) - (3600*24*3)).toString()
            params["end"] = (Calendar.getInstance().timeInMillis / 1000).toString()
        }
        return params
    }

    override fun onRequestSuccess(result: String?) {

    }

    override fun onRequestFailed() {

    }

}