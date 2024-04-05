package com.trips.project.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.os.Bundle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.libraries.places.api.Places
import com.trips.project.R

class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)
                Places.initialize(applicationContext, "AIzaSyBxN6oBonBPwykwxYnz2d9pXRhk4Ey_0nI")
                val bottomNavigationView1: BottomNavigationView
                bottomNavigationView1 = findViewById(R.id.bottom_navigation1)
                val navController = findNavController(this, R.id.nav_host_fragment)
                bottomNavigationView1.setupWithNavController( navController)
        }
}
