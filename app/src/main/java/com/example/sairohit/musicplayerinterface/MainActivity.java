package com.example.sairohit.musicplayerinterface;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private ArrayList<SongsDetail> songsDetailArrayList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.mainsetting,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView)findViewById(R.id.recycleview);

        songsDetailArrayList = new ArrayList<SongsDetail>();
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},123);
            }
        }
//        Log.i("Aray",songsDetailArrayList.get(4).toString());

        getSongfromDevice();

        Collections.sort(songsDetailArrayList, new Comparator<SongsDetail>(){
            public int compare(SongsDetail a, SongsDetail b){
                return b.getTitle().compareTo(a.getTitle());
            }
        });

        Log.i("Aray",songsDetailArrayList.get(4).toString());



        Adapter adapterhere = new Adapter(songsDetailArrayList,this);

        listView.setAdapter(adapterhere);


    }

    public void getSongfromDevice(){

        ContentResolver musicfile = getContentResolver();

        Uri musicuri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musiccursor =  musicfile.query(musicuri,null,null,null,null);
        if(musiccursor!=null && musiccursor.moveToFirst()){
            //get columns
            int titleColumn = musiccursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musiccursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musiccursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);

            int songduration = musiccursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DURATION);
            //add songs to list
            do {
                long thisId = musiccursor.getLong(idColumn);
                String thisTitle = musiccursor.getString(titleColumn);
                String thisArtist = musiccursor.getString(artistColumn);
                long thisTime = musiccursor.getLong(songduration);

                String converttime = String.format("%d:%02d" ,
                        TimeUnit.MILLISECONDS.toMinutes(thisTime),
                        (TimeUnit.MILLISECONDS.toSeconds(thisTime)) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(thisTime))
                );

                songsDetailArrayList.add(new SongsDetail(thisId, thisTitle, thisArtist,converttime));
            }
            while (musiccursor.moveToNext());
        }

    }
}
