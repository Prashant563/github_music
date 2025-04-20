package com.example.mymusicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    ListView listView;
    ArrayList<AudioModel> songList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView= findViewById(R.id.listView);
        textView =findViewById(R.id.textView);

        if (!checkPermission()){
            requestPermission();
        }

        String []projection={
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
        };

        String selection=MediaStore.Audio.Media.IS_MUSIC +"!=0";

        Cursor cursor=getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,selection,null,null);

        while (cursor.moveToNext()){
            AudioModel songData=new AudioModel(cursor.getString(1),cursor.getString(0),cursor.getString(2));

            if (new File(songData.getPath()).exists()){
                songList.add(songData);
            }
        }
        if (songList.isEmpty()){
            Toast.makeText(this, "song not find", Toast.LENGTH_SHORT).show();
        }
        else {
            //list view;

            myCustomAdapter ad =new myCustomAdapter(this,R.layout.my_layout,songList);
            listView.setAdapter(ad);

            // clickListener

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Intent intent=new Intent(MainActivity.this,ActivityMusicPlayer.class);

                    intent.putExtra("position",position);
                    intent.putExtra("songsList",songList);
                    startActivity(intent);

                }
            });

        }

    }

    boolean checkPermission(){
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.TIRAMISU){
            return ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_MEDIA_AUDIO)== PackageManager.PERMISSION_GRANTED;
        }
        else {
            return ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }
    void requestPermission(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_MEDIA_AUDIO},123);
        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myCustomAdapter ad=new myCustomAdapter(this,R.layout.my_layout,songList);
        ad.setCurrentIndex(ActivityMusicPlayer.currentSongIndex);
        listView.setAdapter(ad);
    }
}