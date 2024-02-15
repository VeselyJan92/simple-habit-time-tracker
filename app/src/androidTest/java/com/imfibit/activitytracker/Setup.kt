package com.imfibit.activitytracker

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import com.imfibit.activitytracker.core.AppSettings
import com.imfibit.activitytracker.core.PreferenceStore
import com.imfibit.activitytracker.core.PreferencesModule
import com.imfibit.activitytracker.core.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.runBlocking
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PreferencesModule::class]
)
class FakeAnalyticsModule {

    @Provides
    @Singleton
    fun provideAppSettings(@ApplicationContext context: Context): AppSettings = runBlocking {
        context.dataStore.edit { it.clear() }
        PreferenceStore(context)
    }
}