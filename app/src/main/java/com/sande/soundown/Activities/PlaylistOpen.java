package com.sande.soundown.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sande.soundown.Fragments.Tracks;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.R;
import com.sande.soundown.Utils.ProjectConstants;
import com.sande.soundown.Utils.UtilsManager;

public class PlaylistOpen extends AppCompatActivity implements ApiCons{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_open);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent mInte=getIntent();
        String url=mInte.getStringExtra(ProjectConstants.PLAYLIST_URL);
        url+=PLAYLISTS_TRACKS+OAUTH_TOKEN_URI+ UtilsManager.getAccessToken(this)+LINKED_PARTITION+SET_LIMIT;
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_cpo, Tracks.getInstance(url)).commit();
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
}
