package com.framgia.photoalbum.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.Song;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by HungNT on 5/17/16.
 */
public class ChooseMusicDialog extends Dialog {

    @Bind(R.id.listViewMusic)
    ListView listViewMusic;
    MediaPlayer mMediaPlayer;

    private ArrayList<Song> mListMusic = new ArrayList<>();
    private ChooseMusicAdapter mAdapter;
    private int mChosenPosition;
    private OnAudioSelected mOnAudioSelected;

    public ChooseMusicDialog(Context context, OnAudioSelected onAudioSelected) {
        super(context);
        mOnAudioSelected = onAudioSelected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_choose_music);
        ButterKnife.bind(this);
        prepareListMusic();

        mAdapter = new ChooseMusicAdapter();
        listViewMusic.setAdapter(mAdapter);

        // set default music
        mAdapter.setMusic(0);

        listViewMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Dont need to do anything if new position = last position
                if (mChosenPosition == position) {
                    return;
                }

                mAdapter.setMusic(position);
                playPreviewMusic(mListMusic.get(position));
            }
        });
    }

    private void playPreviewMusic(Song song) {
        stopPlaying();

        if (song.getId() == -1) {
            return;
        }

        mMediaPlayer = MediaPlayer.create(getContext(), song.getId());
        mMediaPlayer.start();
    }

    private void stopPlaying() {
        try {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        } catch (IllegalStateException ex) {
            if (BuildConfig.DEBUG) {
                Log.d("MediaPlayer", "MediaPlayer has not yet been initialized");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlaying();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }

    private void prepareListMusic() {
        //TODO this is sample, add music
        mListMusic.add(new Song("None", -1));
        mListMusic.add(new Song("A Little Love", R.raw.a_little_love));
        mListMusic.add(new Song("Fade", R.raw.fade));
        mListMusic.add(new Song("Hoa Giang Ho", R.raw.hoa_giang_ho));
        mListMusic.add(new Song("Trai Tao Nho", R.raw.trai_tao_nho));
        mListMusic.add(new Song("Unfinished", R.raw.unfinished));
    }

    private class ChooseMusicAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mListMusic.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_list_music, parent, false);
            }

            TextView tvNameSong = (TextView) convertView.findViewById(R.id.tvNameSong);
            RadioButton rbChecked = (RadioButton) convertView.findViewById(R.id.rbChecked);

            tvNameSong.setText(mListMusic.get(position).getName());
            rbChecked.setChecked(mListMusic.get(position).isChecked());
            rbChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mOnAudioSelected.onSelected(mListMusic.get(position));
                }
            });

            return convertView;
        }

        public void setMusic(int position) {
            mChosenPosition = position;
            for (Song item : mListMusic) {
                item.setChecked(false);
            }

            mListMusic.get(position).setChecked(true);
            notifyDataSetChanged();
        }
    }

    public interface OnAudioSelected {
        void onSelected(Song song);
    }
}
