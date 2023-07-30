package com.imfibit.activitytracker.core

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

private const val USER_PREFERENCES_NAME = "app_settings"

val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)


object PreferencesKeys {
    val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    val ASK_FOR_NOTIFICATION = booleanPreferencesKey("ask_for_notifications")
    val ERASE_OBOARDING_SHOW = booleanPreferencesKey("erase_onboarding_data_card")
}