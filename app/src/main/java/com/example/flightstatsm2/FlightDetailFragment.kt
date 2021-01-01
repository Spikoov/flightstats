package com.example.flightstatsm2

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_flight_detail.*
import kotlinx.android.synthetic.main.fragment_flight_list.*
import java.util.*

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FlightDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FlightDetailFragment : Fragment(), FlightListRecyclerAdapter.OnItemClickListener {

    private lateinit var viewModel: FlightListViewModel
    private lateinit var detailViewModel: FlightDetailsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(requireActivity()).get(FlightListViewModel::class.java)
        detailViewModel = ViewModelProvider(requireActivity()).get(FlightDetailsViewModel::class.java)

        detailViewModel.searchFlightInfos(
            viewModel.getSelectedFlightLiveData().value!!.icao24,
            viewModel.getSelectedFlightLiveData().value!!.lastSeen
        )
        detailViewModel.searchPreviousFlights(viewModel.getSelectedFlightLiveData().value!!.icao24)

        // TODO: 01/01/21 voir si il y  a assez de données pour cet écran (ou voir pour mettre un sélectionneur de temps et display sur une map)

        detailViewModel.stateLiveData.observe(this, {
            val states = it.states.get(0).asJsonArray
            flight_name.text = states.get(1).asString
            flight_icao.text = states.get(0).asString
            flight_origin_country.text = states.get(2).asString
        })

        detailViewModel.previousFlightsLiveData.observe(this, {
            if (it == null || it.isEmpty()) {
                //DISPLAY ERROR
            } else {
                val adapter = FlightListRecyclerAdapter()
                adapter.flightList = it
                adapter.onItemClickListener = this
                recyclerViewDetails.adapter = adapter
                recyclerViewDetails.layoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
            }
        })

        detailViewModel.isLoadingLiveData.observe(this, {
            if (it) {
                progressBarFrameDetails.visibility = View.VISIBLE
            } else {
                progressBarFrameDetails.visibility = View.INVISIBLE
            }
        })

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_flight_detail, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FlightDetailFragment.
         */
        @JvmStatic
        fun newInstance() =
            FlightDetailFragment().apply {

            }
    }

    override fun onItemClicked(selectedFlight: FlightModel) {
        viewModel.updateSelectedFlightLiveData(selectedFlight)
    }
}