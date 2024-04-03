package com.trips.project.presentation.ui.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.trips.project.R
import com.trips.project.presentation.ui.adapter.DestinationAdapter
import com.trips.project.data.db.DestinationDatabse
import com.trips.project.data.model.DestinationViewModel
import com.trips.project.data.model.DestinationModelFactory

class DestinationListFragment : Fragment() {
    private lateinit var viewModel: DestinationViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_destination_list, container, false)
        initializeUI(rootView)
        setupViewModel()
        return rootView
    }

    private fun initializeUI(rootView: View) {
        recyclerView = rootView.findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 1)
            adapter = DestinationAdapter(requireActivity(), mutableListOf(), false)
        }

        rootView.findViewById<Button>(R.id.add_button).setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_destinationListFragment_to_addDestinationFragment)
        }
        val addButton: Button = rootView.findViewById(R.id.add_button)
        addButton.visibility = View.GONE

    }

    private fun setupViewModel() {
        val myDB = DestinationDatabse.getInstance(requireContext().applicationContext)
        val factory = DestinationModelFactory(myDB.destinationDao())
        viewModel = ViewModelProvider(this, factory).get(DestinationViewModel::class.java)

        viewModel.allDestinations.observe(viewLifecycleOwner, Observer { destinations ->
            (recyclerView.adapter as DestinationAdapter).destinations = destinations
            recyclerView.adapter?.notifyDataSetChanged()
        })

        if (isOnline()) {
            FirebaseAuth.getInstance().currentUser?.email?.let {
                viewModel.fetchDestinationsFromFirestore(
                    it
                )
            }
        } else {
            Toast.makeText(context, "Offline: Displaying cached destinations", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isOnline(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}
