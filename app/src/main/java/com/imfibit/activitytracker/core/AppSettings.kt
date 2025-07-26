package com.imfibit.activitytracker.core

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import javax.inject.Inject
import javax.inject.Singleton


interface AppSettings {
    suspend fun getOnboarded(): Boolean? = false
    suspend fun setOnboarded(value: Boolean) { }

    suspend fun alreadyAksedForNotification(): Boolean? = false
    suspend fun setAlreadyAksedForNotification(value: Boolean) { }
}

private const val USER_PREFERENCES_NAME = "app_settings"

val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
val ASK_FOR_NOTIFICATION = booleanPreferencesKey("ask_for_notifications")


@InstallIn(SingletonComponent::class)
@Module
class PreferencesModule {

    @Provides
    @Singleton
    fun provideAppSettings(@ApplicationContext context: Context): PreferenceStore{
        return PreferenceStore(context)
    }
}


class PreferenceStore @Inject constructor(
    @param:ApplicationContext val context: Context
): AppSettings {

    val dataStore = context.dataStore

    private suspend fun <T> set(key: Preferences.Key<T>, value: T){
        dataStore.edit { settings -> settings[key] = value }
    }

    private suspend fun <T> get(key: Preferences.Key<T>): T? {
        return context.dataStore.data.first()[key]
    }

    override suspend fun getOnboarded() = get(ONBOARDING_COMPLETED)
    override suspend fun setOnboarded(value: Boolean) = set(ONBOARDING_COMPLETED, value)

    override suspend fun alreadyAksedForNotification() = get(ASK_FOR_NOTIFICATION)
    override suspend fun setAlreadyAksedForNotification(value: Boolean) = set(ASK_FOR_NOTIFICATION, value)

}