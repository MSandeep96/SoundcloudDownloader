package com.sande.soundown.activities;

import android.app.DownloadManager;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.UserManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gigamole.library.NavigationTabBar;
import com.sande.soundown.DialogFragments.DetailedFragment;
import com.sande.soundown.Fragments.Feeds;
import com.sande.soundown.Fragments.Tracks;
import com.sande.soundown.Fragments.Playlists;
import com.sande.soundown.Fragments.Profile;
import com.sande.soundown.GsonFiles.TrackObject;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Interfaces.CallBackMain;
import com.sande.soundown.Interfaces.HasTrackAdapter;
import com.sande.soundown.R;
import com.sande.soundown.Utils.PrefsWrapper;
import com.sande.soundown.Utils.ProjectConstants;
import com.sande.soundown.Utils.UtilsManager;
import com.sande.soundown.services.SetTags;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements CallBackMain,HasTrackAdapter {

    private ViewPager mViewpager;
    private PrefsWrapper prefsWrapper;
    private HashMap<Long,TrackObject> downloadingItems=new HashMap<>();
    private BroadcastReceiver notificationClicked;
    private BroadcastReceiver downloadComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prefsWrapper=new PrefsWrapper(this);
        getSupportActionBar().setTitle("Soundown");
        if(!prefsWrapper.isLoggedIn()){
            Intent mIntent=new Intent(this,LoginActivity.class);
            startActivity(mIntent);
            finish();
        }
        NavigationTabBar ntb=(NavigationTabBar)findViewById(R.id.ntb_main);
        ArrayList<NavigationTabBar.Model> models=new ArrayList<>();
        models.add(new NavigationTabBar.Model(ContextCompat.getDrawable(this,R.drawable.favs),ContextCompat.getColor(this,R.color.colorWhite),"Likes"));
        models.add(new NavigationTabBar.Model(ContextCompat.getDrawable(this,R.drawable.playlists),ContextCompat.getColor(this,R.color.colorWhite),"Playlists"));
        models.add(new NavigationTabBar.Model(ContextCompat.getDrawable(this,R.drawable.feed),ContextCompat.getColor(this,R.color.colorWhite),"Feed"));
        models.add(new NavigationTabBar.Model(ContextCompat.getDrawable(this,R.drawable.profile),ContextCompat.getColor(this,R.color.colorWhite),"Profile"));
        ntb.setModels(models);
        mViewpager=(ViewPager)findViewById(R.id.pager);
        MyFragAdapter mAdapter=new MyFragAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mAdapter);
        mViewpager.setOffscreenPageLimit(5);
        ntb.setViewPager(mViewpager);
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
                    // TODO: 24-05-2016 Add notification
                    Intent mInte=new Intent(context, SetTags.class);
                    mInte.putExtra(ProjectConstants.TRACKOBJECT,downloadingItems.get(reference));
                    startService(mInte);
                }
                downloadingItems.remove(reference);
                // TODO: 24-05-2016 remove reference from hashmap
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(notificationClicked!=null || downloadComplete!=null) {
            unregisterReceiver(notificationClicked);
            unregisterReceiver(downloadComplete);
        }
    }

    private void showDownloads() {
        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id==R.id.search){
            Intent mInte=new Intent(this,SearchActivity.class);
            startActivity(mInte);
        }

        return super.onOptionsItemSelected(item);
    }





    
    
    //HasTrackAdapter Methods
    
    
    
    @Override
    public void launchDialog(TrackObject song) {
        DetailedFragment mFrag=DetailedFragment.getInstance(song);
        mFrag.show(getSupportFragmentManager(),String.valueOf(song.getId()));
    }
    
    
    
    
    
    
    public void startDownload(TrackObject song) {
        String url=song.getStream_url()+"?oauth_token="+PrefsWrapper.with(this).getAccessToken();
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


    @Override
    public void setViewPager(int item) {
        mViewpager.setCurrentItem(item,true);
    }


   
   
   
   
    //FragAdapter
    class MyFragAdapter extends FragmentStatePagerAdapter implements ApiCons{

        public MyFragAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:return Tracks.getInstance(USERS_PAGE + prefsWrapper.getUserID() + FAVORITES +
                        OAUTH_TOKEN_URI + prefsWrapper.getAccessToken() +
                        LINKED_PARTITION + SET_LIMIT);
                case 1:return new Playlists();
                case 2:return new Feeds();
                case 3:return new Profile();
                default:return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

}
