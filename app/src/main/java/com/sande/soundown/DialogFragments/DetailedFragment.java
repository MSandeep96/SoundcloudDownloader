package com.sande.soundown.DialogFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.sande.soundown.GsonFiles.TrackObject;
import com.sande.soundown.Interfaces.HasTrackAdapter;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;

/**
 * Created by Sandeep on 22-05-2016.
 */
public class DetailedFragment extends DialogFragment {
    private static final String KEY_TRACK_OBJ = "TRACK_OBJ";
    TrackObject mObj;
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
        ((NetworkImageView)mView.findViewById(R.id.dfd_iv_album)).setImageUrl(mObj.getArtwork_url(), VolleySingleton.getInstance(getContext()).getImageLoader());
        ((TextView)mView.findViewById(R.id.dfd_tv_track)).setText(mObj.getTitle());
        (mView.findViewById(R.id.dfd_btn_download)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HasTrackAdapter)getContext()).startDownload(mObj);
            }
        });
        (mView.findViewById(R.id.dfd_btn_play)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do something
            }
        });
        return mView;
    }
}
