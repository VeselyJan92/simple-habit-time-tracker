package com.imfibit.activitytracker.core

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

typealias ContextString = Context.() -> String

@Composable
fun ContextString.value(): String {
    val context = LocalContext.current
    return this.invoke(context)
}

fun ContextString.value(context: Context): String {
    return this.invoke(context)
}