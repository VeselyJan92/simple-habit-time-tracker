package com.imfibit.activitytracker.ui.extensions

import androidx.compose.runtime.mutableStateOf
import com.imfibit.activitytracker.core.ComposeString

fun mutableComposeString(value: String = "-") = mutableStateOf<ComposeString>({"-"})
