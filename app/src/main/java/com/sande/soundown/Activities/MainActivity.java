package com.sande.soundown.activities;

import android.app.DownloadManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gigamole.library.NavigationTabBar;
import com.sande.soundown.Fragments.Feeds;
import com.sande.soundown.Fragments.Tracks;
import com.sande.soundown.Fragments.Playlists;
import com.sande.soundown.Fragments.Profile;
import com.sande.soundown.GsonFiles.TrackObject;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Interfaces.CallBackMain;
import com.sande.soundown.R;
import com.sande.soundown.Utils.PrefsWrapper;
import com.sande.soundown.Utils.UtilsManager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CallBackMain {

    private ViewPager mViewpager;
    private DownloadManager mDownloadManager;
    private PrefsWrapper prefsWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDownloadManager=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setViewPager(int item) {
        mViewpager.setCurrentItem(item,true);
    }

    @Override
    public void enqueueDownload(TrackObject song) {
        String url=song.getStream_url()+"?oauth_token="+prefsWrapper.getAccessToken();
        String fileName=song.getTitle().replaceAll("\\W+","");
        if(!UtilsManager.isExternalStorageWritable()){
            Toast.makeText(MainActivity.this, "SD Card not available", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri downUri=Uri.parse(url);
        DownloadManager.Request req=new DownloadManager.Request(downUri);
        req.setTitle(fileName).setDescription("DownloadingYo");
        req.setVisibleInDownloadsUi(false);
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC,UtilsManager.getSongStorDir(fileName));
        mDownloadManager.enqueue(req);
    }

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
