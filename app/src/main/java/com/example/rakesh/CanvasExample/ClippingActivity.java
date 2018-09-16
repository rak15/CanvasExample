package com.example.rakesh.CanvasExample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ClippingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyClippingView(this));
    }
}
