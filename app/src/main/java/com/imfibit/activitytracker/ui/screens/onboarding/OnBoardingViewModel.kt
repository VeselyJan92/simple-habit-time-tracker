package com.imfibit.activitytracker.ui.screens.onboarding

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imfibit.activitytracker.core.PreferencesKeys
import com.imfibit.activitytracker.core.dataStore
import kotlinx.coroutines.launch

class OnBoardingViewModel : ViewModel() {



    fun cancelOnboarding(context: Context){
        viewModelScope.launch {
            context.dataStore.edit { settings ->
                settings[PreferencesKeys.ONBOARDING_COMPLETED] = true
            }
        }
    }


}