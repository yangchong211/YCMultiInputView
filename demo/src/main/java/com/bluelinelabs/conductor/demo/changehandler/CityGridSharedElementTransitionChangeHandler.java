package com.bluelinelabs.conductor.demo.changehandler;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeClipBounds;
import androidx.transition.ChangeTransform;
import androidx.transition.Explode;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionSet;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.changehandler.androidxtransition.SharedElementTransitionChangeHandler;

import java.util.ArrayList;
import java.util.List;

public class CityGridSharedElementTransitionChangeHandler extends SharedElementTransitionChangeHandler {

    private static final String KEY_WAIT_FOR_TRANSITION_NAMES = "CityGridSharedElementTransitionChangeHandler.names";

    private final ArrayList<String> names;

    public CityGridSharedElementTransitionChangeHandler() {
        names = new ArrayList<>();
    }

    public CityGridSharedElementTransitionChangeHandler(@NonNull List<String> waitForTransitionNames) {
        names = new ArrayList<>(waitForTransitionNames);
    }

    @Override
    public void saveToBundle(@NonNull Bundle bundle) {
        bundle.putStringArrayList(KEY_WAIT_FOR_TRANSITION_NAMES, names);
    }

    @Override
    public void restoreFromBundle(@NonNull Bundle bundle) {
        List<String> savedNames = bundle.getStringArrayList(KEY_WAIT_FOR_TRANSITION_NAMES);
        if (savedNames != null) {
            names.addAll(savedNames);
        }
    }

    @Nullable
    public Transition getExitTransition(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush) {
        if (isPush) {
            return new Explode();
        } else {
            return new Slide(Gravity.BOTTOM);
        }
    }

    @Override
    @Nullable
    public Transition getSharedElementTransition(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush) {
        return new TransitionSet().addTransition(new ChangeBounds()).addTransition(new ChangeClipBounds()).addTransition(new ChangeTransform());
    }

    @Override
    @Nullable
    public Transition getEnterTransition(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush) {
        if (isPush) {
            return new Slide(Gravity.BOTTOM);
        } else {
            return new Explode();
        }
    }

    @Override
    public void configureSharedElements(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush) {
        for (String name : names) {
            addSharedElement(name);
            waitOnSharedElementNamed(name);
        }
    }

}
