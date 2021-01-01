package com.example.flightstatsm2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_flight_list.*


class FlightListActivity : AppCompatActivity() {

    private lateinit var viewModel: FlightListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flight_list)

        viewModel = ViewModelProvider(this).get(FlightListViewModel::class.java)
        viewModel.search(
            intent.getStringExtra("icao")!!,
            intent.getBooleanExtra("isArrival", false),
            intent.getLongExtra("begin", 0),
            intent.getLongExtra("end", 0)
        )

        viewModel.getSelectedFlightLiveData().observe(this, {
            val mapFragment: MapFragment = MapFragment.newInstance()
            switchFragment(mapFragment)
        })

    }

    private fun switchFragment(newFragment : Fragment){
        val isMobile = detail_container == null

        //switch fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (isMobile) {
            transaction.add(R.id.activityContainer, newFragment)
            transaction.addToBackStack(null)

            transaction.commit()
        }
        else{
            transaction.add(R.id.detail_container, newFragment)
            transaction.addToBackStack(null)

            transaction.commit()
        }
    }

    fun onDetailsButtonClick(view: View) {
        //change fragment : map to details
        val flightDetailFragment: FlightDetailFragment = FlightDetailFragment.newInstance()
        switchFragment(flightDetailFragment)
    }
}