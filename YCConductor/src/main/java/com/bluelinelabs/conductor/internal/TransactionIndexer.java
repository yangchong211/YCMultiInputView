package com.didi.app.nova.skeleton.conductor.internal;

import android.os.Bundle;
import android.support.annotation.NonNull;

public class TransactionIndexer {

    private static final String KEY_INDEX = "TransactionIndexer.currentIndex";

    private int currentIndex;

    public int nextIndex() {
        return ++currentIndex;
    }

    public void saveInstanceState(@NonNull Bundle outState) {
        outState.putInt(KEY_INDEX, currentIndex);
    }

    public void restoreInstanceState(@NonNull Bundle savedInstanceState) {
        currentIndex = savedInstanceState.getInt(KEY_INDEX);
    }

}
