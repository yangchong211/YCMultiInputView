package com.didi.app.nova.skeleton.conductor;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.ViewGroup;

import com.didi.app.nova.skeleton.conductor.embed.FragmentLifecycle;
import com.didi.app.nova.skeleton.conductor.internal.FragmentLifecycleHandler;
import com.didi.app.nova.skeleton.conductor.internal.LifecycleHandler;
import com.didi.app.nova.skeleton.conductor.internal.ThreadUtils;


/**
 * Point of initial interaction with Conductor. Used to attach a {@link Router} to your Activity.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class Conductor {

    private Conductor() {
    }

    /**
     * Conductor will create a {@link Router} that has been initialized for your Activity and containing ViewGroup.
     * If an existing {@link Router} is already associated with this Activity/ViewGroup pair, either in memory
     * or in the savedInstanceState, that router will be used and rebound instead of creating a new one with
     * an empty backstack.
     *
     * @param activity           The Activity that will host the {@link Router} being attached.
     * @param container          The ViewGroup in which the {@link Router}'s {@link Controller} views will be hosted
     * @param savedInstanceState The savedInstanceState passed into the hosting Activity's onCreate method. Used
     *                           for restoring the Router's state if possible.
     * @return A fully configured {@link Router} instance for use with this Activity/ViewGroup pair.
     */
    @NonNull
    @UiThread
    public static Router attachRouter(@NonNull FragmentActivity activity, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        ThreadUtils.ensureMainThread();

        LifecycleHandler lifecycleHandler = LifecycleHandler.install(activity);

        Router router = lifecycleHandler.getRouter(container, savedInstanceState);
        router.rebindIfNeeded();

        return router;
    }

    /**
     * install Conductor into {@link Fragment}.
     * Call this method on {@link Fragment#onAttach(Activity)}
     *
     * @param fragment
     * @return FragmentLifecycle
     */
    @Nullable
    @UiThread
    public static FragmentLifecycle install(@NonNull Fragment fragment) {
        ThreadUtils.ensureMainThread();
        return FragmentLifecycleHandler.install(fragment);
    }

    /**
     * Call attachRouter after {@link Conductor#install}
     *
     * @param fragment
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @UiThread
    public static Router attachRouter(@NonNull Fragment fragment, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        ThreadUtils.ensureMainThread();
        Router router = FragmentLifecycleHandler.getFragmentRouter(fragment, container, savedInstanceState);
        router.rebindIfNeeded();
        return router;
    }
}
