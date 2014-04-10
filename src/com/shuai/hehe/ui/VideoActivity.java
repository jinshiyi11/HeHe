package com.shuai.hehe.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.shuai.hehe.R;
import com.shuai.hehe.data.Constants;

public class VideoActivity extends Activity {
    
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);
        
        mVideoView=(VideoView) findViewById(R.id.videoView);
        
        Intent intent=getIntent();
        String videoUrl=intent.getStringExtra(Constants.VIDEO_URL);
        Uri uri=Uri.parse(videoUrl);
        
        MediaController mediaController=new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        mVideoView.setVideoURI(uri);
        
        mVideoView.setOnErrorListener(new OnErrorListener() {
            
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(VideoActivity.this, "error", Toast.LENGTH_LONG);
                return false;
            }
        });
        
        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mVideoView.stopPlayback();
        super.onDestroy();
    }
    
    

}
