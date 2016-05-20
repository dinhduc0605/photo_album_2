package com.framgia.photoalbum.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.util.PermissionUtils;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick(R.id.photoEditor)
    public void onClick(View v) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestWriteStoragePermission(this, R.id.rootView);
        } else {
            Intent intent = new Intent(getBaseContext(), ChooseImageActivity.class);
            startActivity(intent);
        }
    }

    @OnClick(R.id.collage)
    public void onClick() {
        Intent intent = new Intent(getBaseContext(), CollageActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(getBaseContext(), ChooseImageActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.write_permission_not_granted), Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
