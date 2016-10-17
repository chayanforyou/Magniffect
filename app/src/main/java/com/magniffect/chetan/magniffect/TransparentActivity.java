package com.magniffect.chetan.magniffect;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.webkit.WebView;
import android.widget.Toast;

import com.magniffect.chetan.libscreenshotter.ScreenshotCallback;
import com.magniffect.chetan.libscreenshotter.Screenshotter;

import java.io.ByteArrayOutputStream;

public class TransparentActivity extends AppCompatActivity {
    private static final int REQUEST_MEDIA_PROJECTION = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static final String TAG = "ScreenshotterExample";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    int Phonewidth;
    int Phoneheight;
    boolean functioncalledonece;
    private Bitmap screenshotBitmap;
    private WebView webView;

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Phonewidth = size.x;
        Phoneheight = size.y;

        webView = (WebView) findViewById(R.id.webView);


        takeScreenshot();
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Do something after 5s = 5000ms
//
//
//            }
//        }, 200);

    }

    public void takeScreenshot() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Screenshotter.getInstance()
                    .setSize(Phonewidth, Phoneheight)
                    .takeScreenshot(this, resultCode, data, new ScreenshotCallback() {
                        @Override
                        public void onScreenshot(Bitmap bitmap) {
                            Log.d(TAG, "onScreenshot called");
                            verifyStoragePermissions(TransparentActivity.this);
                            screenshotBitmap = bitmap.copy(bitmap.getConfig(), true);
                            addWebViewtoScreenShot();
                        }
                    });
        } else {
            Toast.makeText(this, "You denied the permission.", Toast.LENGTH_SHORT).show();
        }
    }

    private void addWebViewtoScreenShot() {

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
        webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", "");
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setPadding(0, 0, 0, 0);

    }

}
