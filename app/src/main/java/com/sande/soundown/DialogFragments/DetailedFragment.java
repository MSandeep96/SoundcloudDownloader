package com.sande.soundown.DialogFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.sande.soundown.GsonFiles.TrackObject;
import com.sande.soundown.Interfaces.HasTrackAdapter;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;
import com.sande.soundown.Utils.UtilsManager;

/**
 * Created by Sandeep on 22-05-2016.
 */
public class DetailedFragment extends DialogFragment {
    private static final String KEY_TRACK_OBJ = "TRACK_OBJ";
    TrackObject mObj;
    private ImageButton mDownBtn;
    private ProgressBar mProgressBar;
    private ImageButton mPlayBtn;
    private ImageButton mShareBtn;

    public DetailedFragment(){

    }

    public static DetailedFragment getInstance(TrackObject mObj) {
        DetailedFragment mFrag=new DetailedFragment();
        Bundle mBund=new Bundle();
        mBund.putParcelable(KEY_TRACK_OBJ,mObj);
        mFrag.setArguments(mBund);
        return mFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mObj=getArguments().getParcelable(KEY_TRACK_OBJ);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView=inflater.inflate(R.layout.dialog_frag_detailed,container,false);
        ((NetworkImageView)mView.findViewById(R.id.dfd_niv_album)).setImageUrl(mObj.getArtwork_url(), VolleySingleton.getInstance(getContext()).getImageLoader());
        ((TextView)mView.findViewById(R.id.dfd_tv_artist)).setText(mObj.getUser().getUsername());
        ((TextView)mView.findViewById(R.id.dfd_tv_track)).setText(mObj.getTitle());
        mDownBtn=(ImageButton)mView.findViewById(R.id.dfd_ib_down);
        mProgressBar=(ProgressBar)mView.findViewById(R.id.dfd_pb);
        mDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mDownBtn.setVisibility(View.GONE);
                ((HasTrackAdapter)getContext()).startDownload(mObj);
            }
        });
        int dur = mObj.getDuration();
        String dura = String.format("%02d", dur / (60 * 1000)) + ":" + String.format("%02d", (dur / 1000) % (60));
        ((TextView)mView.findViewById(R.id.dfd_tv_time)).setText(dura);
        mPlayBtn=(ImageButton)mView.findViewById(R.id.dfd_ib_play);
        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
            }
        });
        mShareBtn=(ImageButton)mView.findViewById(R.id.dfd_ib_share);
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
            }
        });
        changeVisibilities();
        return mView;
    }

    private void changeVisibilities() {
        if(((HasTrackAdapter)getContext()).isDownloading(mObj.getId())){
            mPlayBtn.setVisibility(View.GONE);
            mDownBtn.setVisibility(View.GONE);
            mShareBtn.setVisibility(View.GONE);
        }else{
            boolean fileExists = UtilsManager.doesSongExist(mObj.getTitle());
            if(fileExists){
                mDownBtn.setVisibility(View.GONE);
            }else{
                mPlayBtn.setVisibility(View.GONE);
                mShareBtn.setVisibility(View.GONE);
            }
        }
    }


    public void downloadComplete(){
        mProgressBar.setVisibility(View.GONE);
        mPlayBtn.setVisibility(View.VISIBLE);
        mShareBtn.setVisibility(View.VISIBLE);
    }


}
