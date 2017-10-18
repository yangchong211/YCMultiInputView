package com.ns.yc.ycmultiinputview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ns.yc.ycmultiinputviewlib.MultiEditInputView;

public class MainActivity extends AppCompatActivity {

    private MultiEditInputView mev_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        mev_view = (MultiEditInputView) findViewById(R.id.mev_view);
        initMevView();
    }

    private void initMevView() {
        String contentText = mev_view.getContentText();
        String hintText = mev_view.getHintText();
    }
}
