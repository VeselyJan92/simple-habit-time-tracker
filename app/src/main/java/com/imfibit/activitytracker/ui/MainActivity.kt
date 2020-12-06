package com.imfibit.activitytracker.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.imfibit.activitytracker.ui.Router

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { Router() }
    }
}