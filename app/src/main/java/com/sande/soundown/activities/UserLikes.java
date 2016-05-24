package com.sande.soundown.activities;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sande.soundown.Adapter.TracksAdapter;
import com.sande.soundown.DialogFragments.DetailedFragment;
import com.sande.soundown.GsonFiles.TrackObject;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Interfaces.HasTrackAdapter;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;
import com.sande.soundown.Utils.PrefsWrapper;
import com.sande.soundown.Utils.ProjectConstants;
import com.sande.soundown.Utils.UtilsManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserLikes extends AppCompatActivity implements ApiCons,HasTrackAdapter{

    private TracksAdapter mAdapter;
    private String mUrlLikes;
    private boolean isScrollable;
    private boolean loading;
    private HashMap<Long,TrackObject> downloadingItems=new HashMap<>();
    private BroadcastReceiver notificationClicked;
    private BroadcastReceiver downloadComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_likes);
        Intent inte=getIntent();
        long userId = inte.getLongExtra(ProjectConstants.USER_ID, 0);
        VolleySingleton.getInstance(this).getImageLoader().get(inte.getStringExtra(ProjectConstants.USER_DISPLAY_PIC), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                ((CircleImageView)findViewById(R.id.civ_aul)).setImageBitmap(response.getBitmap());
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        ((ImageButton)findViewById(R.id.close_ib_aul)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView mUserName=((TextView)findViewById(R.id.tv_username_aul));
        mUserName.setText(inte.getStringExtra(ProjectConstants.USER_NAME));
        RecyclerView mRecy=(RecyclerView)findViewById(R.id.rv_aul);
        mAdapter=new TracksAdapter(this);
        final LinearLayoutManager mLLM=new LinearLayoutManager(this);
        mRecy.setLayoutManager(mLLM);
        mRecy.setAdapter(mAdapter);
        mRecy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isScrollable) {
                    if (dy > 0) {
                        int visibleItemCount = mLLM.getChildCount();
                        int totalItemCount = mLLM.getItemCount();
                        int pastVisibleItems = mLLM.findFirstVisibleItemPosition();
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            if (!loading) {
                                loading = true;
                                getLikes(mUrlLikes);
                            }
                        }
                    }
                }
            }
        });
        String url=USERS_PAGE+ userId +FAVORITES+OAUTH_TOKEN_URI+new PrefsWrapper(this).getAccessToken()+LINKED_PARTITION+SET_LIMIT;
        getLikes(url);
    }

    private void getLikes(String url) {
        JsonObjectRequest getObj=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading=false;
                ArrayList<TrackObject> mLikes=null;
                try {
                    if(response.has(NEXT_HREF)) {
                        mUrlLikes = response.getString(NEXT_HREF);
                    }else{
                        mAdapter.setScrollable(false);
                        isScrollable=false;
                    }
                    Gson gson=new GsonBuilder().create();
                    mLikes=new ArrayList<>(Arrays.asList(gson.fromJson(response.getJSONArray(COLLECTION).toString(),TrackObject[].class)));
                    mAdapter.addLikesObjects(mLikes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(getObj);

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

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(notificationClicked);
        unregisterReceiver(downloadComplete);
    }

    private void showDownloads() {
        Intent i = new Intent();
        i.setAction(DownloadManager.ACTION_VIEW_DOWNLOADS);
        startActivity(i);
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
