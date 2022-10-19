package com.imfibit.activitytracker.core.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.burnoutcrew.reorderable.ItemPosition

fun <T> MutableList<T>.swap(from: Int, to: Int) {
    val t = this[from]
    this[from] = this[to]
    this[to] = t
}


fun <T>  MutableState<List<T>>.swap(from: ItemPosition, to: ItemPosition){
    this.value = this.value.toMutableList().apply{
        swap(from.index, to.index)
    }
}


@Composable
fun <T> rememberReorderList(items: List<T>): MutableState<List<T>> {
    val state =  remember(items) {
        mutableStateOf(items)
    }
    return state
}