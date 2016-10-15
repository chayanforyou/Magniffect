package com.magniffect.chetan.magniffect;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final private static int OVERLAY_PERMISSION_REQ_CODE = 1234;
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
        }
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
        }
    }


    private void showFloatingView() {
        startService(new Intent(getApplicationContext(), FloatingViewService.class));
    }

    private void hideFloatingView() {
        stopService(new Intent(getApplicationContext(), FloatingViewService.class));
    }
}
