package com.example.rakesh.CanvasExample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DrawingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No XML file; just one custom view created programmatically.
        MyCanvasView myCanvasView = new MyCanvasView(this);
        setContentView(myCanvasView);
    }
}
