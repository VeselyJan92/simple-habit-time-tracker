package com.janvesely.activitytracker.core

import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView

fun <T> MutableList<T>.swap(i: Int, j: Int) {
    with(this[i]) {
        this@swap[i] = this@swap[j]
        this@swap[j] = this
    }
}

fun RecyclerView.setDivider(@DrawableRes drawableRes: Int) {
    val divider = DividerItemDecoration(
        this.context,
        DividerItemDecoration.VERTICAL
    )
    val drawable = ContextCompat.getDrawable(
        this.context,
        drawableRes
    )
    drawable?.let {
        divider.setDrawable(it)
        addItemDecoration(divider)
    }
}

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