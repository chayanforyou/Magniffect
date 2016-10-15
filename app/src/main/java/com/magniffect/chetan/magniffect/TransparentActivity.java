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
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;

import com.magniffect.chetan.libscreenshotter.ScreenshotCallback;
import com.magniffect.chetan.libscreenshotter.Screenshotter;

public class TransparentActivity extends AppCompatActivity {
    private static final int REQUEST_MEDIA_PROJECTION = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 2;
    private static final String TAG = "ScreenshotterExample";
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private ImageView demoImageView;
    private Bitmap screenshotBitmap;
    int Phonewidth ;
    int Phoneheight ;



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


        demoImageView = (ImageView) findViewById(R.id.zoomImageView);
        takeScreenshot();





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
                            demoImageView.setImageBitmap(bitmap);
                            Toast.makeText(TransparentActivity.this, "Screenshot Captured!", Toast.LENGTH_SHORT).show();
                            screenshotBitmap =bitmap;
                        }
                    });
        } else {
            Toast.makeText(this, "You denied the permission.", Toast.LENGTH_SHORT).show();
        }
    }

}
