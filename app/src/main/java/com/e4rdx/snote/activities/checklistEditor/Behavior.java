package com.e4rdx.snote.activities.checklistEditor;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

public class Behavior extends CoordinatorLayout.Behavior<LinearLayout> {

    public Behavior(Context context, AttributeSet attrs) {

    }

    @Override
    public boolean layoutDependsOn(@NotNull CoordinatorLayout parent, @NotNull LinearLayout child, @NotNull View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(@NotNull CoordinatorLayout parent, LinearLayout child, View dependency) {
        float translationY = Math.min(0, dependency.getTranslationY() - dependency.getHeight());
        child.setTranslationY(translationY);
        return true;
    }

    @Override
    public void onDependentViewRemoved(@NonNull CoordinatorLayout parent, @NonNull LinearLayout child, @NonNull View dependency) {
        super.onDependentViewRemoved(parent, child, dependency);
        float translationY = Math.min(0, 0);
        child.setTranslationY(translationY);
    }
}
