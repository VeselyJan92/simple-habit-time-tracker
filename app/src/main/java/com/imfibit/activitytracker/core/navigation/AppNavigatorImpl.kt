package com.imfibit.activitytracker.core.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.imfibit.activitytracker.core.PreferenceStore
import com.imfibit.activitytracker.ui.AppDestination
import com.imfibit.activitytracker.ui.Destinations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton


interface AppNavigator {

    val backStack: StateFlow<List<AppDestination>>

    fun navigate(update: (List<AppDestination>) -> List<AppDestination>)

    fun popBackStack()

    fun navigate(destination: AppDestination)
}

@InstallIn(SingletonComponent::class)
@Module
class AppNavigatorModule {

    @Provides
    @Singleton
    fun provideNavigation(
        settings: PreferenceStore,
    ): AppNavigator {
        return AppNavigatorImpl(settings)
    }
}

class AppNavigatorImpl(
    val settings: PreferenceStore
) : AppNavigator {

    private val _backStack = MutableStateFlow<List<AppDestination>>(emptyList())
    override val backStack = _backStack.asStateFlow()

    val startDestination: AppDestination = runBlocking {
        if (settings.getOnboarded() ?: false) Destinations.ScreenActivities else Destinations.ScreenOnboarding
    }

    init {
        _backStack.value = listOf(startDestination)
    }

    override fun navigate(update: (List<AppDestination>) -> List<AppDestination>) {
        _backStack.value = update(_backStack.value).toMutableList()
    }

    override fun popBackStack() = navigate {
        it.dropLast(1)
    }

    override fun navigate(destination: AppDestination) = navigate {
        it + destination
    }
}


@HiltViewModel
class BackstackViewModel @Inject constructor(
    appNavigatorImpl: AppNavigator,
) : ViewModel(), AppNavigator by appNavigatorImpl
