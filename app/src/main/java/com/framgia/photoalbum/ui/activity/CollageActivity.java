package com.framgia.photoalbum.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.FeatureItem;
import com.framgia.photoalbum.ui.adapter.ListFeatureAdapter;
import com.framgia.photoalbum.ui.custom.PartImageView;
import com.framgia.photoalbum.util.DimenUtils;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CollageActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_CHOOSE_IMAGE = 0;
    private static final String TAG = "CollageActivity";
    @Bind(R.id.rootView)
    RelativeLayout rootView;

    @Bind(R.id.listLayoutCollage)
    RecyclerView listLayoutCollage;

    ArrayList<FeatureItem> layoutItems = new ArrayList<>();
    ListFeatureAdapter adapter;
    int viewWidth, viewHeight;
    PartImageView[] partImageViews;
    int mPosition;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collage);
        ButterKnife.bind(this);
        initView();
        bindWidgetControl();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE_IMAGE) {
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                partImageViews[mPosition].setImageBitmap(bitmap);
            }
        }
    }

    private void initData() {
        layoutItems.add(new FeatureItem(R.drawable.ic_collage_1, getString(R.string.collage_1)));
        layoutItems.add(new FeatureItem(R.drawable.ic_collage_2, getString(R.string.collage_2)));
        layoutItems.add(new FeatureItem(R.drawable.ic_collage_3, getString(R.string.collage_3)));
        layoutItems.add(new FeatureItem(R.drawable.ic_collage_4, getString(R.string.collage_4)));
        layoutItems.add(new FeatureItem(R.drawable.ic_collage_5, getString(R.string.collage_5)));
        layoutItems.add(new FeatureItem(R.drawable.ic_collage_6, getString(R.string.collage_6)));
    }

    private void initView() {
        initData();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listLayoutCollage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new ListFeatureAdapter(this, layoutItems, new ListFeatureAdapter.OnFeatureClicked() {
            @Override
            public void onClick(View v, int position) {

            }
        });
        listLayoutCollage.setAdapter(adapter);

        rootView.post(new Runnable() {
            @Override
            public void run() {
                viewWidth = rootView.getWidth();
                viewHeight = (int) (rootView.getHeight() - DimenUtils.dpToPx(getBaseContext(), 100));
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, viewWidth + "-" + viewHeight);
                }
                partImageViews = new PartImageView[3];
                for (int i = 0; i < partImageViews.length; i++) {
                    partImageViews[i] = new PartImageView(getBaseContext());
                    partImageViews[i].setId(i + 1);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, viewHeight / 3);
                    if (i > 0) {
                        layoutParams.addRule(RelativeLayout.BELOW, i);
                    }
                    layoutParams.setMargins(0, 8, 0, 8);
                    partImageViews[i].setLayoutParams(layoutParams);
                    partImageViews[i].setBackgroundColor(ContextCompat.getColor(getBaseContext(), android.R.color.white));
                    rootView.addView(partImageViews[i]);

                    final int finalI = i;
                    partImageViews[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPosition = finalI;
                            Log.d(TAG, "onClick: ");
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE);
                        }
                    });
                }
            }
        });
    }

    private void bindWidgetControl() {

    }
}
