package com.magniffect.chetan.magniffect;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TransparentActivity extends AppCompatActivity {

    private ImageView zoomImageView;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transparent);

        zoomImageView= (ImageView) findViewById(R.id.zoomImageView);
        relativeLayout= (RelativeLayout) findViewById(R.id.activity_transparent);



    }


    @Override
    protected void onPause() {
        super.onPause();
        TransparentActivity.this.finish();
    }




}
