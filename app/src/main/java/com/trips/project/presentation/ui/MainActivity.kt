package com.trips.project.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.navigation.Navigation.findNavController
import com.google.android.libraries.places.api.Places
import com.trips.project.R
import me.ibrahimsn.lib.SmoothBottomBar

class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main)
                Places.initialize(applicationContext, "AIzaSyBxN6oBonBPwykwxYnz2d9pXRhk4Ey_0nI")
                val bottomNavigationView1: BottomNavigationView
                val menu: Menu
                bottomNavigationView1 = findViewById(R.id.bottom_navigation1)
                menu = bottomNavigationView1.menu
                val bottomNavigationView = findViewById<SmoothBottomBar>(R.id.bottom_navigation)
                bottomNavigationView.visibility = View.VISIBLE
                val navController = findNavController(this, R.id.nav_host_fragment)
                bottomNavigationView.setupWithNavController(menu , navController)
        }
}
