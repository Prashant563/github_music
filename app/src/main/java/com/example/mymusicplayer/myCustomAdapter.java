package com.example.mymusicplayer;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class myCustomAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<AudioModel> songs;
    private int currentIndex=-1;

    public void setCurrentIndex(int index){
        this.currentIndex=index;
    }

    public myCustomAdapter(@NonNull Context context, int resource, @NonNull ArrayList<AudioModel> songs) {
        super(context, resource, songs);
        this.context=context;
        this.songs=songs;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.my_layout,parent,false);
        }

        AudioModel currentSong =songs.get(position);
        TextView textView=convertView.findViewById(R.id.textView);
        textView.setText(currentSong.getTitle());

        if (position==currentIndex){

            textView.setSelected(true);
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView.setSingleLine(true);
            textView.setMarqueeRepeatLimit(-1);

        }
        else {

            textView.setSelected(false);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setSingleLine(true);

        }

        return convertView;
    }
}
