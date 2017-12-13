package com.example.sairohit.musicplayerinterface;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by sairohit on 04/12/17.
 */

public class Adapter extends BaseAdapter {

    private ArrayList<SongsDetail> songs;
    private LayoutInflater songInf;

    public Adapter(ArrayList<SongsDetail> songs, Context c) {
        this.songs = songs;
        songInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LinearLayout linearLayout = (LinearLayout)songInf.inflate(R.layout.songs,viewGroup,false);

        TextView songView = linearLayout.findViewById(R.id.song_title);
        TextView artistView = linearLayout.findViewById(R.id.song_artist);
        TextView time = linearLayout.findViewById(R.id.song_time);

        SongsDetail currSong = songs.get(i);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        artistView.setText(currSong.getArtist());
        time.setText(currSong.getDuration());
        //set position as tag
        linearLayout.setTag(i);
        return linearLayout;


    }
}
