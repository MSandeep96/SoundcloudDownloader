package com.sande.soundown.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.gigamole.library.NavigationTabBar;
import com.sande.soundown.Fragments.Feeds;
import com.sande.soundown.Fragments.Likes;
import com.sande.soundown.Fragments.Playlists;
import com.sande.soundown.Fragments.Profile;
import com.sande.soundown.R;
import com.crashlytics.android.Crashlytics;
import com.sande.soundown.Utils.UtilsManager;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Soundown");
        if(!UtilsManager.isLoggedIn(this)){
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
        ViewPager mViewpager=(ViewPager)findViewById(R.id.vp_main);
        MyFragAdapter mAdapter=new MyFragAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mAdapter);
        mViewpager.setOffscreenPageLimit(3);
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
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    class MyFragAdapter extends FragmentPagerAdapter{

        public MyFragAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:return new Likes();
                case 1:return new Playlists();
                case 2:return new Feeds();
                default:return new Profile();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

}
