package com.janvesely.activitytracker

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

open class BaseFragment: Fragment(){

    lateinit var fContext: Context
    lateinit var fArgs: Bundle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fArgs = if (arguments == null) Bundle() else arguments!!
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fContext = context
    }

}