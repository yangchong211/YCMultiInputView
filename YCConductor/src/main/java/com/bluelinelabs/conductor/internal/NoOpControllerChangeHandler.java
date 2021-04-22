package com.didi.app.nova.skeleton.conductor.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.didi.app.nova.skeleton.conductor.ControllerChangeHandler;


public class NoOpControllerChangeHandler extends ControllerChangeHandler {

    @Override
    public void performChange(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush, @NonNull ControllerChangeCompletedListener changeListener) {
        changeListener.onChangeCompleted();
    }

    @NonNull
    @Override
    public ControllerChangeHandler copy() {
        return new NoOpControllerChangeHandler();
    }

    @Override
    public boolean isReusable() {
        return true;
    }
}
