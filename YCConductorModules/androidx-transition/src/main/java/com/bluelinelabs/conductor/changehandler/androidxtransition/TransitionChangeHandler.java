package com.bluelinelabs.conductor.changehandler.androidxtransition;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;

/**
 * A base {@link ControllerChangeHandler} that facilitates using {@link Transition}s to replace Controller Views.
 * <p/>
 * Note that this class uses the <b>androidx</b> {@link Transition}. If you're using Android's platform transitions,
 * consider using the {@code TransitionChangeHandler} provided by the {@code android-transitions} Conductor module.
 */
public abstract class TransitionChangeHandler extends ControllerChangeHandler {

    public interface OnTransitionPreparedListener {
        void onPrepared();
    }

    boolean canceled;
    private boolean needsImmediateCompletion;

    /**
     * Should be overridden to return the Transition to use while replacing Views.
     *
     * @param container The container these Views are hosted in
     * @param from      The previous View in the container or {@code null} if there was no Controller before this transition
     * @param to        The next View that should be put in the container or {@code null} if no Controller is being transitioned to
     * @param isPush    True if this is a push transaction, false if it's a pop
     */
    @NonNull
    protected abstract Transition getTransition(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, boolean isPush);

    @Override
    public void onAbortPush(@NonNull ControllerChangeHandler newHandler, @Nullable Controller newTop) {
        super.onAbortPush(newHandler, newTop);

        canceled = true;
    }

    @Override
    public void completeImmediately() {
        super.completeImmediately();

        needsImmediateCompletion = true;
    }

    @Nullable
    private ControllerChangeCompletedListener listener;

    @Override
    public void performChange(@NonNull final ViewGroup container, @Nullable final View from, @Nullable final View to, final boolean isPush, @NonNull final ControllerChangeCompletedListener changeListener) {
        listener = changeListener;
        if (canceled) {
            changeListener.onChangeCompleted();
            return;
        }
        if (needsImmediateCompletion) {
            executePropertyChanges(container, from, to, null, isPush);
            changeListener.onChangeCompleted();
            return;
        }

        final Runnable onTransitionNotStarted = new Runnable() {
            @Override
            public void run() {
                changeListener.onChangeCompleted();
            }
        };

        final Transition transition = getTransition(container, from, to, isPush);
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                container.removeCallbacks(onTransitionNotStarted);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                listener.onChangeCompleted();
                listener = null;
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                listener.onChangeCompleted();
                listener = null;
            }

            @Override
            public void onTransitionPause(Transition transition) { }

            @Override
            public void onTransitionResume(Transition transition) { }
        });

        prepareForTransition(container, from, to, transition, isPush, new OnTransitionPreparedListener() {
            @Override
            public void onPrepared() {
                if (!canceled) {
                    TransitionManager.beginDelayedTransition(container, transition);
                    executePropertyChanges(container, from, to, transition, isPush);
                    container.post(onTransitionNotStarted);
                }
            }
        });
    }

    @Override
    public boolean removesFromViewOnPush() {
        return true;
    }

    /**
     * Called before a transition occurs. This can be used to reorder views, set their transition names, etc. The transition will begin
     * when {@code onTransitionPreparedListener} is called.
     *
     * @param container  The container these Views are hosted in
     * @param from       The previous View in the container or {@code null} if there was no Controller before this transition
     * @param to         The next View that should be put in the container or {@code null} if no Controller is being transitioned to
     * @param transition The transition that is being prepared for
     * @param isPush     True if this is a push transaction, false if it's a pop
     */
    public void prepareForTransition(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, @NonNull Transition transition, boolean isPush, @NonNull OnTransitionPreparedListener onTransitionPreparedListener) {
        onTransitionPreparedListener.onPrepared();
    }

    /**
     * This should set all view properties needed for the transition to work properly. By default it removes the "from" view
     * and adds the "to" view.
     *
     * @param container  The container these Views are hosted in
     * @param from       The previous View in the container or {@code null} if there was no Controller before this transition
     * @param to         The next View that should be put in the container or {@code null} if no Controller is being transitioned to
     * @param transition The transition with which {@code TransitionManager.beginDelayedTransition} has been called. This will be null only if another ControllerChangeHandler immediately overrides this one.
     * @param isPush     True if this is a push transaction, false if it's a pop
     */
    public void executePropertyChanges(@NonNull ViewGroup container, @Nullable View from, @Nullable View to, @Nullable Transition transition, boolean isPush) {
        if (from != null && (removesFromViewOnPush() || !isPush) && from.getParent() == container) {
            container.removeView(from);
        }
        if (to != null && to.getParent() == null) {
            container.addView(to);
        }
    }

}
