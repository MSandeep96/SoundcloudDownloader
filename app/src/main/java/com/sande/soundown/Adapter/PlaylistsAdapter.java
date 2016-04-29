package com.sande.soundown.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.sande.soundown.GsonFiles.LikesObject;
import com.sande.soundown.GsonFiles.PlaylistObject;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;
import com.sande.soundown.Utils.UtilsManager;

import java.util.ArrayList;

/**
 * Created by Sandeep on 29-Apr-16.
 */
public class PlaylistsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private Context mContext;
    public static final int VIEW_TYPE = 0;
    public static final int LOAD_TYPE = 1;
    private ImageLoader mImageLoader;
    private boolean isScrollable=true;
    private ArrayList<PlaylistObject> items = new ArrayList<>();

    public PlaylistsAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        VolleySingleton mSingleton = VolleySingleton.getInstance(context);
        mImageLoader = mSingleton.getImageLoader();
    }

    public void addPlaysObjects(ArrayList<PlaylistObject> mPlays) {
        if (items.size() == 0) {
            items = mPlays;
        } else {
            items.addAll(mPlays);
        }
        notifyDataSetChanged();
    }

    public void setScrollable(boolean scrollable) {
        isScrollable = scrollable;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == items.size()) {
            return LOAD_TYPE;
        } else {
            return VIEW_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE) {
            View mView = mLayoutInflater.inflate(R.layout.playlists_item, parent, false);
            return new PlaylistsViewHolder(mView);
        } else {
            LinearLayout mLinearLayout = new LinearLayout(mContext);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mLinearLayout.setLayoutParams(params);
            mLinearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            ProgressBar mProgre = new ProgressBar(mContext);
            mLinearLayout.addView(mProgre);
            return new RecyclerView.ViewHolder(mLinearLayout) {
            };
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // TODO: 28-Apr-16 hooooly shit
        if (holder instanceof PlaylistsViewHolder) {
            ((PlaylistsViewHolder) holder).artwork.setImageUrl(items.get(position).getUri(), mImageLoader);
            ((PlaylistsViewHolder) holder).title.setText(items.get(position).getTitle());
            ((PlaylistsViewHolder) holder).created_by.setText(items.get(position).getUser().getUsername());
            String trackCount = "Tracks : "+items.get(position).getTrack_count();
            ((PlaylistsViewHolder) holder).track_count.setText(trackCount);
            ((PlaylistsViewHolder) holder).openbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: 29-Apr-16 dsfs
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (items.size() == 0) {
            return 0;
        }
        if(isScrollable) {
            return items.size() + 1;
        }else{
            return items.size();
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof PlaylistsViewHolder) {
            ((PlaylistsViewHolder) holder).artwork.setImageResource(0);
        }
    }

    public static class PlaylistsViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView artwork;
        TextView title;
        TextView created_by;
        TextView track_count;
        ImageButton openbtn;

        public PlaylistsViewHolder(View itemView) {
            super(itemView);
            artwork = (NetworkImageView) itemView.findViewById(R.id.artwork_iv_pi);
            title = (TextView) itemView.findViewById(R.id.title_tv_pi);
            created_by = (TextView) itemView.findViewById(R.id.artistn_tv_pi);
            track_count = (TextView) itemView.findViewById(R.id.tracks_tv_pi);
            openbtn = (ImageButton) itemView.findViewById(R.id.openplay_ib_pi);
        }
    }
}