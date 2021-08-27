package com.imfibit.activitytracker.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class AppViewModel : ViewModel() {

    inline fun launchIO(noinline block: suspend CoroutineScope.()->Unit) = viewModelScope.launch(Dispatchers.IO, block = block)

}