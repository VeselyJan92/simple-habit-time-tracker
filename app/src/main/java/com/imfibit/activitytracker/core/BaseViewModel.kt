package com.imfibit.activitytracker.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    fun launchIO( block: suspend CoroutineScope.()->Unit) = viewModelScope.launch(Dispatchers.IO, block = block)

    fun launchMain( block: suspend CoroutineScope.()->Unit) = viewModelScope.launch(Dispatchers.Main, block = block)

    fun <T> asyncIO( block: suspend CoroutineScope.()->T) = viewModelScope.async(Dispatchers.IO, block = block)

}