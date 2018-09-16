package com.example.rakesh.CanvasExample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class CircularImageActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular_image);
        imageView = findViewById(R.id.imageView);
        getSupportActionBar().setTitle("CircularImageActivity");
        //Drawable drawable = ContextCompat.getDrawable(this,R.drawable.screenshot);
        //Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
        //OR
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.screenshot);

        //imageView.setImageDrawable(roundedBitmapDrawable);
        imageView.setImageDrawable(createCircularImageWithBorder(bitmap));
    }

    private RoundedBitmapDrawable createCircularImage(Bitmap bitmap) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
        roundedBitmapDrawable.setCircular(true);
        return roundedBitmapDrawable;
    }

    private RoundedBitmapDrawable createCircularImageWithBorder(Bitmap bitmap) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int borderWidth = 50;

        int squareImageSize = Math.min(width, height);
        int radius = squareImageSize / 2;

        Bitmap roundedBitmap = Bitmap.createBitmap(squareImageSize, squareImageSize, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(roundedBitmap);
        canvas.drawColor(ContextCompat.getColor(this, R.color.colorAccent));
        canvas.drawBitmap(bitmap, (squareImageSize - width) / 2, (squareImageSize - height) / 2, null);

        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        canvas.drawCircle(canvas.getWidth() / 2, canvas.getHeight() / 2, radius, borderPaint);

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), roundedBitmap);
        roundedBitmapDrawable.setAntiAlias(true);
        roundedBitmapDrawable.setCornerRadius(radius);
        return roundedBitmapDrawable;
    }

    public void next(View view) {
        startActivity(new Intent(this, CanvasActivity.class));
    }
}
