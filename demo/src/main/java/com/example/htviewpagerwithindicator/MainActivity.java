package com.example.htviewpagerwithindicator;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_top).setOnClickListener(this);
        findViewById(R.id.button_bottom).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_top:
                DefaultSlidingTabLayoutActivity.start(this, 0);
                break;
            case R.id.button_bottom:
                DefaultSlidingTabLayoutActivity.start(this, 1);
                break;
            default:
                break;
        }
    }
}
