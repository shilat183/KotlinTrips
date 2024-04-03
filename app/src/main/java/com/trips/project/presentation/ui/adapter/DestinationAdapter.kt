package com.trips.project.presentation.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.trips.project.R
import com.trips.project.data.model.DestinationModel
import com.squareup.picasso.Picasso

class DestinationAdapter(
    private val fragmentActivity: FragmentActivity,
    var destinations: List<DestinationModel>,
    private val isEdit: Boolean
) : RecyclerView.Adapter<DestinationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_destination, parent, false)
        return DestinationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        val destination = destinations[position]
        Picasso.get().load(destination.imageUrl).into(holder.destinationImage)
        holder.destinationName.text = destination.name
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("destinationId", destination.key)
                putString("name", destination.name)
                putString("userEmail", destination.userEmail)
                putString("imageUrl", destination.imageUrl)
                putString("menu", destination.menu)
                putString("address", destination.address)
                putString("foodtype", destination.foodtype)
                putString("review", destination.review)
                putBoolean("favourite", destination.favourite)
                putBoolean("isEdit", isEdit)
            }
            val navController = fragmentActivity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navController.navController.navigate(R.id.destinationDetailFragment, bundle)
        }
    }

    override fun getItemCount(): Int {
        return destinations.size
    }
}
