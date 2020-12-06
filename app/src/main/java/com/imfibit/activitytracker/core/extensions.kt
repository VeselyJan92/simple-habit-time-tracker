package com.imfibit.activitytracker.core

import androidx.compose.runtime.Composable


fun <T> Array<T>.leftShift(d: Int): Array<T> {
    val newList = this.copyOf()
    var shift = d
    if (shift > size) shift %= size
    forEachIndexed { index, value ->
        val newIndex = (index + (size - shift)) % size
        newList[newIndex] = value
    }
    return newList
}


typealias ComposeString = @Composable ()->String


inline fun <T> Iterable<T>.sumByLong(selector: (T) -> Long): Long {
    var sum = 0L
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
