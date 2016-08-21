package com.sande.soundown.Fragments;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sande.soundown.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment extends Fragment {


    public WelcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView= inflater.inflate(R.layout.fragment_welcome, container, false);
        Typeface mTypeface=Typeface.createFromAsset(getContext().getAssets(),"fonts/Slim Joe.otf");
        ((TextView)mView.findViewById(R.id.welcome_tv_title)).setTypeface(mTypeface);
        Glide.with(this).load(R.drawable.welcome_icon).into((ImageView)mView.findViewById(R.id.welcome_iv_squ));
        return mView;
    }

}
