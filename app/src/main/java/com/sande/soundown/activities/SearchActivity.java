package com.sande.soundown.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.sande.soundown.Fragments.Tracks;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.R;
import com.sande.soundown.Utils.PrefsWrapper;

public class SearchActivity extends AppCompatActivity implements ApiCons{

    Fragment mFrag;
    PrefsWrapper mPrefWrap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mPrefWrap=PrefsWrapper.with(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final EditText mEditText=(EditText)findViewById(R.id.as_et_search);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE||actionId==EditorInfo.IME_ACTION_SEARCH){
                    searchQuery(mEditText.getText().toString());
                    return true;
                }
                return false;
            }
        });
        RecyclerView mRecy=(RecyclerView)findViewById(R.id.cs_rv_search);

    }

    private void searchQuery(String s) {
        String mUrl=TRACKS_ID+OAUTH_TOKEN_URI+mPrefWrap.getAccessToken()+SEARCH_START+LINKED_PARTITION+SET_LIMIT;
        if(mFrag==null){
            mFrag= Tracks.getInstance(mUrl);
            getSupportFragmentManager().beginTransaction().add(R.id.fl_cs,mFrag,"SEARCH_FRAG").commit();
        }else{
            ((Tracks)mFrag).changedQuery(s);
        }
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
