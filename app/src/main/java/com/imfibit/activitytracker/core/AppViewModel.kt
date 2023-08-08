package com.imfibit.activitytracker.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.InvalidationTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

open class AppViewModel : ViewModel() {

    fun launchIO( block: suspend CoroutineScope.()->Unit) = viewModelScope.launch(Dispatchers.IO, block = block)

    fun <T> asyncIO( block: suspend CoroutineScope.()->T) = viewModelScope.async(Dispatchers.IO, block = block)





}