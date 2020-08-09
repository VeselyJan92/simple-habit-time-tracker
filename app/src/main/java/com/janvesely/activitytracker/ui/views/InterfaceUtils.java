package com.janvesely.activitytracker.ui.views;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import androidx.core.view.ViewCompat;

public abstract class InterfaceUtils {
    private static Float fixedResolution;
    private static Typeface fontAwesome;

    public static void setFixedResolution(Float f) {
        fixedResolution = f;
    }

    public static Typeface getFontAwesome(Context context) {
        if (fontAwesome == null) {
            fontAwesome = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
        }
        return fontAwesome;
    }

    public static float dpToPixels(Context context, float f) {
        Float f2 = fixedResolution;
        if (f2 != null) {
            return f * f2.floatValue();
        }
        return TypedValue.applyDimension(1, f, context.getResources().getDisplayMetrics());
    }

    public static float spToPixels(Context context, float f) {
        Float f2 = fixedResolution;
        if (f2 != null) {
            return f * f2.floatValue();
        }
        return TypedValue.applyDimension(2, f, context.getResources().getDisplayMetrics());
    }

    public static float getDimension(Context context, int i) {
        float dimension = context.getResources().getDimension(i);
        if (fixedResolution == null) {
            return dimension;
        }
        return (dimension / context.getResources().getDisplayMetrics().density) * fixedResolution.floatValue();
    }

    public static void setupEditorAction(ViewGroup viewGroup, OnEditorActionListener onEditorActionListener) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ViewGroup) {
                setupEditorAction((ViewGroup) childAt, onEditorActionListener);
            }
            if (childAt instanceof TextView) {
                ((TextView) childAt).setOnEditorActionListener(onEditorActionListener);
            }
        }
    }

    public static boolean isLayoutRtl(View view) {
        return ViewCompat.getLayoutDirection(view) == 1;
    }
}
