package com.imfibit.activitytracker.core

import androidx.compose.ui.graphics.Color


inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

fun Int.toColor() = Color(this)




