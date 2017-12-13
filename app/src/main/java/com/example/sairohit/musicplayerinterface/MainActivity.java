package com.example.sairohit.musicplayerinterface;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 0;
    boolean permissiongranted = false;
    ListView listView;

     static int viewid;
    public static ArrayList<SongsDetail> songsDetailArrayList;

    static MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.mainsetting,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected

        switch (item.getItemId()) {
            case R.id.action_shuffle:
                //shuffle
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicSrv=null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         listView = findViewById(R.id.recycleview);

        songsDetailArrayList = new ArrayList<SongsDetail>();
        if(Build.VERSION.SDK_INT>=23) {

            PackageManager pm = this.getPackageManager();
            int hasPerm = pm.checkPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    this.getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {

                mainprocess();
            }
            else {
                requestPermission();
            }
        }
    }
    // on create Ends here

    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            musicSrv.setSongs(MainActivity.songsDetailArrayList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };



    public void open(View view){

        Intent playerActivity = new Intent(MainActivity.this,playerdesign.class);

        playerActivity.putExtra("id",view.getTag().toString());
        startActivity(playerActivity);


    }


    // to get songs from local Device

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

    // checking the Permission

    public boolean checkPermission(){


        int result = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

//If the app does have this permission, then return true//

        return result == PackageManager.PERMISSION_GRANTED;

    }

    // Rewuesting the Permission

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mainprocess();


                }
                else {

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write Storgae permission is necessary!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            requestPermission();
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                    break;
                }
        }
    }

    public void mainprocess(){

        getSongfromDevice();

        Collections.sort(songsDetailArrayList, new Comparator<SongsDetail>(){
            public int compare(SongsDetail a, SongsDetail b){
                return b.getTitle().compareTo(a.getTitle());
            }
        });

        Log.i("Aray",songsDetailArrayList.get(4).toString());



        Adapter adapterhere = new Adapter(songsDetailArrayList,this);

        listView.setAdapter(adapterhere);

        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }

    }



}
