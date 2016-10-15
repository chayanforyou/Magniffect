package com.magniffect.chetan.magniffect;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final private static int OVERLAY_PERMISSION_REQ_CODE = 3;
    final private static int WRITE_EXTERNAL_STORAGE_REQ_CODE = 4;

    private Button mBtnShowView;
    private boolean mIsFloatingViewShow; //Flag variable used to identify if the Floating View is visible or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionStatusCheck();

        mBtnShowView = (Button) findViewById(R.id.btn_show_floating_view);
        mIsFloatingViewShow = false;

        mBtnShowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsFloatingViewShow) {
                    hideFloatingView();
                    mIsFloatingViewShow = false;
                    mBtnShowView.setText(R.string.show_floating_view);
                } else {
                    showFloatingView();
                    mIsFloatingViewShow = true;
                    mBtnShowView.setText(R.string.hide_floating_view);
                }
            }
        });


    }

    private void permissionStatusCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }

            if (!isReadStorageAllowed()) {
                //And finally ask for the permission
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQ_CODE);
            }

        }
    }

    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
                if (!Settings.canDrawOverlays(this)) {
                    // SYSTEM_ALERT_WINDOW permission not granted.
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }

            if (requestCode == WRITE_EXTERNAL_STORAGE_REQ_CODE) {

                if (!isReadStorageAllowed()) {
                    //Displaying another toast if permission is not granted
                    Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private void showFloatingView() {
        startService(new Intent(getApplicationContext(), FloatingViewService.class));
    }

    private void hideFloatingView() {
        stopService(new Intent(getApplicationContext(), FloatingViewService.class));
    }
}
