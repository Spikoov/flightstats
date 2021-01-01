package com.example.flightstatsm2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.flight_cell.*
import kotlinx.android.synthetic.main.fragment_flight_detail.*
import kotlinx.android.synthetic.main.fragment_flight_list.*
import kotlinx.android.synthetic.main.fragment_map.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private lateinit var viewModel: FlightListViewModel
    private lateinit var trackViewModel: TrackViewModel

    private lateinit var map: GoogleMap

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
        trackViewModel = ViewModelProvider(requireActivity()).get(TrackViewModel::class.java)

        val myView : View = inflater.inflate(R.layout.fragment_map, container, false)

        val mapView = myView.findViewById(R.id.myMapView) as MapView

        trackViewModel.search(
            viewModel.getSelectedIcaoLiveData().value!!,
            viewModel.getSelectedArrivalTimeLiveData().value!!
        )

        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync(this)

        trackViewModel.isLoadingLiveData.observe(this, {
            if (it) {
                progressBarFrame.visibility = View.VISIBLE
                details_button.visibility = View.INVISIBLE
            } else {
                progressBarFrame.visibility = View.INVISIBLE
                details_button.visibility = View.VISIBLE
            }
        })

        // Inflate the layout for this fragment
        return myView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.setAllGesturesEnabled(false)
        map.setOnMapLoadedCallback(this)
    }

    override fun onMapLoaded() {
        trackViewModel.trackLiveData.observe(this, {
            if(trackViewModel.isLoadingLiveData.value == false) {
                map.clear()
                map.uiSettings.setAllGesturesEnabled(true)

                //marker of departure airport
                val departureMarker = map.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                it.path.first().asJsonArray.get(1).asDouble,
                                it.path.first().asJsonArray.get(2).asDouble
                            )
                        )
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(generateMarkerIcon(R.drawable.departure)))
                )

                //marker of arrival airport
                val arrivalMarker = map.addMarker(
                    MarkerOptions()
                        .position(
                            LatLng(
                                it.path.last().asJsonArray.get(1).asDouble,
                                it.path.last().asJsonArray.get(2).asDouble
                            )
                        )
                        .draggable(false)
                        .icon(BitmapDescriptorFactory.fromBitmap(generateMarkerIcon(R.drawable.arrival)))
                )

                // draw path line
                for(p in it.path.zipWithNext()){
                    map.addPolyline(PolylineOptions()
                        .add(
                            LatLng(
                                p.first.asJsonArray.get(1).asDouble,
                                p.first.asJsonArray.get(2).asDouble
                            ),
                            LatLng(
                                p.second.asJsonArray.get(1).asDouble,
                                p.second.asJsonArray.get(2).asDouble
                            )
                        )
                        .color(0xff0000ff.toInt())
                    )
                }

                //moving camera
                val builder : LatLngBounds.Builder = LatLngBounds.Builder()
                builder.include(departureMarker.position)
                builder.include(arrivalMarker.position)

                val bounds : LatLngBounds = builder.build()

                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 400))
            }
        })
    }

    private fun generateMarkerIcon(drawable: Int) : Bitmap {
        val bitmap = BitmapFactory.decodeResource(resources, drawable)
        return Bitmap.createScaledBitmap(bitmap, 150, 150, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MapFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            MapFragment().apply {

            }
    }
}