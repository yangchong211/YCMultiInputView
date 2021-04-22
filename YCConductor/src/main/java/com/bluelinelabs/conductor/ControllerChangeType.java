package com.didi.app.nova.skeleton.conductor;

import android.support.annotation.RestrictTo;

/**
 * All possible types of {@link Controller} changes to be used in {@link ControllerChangeHandler}s
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public enum ControllerChangeType {
    /**
     * The Controller is being pushed to the host container
     */
    PUSH_ENTER(true, true),

    /**
     * The Controller is being pushed to the backstack as another Controller is pushed to the host container
     */
    PUSH_EXIT(true, false),

    /**
     * The Controller is being popped from the backstack and placed in the host container as another Controller is popped
     */
    POP_ENTER(false, true),

    /**
     * The Controller is being popped from the host container
     */
    POP_EXIT(false, false);

    public boolean isPush;
    public boolean isEnter;

    ControllerChangeType(boolean isPush, boolean isEnter) {
        this.isPush = isPush;
        this.isEnter = isEnter;
    }
}
