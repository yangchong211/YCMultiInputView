package com.ns.yc.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.bluelinelabs.conductor.Controller;


public class HomeController extends Controller {

    @androidx.annotation.NonNull
    @Override
    protected View onCreateView(@androidx.annotation.NonNull LayoutInflater inflater, @androidx.annotation.NonNull ViewGroup container, @Nullable Bundle savedViewState) {
        android.view.View view = inflater.inflate(R.layout.component_banner, container, false);
        ((android.widget.TextView)view.findViewById(R.id.banner)).setText("Hello World");
        android.util.Log.d("HomeController---","onCreateView");
        return view;
    }


    @Override
    protected void onRestoreInstanceState(@NonNull android.os.Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        android.util.Log.d("HomeController---","onRestoreInstanceState");
    }

    @Override
    protected void onRestoreViewState(@NonNull android.view.View view, @NonNull android.os.Bundle savedViewState) {
        super.onRestoreViewState(view, savedViewState);
        android.util.Log.d("HomeController---","onRestoreViewState");
    }

    @Override
    protected void onAttach(@NonNull android.view.View view) {
        super.onAttach(view);
        android.util.Log.d("HomeController---","onAttach");
    }


    @Override
    protected void onDetach(@NonNull android.view.View view) {
        super.onDetach(view);
        android.util.Log.d("HomeController---","onDetach");
    }

    @Override
    protected void onSaveViewState(@NonNull android.view.View view, @NonNull android.os.Bundle outState) {
        super.onSaveViewState(view, outState);
        android.util.Log.d("HomeController---","onSaveViewState");
    }

    @Override
    protected void onDestroyView(@NonNull android.view.View view) {
        super.onDestroyView(view);
        android.util.Log.d("HomeController---","onDestroyView");
    }

    @Override
    protected void onSaveInstanceState(@NonNull android.os.Bundle outState) {
        super.onSaveInstanceState(outState);
        android.util.Log.d("HomeController---","onSaveInstanceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.util.Log.d("HomeController---","onDestroy");
    }



}
