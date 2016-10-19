package com.magniffect.chetan.magniffect;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final private static int OVERLAY_PERMISSION_REQ_CODE = 3;
    final private static int WRITE_EXTERNAL_STORAGE_REQ_CODE = 4;

    private ImageButton serviceEnablerImageButton;
    private FrameLayout frameLayout;
    private TextView appnametextView;
    private boolean mIsFloatingViewShow; //Flag variable used to identify if the Floating View is visible or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        permissionStatusCheck();

        applyCustomFonts();

        mIsFloatingViewShow = false;

        serviceEnablerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsFloatingViewShow) {
                    hideFloatingView();
                    mIsFloatingViewShow = false;
                } else {
                    showFloatingView();
                    mIsFloatingViewShow = true;
                }
            }
        });

    }

    private void applyCustomFonts() {
        Typeface customFont = Typeface.createFromAsset(getAssets(), "fonts/Lobster-Regular.ttf");
        appnametextView.setTypeface(customFont);
    }

    private void initViews() {
        serviceEnablerImageButton = (ImageButton) findViewById(R.id.serviceEnablerImageButton);
        appnametextView = (TextView) findViewById(R.id.appnametextView);
        frameLayout = (FrameLayout) findViewById(R.id.main_frameLayout);
    }


    private void permissionStatusCheck() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("System Overlay Permission");
        alertDialog.setMessage("Please allow system overlay permission to enable headup display");
        alertDialog.setIcon(R.drawable.setting_permission);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SYSTEM_ALERT_WINDOW);
            if (result == PackageManager.PERMISSION_GRANTED) {
                Snackbar snackbar = Snackbar.make(frameLayout, "Permission applied", Snackbar.LENGTH_LONG).setAction("", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        permissionStatusCheck();
                    }
                });
                snackbar.show();
            } else {

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar snackbar = Snackbar.make(frameLayout, "Permission denied", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                permissionStatusCheck();
                            }
                        });

                        snackbar.show();
                        dialog.cancel();
                    }
                });

                alertDialog.show();
            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar snackbar = Snackbar.make(frameLayout, "Permission denied", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                permissionStatusCheck();
                            }
                        });

                        snackbar.show();
                        dialog.cancel();
                    }
                });

                alertDialog.show();
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

    @Override
    protected void onPause() {
        super.onPause();
    }
}
