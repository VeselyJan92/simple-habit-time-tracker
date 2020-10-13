package com.imfibit.activitytracker

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.selectedItemId = R.id.navigation_activities_list


        val navController = findNavController(R.id.nav_host_fragment)


        navView.setupWithNavController(navController)

        navView.selectedItemId = R.id.navigation_activities_list

    }
}