package com.video.downloader.thread.videodownloader.threadsvideodownloader;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;

public class VideoPlayer extends AppCompatActivity {
    private VideoView videoView;
    private ImageView btnStart, btnPause, back_btn;

    private Intent intent;
    String path = "";
    public static int deleteCheck = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        videoView = findViewById(R.id.video_view);
        back_btn = findViewById(R.id.back_btn);

        intent = getIntent();

        android.widget.MediaController mediaController = new android.widget.MediaController(this);
        videoView.setMediaController(mediaController);
        path = intent.getStringExtra("Link");


        String videoUrl =path;
        Uri videoUri = Uri.parse(videoUrl);
        videoView.setVideoURI(videoUri);
        videoView.start();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.share_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareVideo();
            }
        });
        findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (path != null)
                        new File(path).delete();
                    deleteCheck = 1;
                } catch (Exception e) {

                }

                finish();
            }
        });
    }
    private void shareVideo() {
        Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(path));

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, photoURI);
        intent.setType("video/*"); // this line is use to filter which app support specified media formate to share
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent, "share"), 1);
    }
}