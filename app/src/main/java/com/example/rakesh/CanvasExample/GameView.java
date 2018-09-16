package com.example.rakesh.CanvasExample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {
    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Path mPath;
    private int mBitmapX;
    private int mBitmapY;
    private RectF mWinnerRect;
    private int mViewWidth;
    private int mViewHeight;
    private Bitmap mBitmap;
    private boolean mRunning;
    private Thread mGameThread;
    private FlashLightCone mFlashLightCone;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        mPaint.setColor(Color.DKGRAY);
        mPath = new Path();
    }

    /**
     * We cannot get the correct dimensions of views in onCreate because
     * they have not been inflated yet. This method is called every time the
     * size of a view changes, including the first time after it has been
     * inflated.
     *
     * @param w Current width of view.
     * @param h Current height of view.
     * @param oldw Previous width of view.
     * @param oldh Previous height of view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mViewWidth = w;
        mViewHeight = h;

        mFlashLightCone = new FlashLightCone(mViewWidth, mViewHeight);

        // Set font size proportional to view size.
        mPaint.setTextSize(mViewHeight / 5);

        mBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.android);
        setUpBitmap();
    }



    @Override
    public void run() {
        Canvas canvas;

        while (mRunning) {
            // If we can obtain a valid drawing surface...
            if (mSurfaceHolder.getSurface().isValid()) {

                // Helper variables for performance.
                int x = mFlashLightCone.getX();
                int y = mFlashLightCone.getY();
                int radius = mFlashLightCone.getRadius();

                // Lock the canvas. Note that in a more complex app, with
                // more threads, you need to put this into a try/catch block
                // to make sure only one thread is drawing to the surface.
                // Starting with O, you can request a hardware surface with
                //    lockHardwareCanvas().
                // See https://developer.android.com/reference/android/view/
                //    SurfaceHolder.html#lockHardwareCanvas()
                canvas = mSurfaceHolder.lockCanvas();

                // Fill the canvas with white and draw the bitmap.
                canvas.save();
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(mBitmap, mBitmapX, mBitmapY, mPaint);

                // Add clipping region and fill rest of the canvas with black.
                mPath.addCircle(x, y, radius, Path.Direction.CCW);
                canvas.clipPath(mPath, Region.Op.DIFFERENCE);
                canvas.drawColor(Color.BLACK);

                // If the x, y coordinates of the user touch are within a
                //  bounding rectangle, display the winning message.
                if (x > mWinnerRect.left && x < mWinnerRect.right
                        && y > mWinnerRect.top && y < mWinnerRect.bottom) {
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(mBitmap, mBitmapX, mBitmapY, mPaint);
                    canvas.drawText(
                            "WIN!", mViewWidth / 3, mViewHeight / 2, mPaint);
                }
                // Clear the path data structure.
                mPath.rewind();
                // Restore the previously saved (default) clip and matrix state.
                canvas.restore();
                // Release the lock on the canvas and show the surface's
                // contents on the screen.
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     * Calculates a randomized location for the bitmap
     * and the winning bounding rectangle.
     */
    private void setUpBitmap() {
        mBitmapX = (int) Math.floor(
                Math.random() * (mViewWidth - mBitmap.getWidth()));
        mBitmapY = (int) Math.floor(
                Math.random() * (mViewHeight - mBitmap.getHeight()));
        mWinnerRect = new RectF(mBitmapX, mBitmapY,
                mBitmapX + mBitmap.getWidth(),
                mBitmapY + mBitmap.getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        // Invalidate() is inside the case statements because there are
        // many other motion events, and we don't want to invalidate
        // the view for those.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setUpBitmap();
                // Set coordinates of flashlight cone.
                updateFrame((int) x, (int) y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                // Updated coordinates for flashlight cone.
                updateFrame((int) x, (int) y);
                invalidate();
                break;
            default:
                // Do nothing.
        }
        return true;
    }

    /**
     * Updates the game data.
     * Sets new coordinates for the flashlight cone.
     *
     * @param newX New x position of touch event.
     * @param newY New y position of touch event.
     */
    private void updateFrame(int newX, int newY) {
        mFlashLightCone.update(newX, newY);
    }

    /**
     * Called by MainActivity.onPause() to stop the thread.
     */
    public void pause() {
        mRunning = false;
        try {
            // Stop the thread == rejoin the main thread.
            mGameThread.join();
        } catch (InterruptedException e) {
        }
    }

    /**
     * Called by MainActivity.onResume() to start a thread.
     */
    public void resume() {
        mRunning = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }
}
