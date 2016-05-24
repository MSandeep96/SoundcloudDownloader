package com.sande.soundown.activities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.sande.soundown.DialogFragments.DetailedFragment;
import com.sande.soundown.Fragments.Tracks;
import com.sande.soundown.GsonFiles.TrackObject;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Interfaces.HasTrackAdapter;
import com.sande.soundown.R;
import com.sande.soundown.Utils.PrefsWrapper;
import com.sande.soundown.Utils.ProjectConstants;
import com.sande.soundown.Utils.UtilsManager;

import java.util.HashMap;

public class PlaylistOpen extends AppCompatActivity implements ApiCons,HasTrackAdapter{


    private HashMap<Long,TrackObject> downloadingItems=new HashMap<>();
    private BroadcastReceiver notificationClicked;
    private BroadcastReceiver downloadComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_open);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent mInte=getIntent();
        String url=mInte.getStringExtra(ProjectConstants.PLAYLIST_URL);
        url+=PLAYLISTS_TRACKS+OAUTH_TOKEN_URI+ new PrefsWrapper(this).getAccessToken()+LINKED_PARTITION+SET_LIMIT;
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_cpo, Tracks.getInstance(url)).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationClicked=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String extraId=DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
                long[] references=intent.getLongArrayExtra(extraId);
                for(long refer:references){
                    if(downloadingItems.containsKey(refer)){
                        showDownloads();
                    }
                }
            }
        };
        downloadComplete=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,-1);
                if(downloadingItems.containsKey(reference)){
                    changeDialog(reference);
                }
            }
        };
        IntentFilter completeFilter=new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        IntentFilter filter=new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        registerReceiver(notificationClicked,filter);
        registerReceiver(downloadComplete,completeFilter);
    }

    private void changeDialog(long reference) {
        String tag=String.valueOf(downloadingItems.get(reference).getId());
        Fragment mDial=getSupportFragmentManager().findFragmentByTag(tag);
        if(mDial!=null){
            ((DetailedFragment)mDial).downloadComplete();
        }
    }


    private void showDownloads() {
        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(notificationClicked);
        unregisterReceiver(downloadComplete);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void launchDialog(TrackObject song) {
        DetailedFragment mFrag=DetailedFragment.getInstance(song);
        mFrag.show(getSupportFragmentManager(),String.valueOf(song.getId()));
    }

    @Override
    public void startDownload(TrackObject song) {
        String url=song.getStream_url()+"?oauth_token="+new PrefsWrapper(this).getAccessToken();
        String fileName=song.getTitle().replaceAll("\\W+","");
        if(!UtilsManager.isExternalStorageWritable()){
            Toast.makeText(this, "SD Card not available", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri downUri=Uri.parse(url);
        DownloadManager.Request req=new DownloadManager.Request(downUri);
        req.setTitle(fileName);
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC,UtilsManager.getSongStorDir(fileName));
        DownloadManager mDownloadManager=(DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadRef=mDownloadManager.enqueue(req);
        downloadingItems.put(downloadRef,song);
    }

    @Override
    public boolean isDownloading(TrackObject song) {
        return downloadingItems.containsValue(song);
    }

}
