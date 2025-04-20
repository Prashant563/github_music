package com.example.mymusicplayer;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;

public class ActivityMusicPlayer extends AppCompatActivity {

    TextView songTitle;
    SeekBar seekBar;
    ImageView pause,prev,next;
    ArrayList<AudioModel> songsList;
    int currentIndex;

    public static MediaPlayer mediaPlayer;
    public static int currentSongIndex=-1;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        songTitle =findViewById(R.id.songTitle);
        songTitle.setSelected(true);
        seekBar=findViewById(R.id.seekBar);
        pause=findViewById(R.id.pause);
        next= findViewById(R.id.next);
        prev= findViewById(R.id.prev);



        songsList=(ArrayList<AudioModel>) getIntent().getSerializableExtra("songsList");
        currentIndex=getIntent().getIntExtra("position",0);
        playSong(currentIndex);

        //  seekbar

        ActivityMusicPlayer.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer!=null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                }
                new Handler().postDelayed(this,50);
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer!=null && b){
                    mediaPlayer.seekTo(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // next button logic

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentIndex++;
                if (currentIndex>=songsList.size()){
                    currentIndex=0;
                }
                playSong(currentIndex);
            }
        });

        //  previous button logic

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentIndex --;
                if (currentIndex<0){
                    currentIndex=songsList.size()-1;
                }
                playSong(currentIndex);
            }
        });

        // play and pause logic

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mediaPlayer!=null){
                    if (mediaPlayer.isPlaying()){
                        mediaPlayer.stop();
                        pause.setImageResource(R.drawable.play);
                    }
                    else {
                        mediaPlayer.start();
                        pause.setImageResource(R.drawable.pause);
                    }
                }

            }
        });


    }
    public void playSong(int index){
        AudioModel song =songsList.get(currentIndex);
        songTitle.setText(song.getTitle());

        currentSongIndex=index;

        if (mediaPlayer!=null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer=null;
        }

        mediaPlayer =new MediaPlayer();

        try {
            mediaPlayer.setDataSource(song.getPath());
            mediaPlayer.prepare();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //   auto next play song
                    currentIndex++;
                    if (currentIndex>=songsList.size()){
                        currentIndex=0;  // for looping
                    }
                    playSong(currentIndex);
                }
            });


            //   add album picture
            ImageView albm=findViewById(R.id.albm);
            MediaMetadataRetriever mmr=new MediaMetadataRetriever();
            mmr.setDataSource(song.getPath());
            byte[] art =mmr.getEmbeddedPicture();

            if (art!= null){
                Bitmap bitmap= BitmapFactory.decodeByteArray(art,0,art.length);
                albm.setImageBitmap(bitmap);
            }
            else {
                albm.setImageResource(R.drawable.image);
            }

            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
            pause.setImageResource(R.drawable.pause);
        } catch (IOException e) {
            Toast.makeText(this, "Can't play the song....", Toast.LENGTH_SHORT).show();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}