package com.imfibit.activitytracker.core

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    val settings: PreferenceStore
) : ViewModel()