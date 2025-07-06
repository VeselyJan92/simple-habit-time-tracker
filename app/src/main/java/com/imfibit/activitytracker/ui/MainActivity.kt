package com.imfibit.activitytracker.ui


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.imfibit.activitytracker.core.notifications.NotificationLiveSession
import com.imfibit.activitytracker.core.notifications.NotificationTimerOver
import com.imfibit.activitytracker.database.AppDatabase
import com.imfibit.activitytracker.ui.components.Colors
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val SCREEN_FOCUS_BOARD_PAGER_ID = 0
const val SCREEN_ACTIVITIES_PAGER_ID = 1
const val SCREEN_MIND_BOOT_ID = 2

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var db: AppDatabase

    companion object {
        const val NOTIFICATION_NAVIGATE_TO_ACTIVITY = "NAV_TO_ACTIVITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        NotificationTimerOver.createChannel(this)
        NotificationLiveSession.createChannel(this)

        setContent {
            AppTheme {
                AppNavHost()
            }
        }
    }

}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            background = Colors.AppBackground,
            surface = Color.White,
            surfaceContainerLow = Color.White
        )
    ) {
        content()
    }
}

val LocalNavController = compositionLocalOf<NavHostController> {
    error("No LocalNavController provided")
}

@Composable
fun DashboardBody(content: @Composable (ColumnScope.() -> Unit)) {
    Column(modifier = Modifier.fillMaxSize(), content = content)
}


