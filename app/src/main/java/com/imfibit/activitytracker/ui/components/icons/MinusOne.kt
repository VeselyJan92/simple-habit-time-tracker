package com.janvesely.activitytracker.ui.components.icons

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.VectorAsset


val Icons.Filled.MinusOne: VectorAsset
    get() {
        if (_MinusOne != null) {
            return _MinusOne!!
        }
        _MinusOne = materialIcon {
            materialPath {
                moveTo(19.0f, 13.0f)
                horizontalLineTo(5.0f)
                verticalLineToRelative(-2.0f)
                horizontalLineToRelative(9.0f)
                verticalLineToRelative(2.0f)
                close()
                moveTo(14.5f, 6.08f)
                lineTo(14.5f, 7.9f)
                lineToRelative(2.5f, -0.5f)
                lineTo(17.0f, 18.0f)
                horizontalLineToRelative(2.0f)
                lineTo(19.0f, 5.0f)
                close()
            }
        }
        return _MinusOne!!
    }

private var _MinusOne: VectorAsset? = null