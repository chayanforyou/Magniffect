package com.magniffect.chetan.magniffect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.projection.MediaProjectionManager;
import android.os.IBinder;
import android.util.Base64;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by chetan on 15-10-2016.
 */

public class FloatingViewService extends Service {

    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private WindowManager mWindowManager;
    private ImageView mImgFloatingView;
    private boolean mIsFloatingViewAttached = false;
    private int Phonewidth;
    private int Phoneheight;
    private Bitmap screenshotBitmap;

    @Override
    public IBinder onBind(Intent intent) {
        //Not use this method
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mIsFloatingViewAttached) {
            mWindowManager.addView(mImgFloatingView, mImgFloatingView.getLayoutParams());
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        mImgFloatingView = new ImageView(this);
        mImgFloatingView.setBackgroundResource(R.drawable.round_button);
        mImgFloatingView.setImageResource(R.drawable.floating_image);
        mImgFloatingView.setElevation(5.0f);


        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;

        mWindowManager.addView(mImgFloatingView, params);
        mImgFloatingView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if ((Math.abs(initialTouchX - event.getRawX()) < 5) && (Math.abs(initialTouchY - event.getRawY()) < 5)) {
                            performClick();
                        }
                        //Toast.makeText(FloatingViewService.this, "you moved the head", Toast.LENGTH_SHORT).show();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mImgFloatingView, params);
                        return true;
                }
                return false;
            }
        });

        mIsFloatingViewAttached = true;
    }

    private void performClick() {
//        Intent dialogIntent = new Intent(this, TransparentActivity.class);
//        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(dialogIntent);

        WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = window.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Phonewidth = size.x;
        Phoneheight = size.y;

        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;
        params.width = 0;
        params.height = 0;

        LinearLayout view = new LinearLayout(this);
        view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        WebView wv = new WebView(this);
        wv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        view.addView(wv);

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);


        Intent i = mediaProjectionManager.createScreenCaptureIntent();
        i.addFlags(REQUEST_MEDIA_PROJECTION);
        i.addFlags(FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);


        // Desired Bitmap and the html code, where you want to place it
        String html = "<html><body><img src='{IMAGE_PLACEHOLDER}' /></body></html>";

        // Convert bitmap to Base64 encoded image for web
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        screenshotBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imgageBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        String image = "data:image/png;base64," + imgageBase64;

        // Use image for the img src parameter in your html and load to webview
        html = html.replace("{IMAGE_PLACEHOLDER}", image);
        wv.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", "");
        wv.getSettings().setSupportZoom(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.setPadding(0, 0, 0, 0);

        windowManager.addView(view, params);

    }

    public void removeView() {
        if (mImgFloatingView != null) {
            mWindowManager.removeView(mImgFloatingView);
            mIsFloatingViewAttached = false;
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT);
        super.onDestroy();
        removeView();
    }


}
