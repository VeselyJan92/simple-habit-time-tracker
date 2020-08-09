package com.janvesely.activitytracker.ui.views;

import android.graphics.Color;
import kotlin.KotlinVersion;

public abstract class ColorUtils {
    public static int mixColors(int i, int i2, float f) {
        float f2 = 1.0f - f;
        return ((((int) ((((float) (i & KotlinVersion.MAX_COMPONENT_VALUE)) * f) + (((float) (i2 & KotlinVersion.MAX_COMPONENT_VALUE)) * f2))) & KotlinVersion.MAX_COMPONENT_VALUE) << 0) | ((((int) ((((float) ((i >> 24) & KotlinVersion.MAX_COMPONENT_VALUE)) * f) + (((float) ((i2 >> 24) & KotlinVersion.MAX_COMPONENT_VALUE)) * f2))) & KotlinVersion.MAX_COMPONENT_VALUE) << 24) | ((((int) ((((float) ((i >> 16) & KotlinVersion.MAX_COMPONENT_VALUE)) * f) + (((float) ((i2 >> 16) & KotlinVersion.MAX_COMPONENT_VALUE)) * f2))) & KotlinVersion.MAX_COMPONENT_VALUE) << 16) | ((((int) ((((float) ((i >> 8) & KotlinVersion.MAX_COMPONENT_VALUE)) * f) + (((float) ((i2 >> 8) & KotlinVersion.MAX_COMPONENT_VALUE)) * f2))) & KotlinVersion.MAX_COMPONENT_VALUE) << 8);
    }

    public static int setAlpha(int i, float f) {
        return Color.argb((int) (f * 255.0f), Color.red(i), Color.green(i), Color.blue(i));
    }

    public static int setMinValue(int i, float f) {
        float[] fArr = new float[3];
        Color.colorToHSV(i, fArr);
        fArr[2] = Math.max(fArr[2], f);
        return Color.HSVToColor(fArr);
    }
}
