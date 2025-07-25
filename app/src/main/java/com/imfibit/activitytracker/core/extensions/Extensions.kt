package com.imfibit.activitytracker.core.extensions

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

fun <T> MutableList<T>.swap(from: Int, to: Int) {
    val t = this[from]
    this[from] = this[to]
    this[to] = t
}

fun <T>  MutableStateFlow<List<T>>.swap(from: LazyListItemInfo, to: LazyListItemInfo){
    this.value = this.value.toMutableList().apply { swap(from.index, to.index) }
}

fun <T>  MutableStateFlow<List<T>>.swap(from: Int, to: Int){
    this.value = this.value.toMutableList().apply { swap(from, to) }
}

private val alphanumeric = ('A'..'Z') + ('a'..'z') + ('0'..'9')

fun Random.randomString(length: Int) = buildString {
        repeat(length) { append(alphanumeric.random()) }
}
