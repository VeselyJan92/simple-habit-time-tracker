package com.janvesely.activitytracker.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.VectorAsset

//<path android:fillColor="#FF000000" android:pathData="
// M12,2
// C6.48,2 2,6.48 2,12
// s4.48,10 10,10 10,-4.48 10,-10
// S17.52,2 12,2
// z
// M10,16.5
// v-9
// l6,4.5 -6,4.5
// z"/>

val Icons.Filled.PlayCircle: VectorAsset
    get() {
        if (icon != null) return icon!!
        icon = materialIcon {
            materialPath {
                moveTo(12.0f, 2.0f)
                curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
                reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
                reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)


//                moveTo(8.0f, 5.0f)
//                verticalLineToRelative(14.0f)
//                lineToRelative(11.0f, -7.0f)
                close()
            }
        }
        return icon!!
    }

private var icon: VectorAsset? = null