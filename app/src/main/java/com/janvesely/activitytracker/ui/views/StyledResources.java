package com.janvesely.activitytracker.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

public class StyledResources {
    private static Integer fixedTheme;
    private final Context context;

    public StyledResources(Context context2) {
        this.context = context2;
    }

    public static void setFixedTheme(Integer num) {
        fixedTheme = num;
    }

    public boolean getBoolean(int i) {
        TypedArray typedArray = getTypedArray(i);
        boolean z = typedArray.getBoolean(0, false);
        typedArray.recycle();
        return z;
    }

    public int getDimension(int i) {
        TypedArray typedArray = getTypedArray(i);
        int dimensionPixelSize = typedArray.getDimensionPixelSize(0, 0);
        typedArray.recycle();
        return dimensionPixelSize;
    }

    public int getColor(int i) {
        TypedArray typedArray = getTypedArray(i);
        int color = typedArray.getColor(0, 0);
        typedArray.recycle();
        return color;
    }

    public Drawable getDrawable(int i) {
        TypedArray typedArray = getTypedArray(i);
        Drawable drawable = typedArray.getDrawable(0);
        typedArray.recycle();
        return drawable;
    }

    public float getFloat(int i) {
        TypedArray typedArray = getTypedArray(i);
        float f = typedArray.getFloat(0, 0.0f);
        typedArray.recycle();
        return f;
    }



    public int getResource(int i) {
        TypedArray typedArray = getTypedArray(i);
        int resourceId = typedArray.getResourceId(0, -1);
        typedArray.recycle();
        return resourceId;
    }

    private TypedArray getTypedArray(int i) {
        int[] iArr = {i};
        if (fixedTheme != null) {
            return this.context.getTheme().obtainStyledAttributes(fixedTheme.intValue(), iArr);
        }
        return this.context.obtainStyledAttributes(iArr);
    }
}
