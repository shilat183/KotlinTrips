package com.trips.project.presentation.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trips.project.R

class DestinationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val destinationImage: ImageView = itemView.findViewById(R.id.destinationImage)
    val destinationName: TextView = itemView.findViewById(R.id.name)
}
