package com.framgia.photoalbum.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.framgia.photoalbum.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    View mDecorView;
    @Bind(R.id.photoEditor)
    Button mPhotoEditorBtn;
    @Bind(R.id.collage)
    Button collage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        bindViewControl();
    }

    private void initView() {
        ButterKnife.bind(this);
        mDecorView = getWindow().getDecorView();
    }

    private void bindViewControl() {
        //listen to even which status bar appear, hide it immediately
//        mDecorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int i) {
//                if ((i & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
//                    mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //hide status bar
//        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @OnClick(R.id.photoEditor)
    public void onClick(View v) {
        Intent intent = new Intent(getBaseContext(), ChooseImageActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.collage)
    public void onClick() {
        Intent intent = new Intent(getBaseContext(), CollageActivity.class);
        startActivity(intent);
    }
}
