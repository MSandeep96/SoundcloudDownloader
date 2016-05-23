package com.sande.soundown.Adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.sande.soundown.GsonFiles.TrackObject;
import com.sande.soundown.Interfaces.CallBackMain;
import com.sande.soundown.Interfaces.HasTrackAdapter;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;
import com.sande.soundown.Utils.PrefsWrapper;
import com.sande.soundown.Utils.UtilsManager;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Sandeep on 28-Apr-16.
 */
public class TracksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater mLayoutInflater;
    private Context mContext;
    public static final int VIEW_TYPE = 0;
    public static final int LOAD_TYPE = 1;
    private ImageLoader mImageLoader;
    private boolean isScrollable = true;
    private ArrayList<TrackObject> items = new ArrayList<>();
    private HashMap<Long,Long> downloadingItems=new HashMap<>();

    public TracksAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
        mContext = context;
        VolleySingleton mSingleton = VolleySingleton.getInstance(context);
        mImageLoader = mSingleton.getImageLoader();
    }

    public void addLikesObjects(ArrayList<TrackObject> mLikes) {
        if (items.size() == 0) {
            items = mLikes;
        } else {
            items.addAll(mLikes);
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
            View mView = mLayoutInflater.inflate(R.layout.likes_item, parent, false);
            return new LikesViewHolder(mView);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        // TODO: 28-Apr-16 hooooly shit
        if (holder instanceof LikesViewHolder) {
            ((LikesViewHolder) holder).artwork.setImageUrl(items.get(position).getArtwork_url(), mImageLoader);
            ((LikesViewHolder) holder).title.setText(items.get(position).getTitle());
            ((LikesViewHolder) holder).artist.setText(items.get(position).getUser().getUsername());
            int dur = items.get(position).getDuration();
            String dura = String.format("%02d", dur / (60 * 1000)) + ":" + String.format("%02d", (dur / 1000) % (60));
            ((LikesViewHolder) holder).duration.setText(dura);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((HasTrackAdapter)mContext).launchDialog(items.get(position));
                }
            });
            boolean fileExists = UtilsManager.doesSongExist(items.get(position).getTitle());
            /*if (items.get(position).isStreamable()) {
                if (fileExists) {
                    ((LikesViewHolder) holder).playbtn.setVisibility(View.VISIBLE);
                } else {
                    ((LikesViewHolder) holder).downbtn.setVisibility(View.VISIBLE);
                }
            }
            ((LikesViewHolder) holder).downbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startDownload(items.get(position));
                }
            });
            ((LikesViewHolder) holder).playbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(UtilsManager.getSongFile(items.get(position).getTitle())), "audio/*");
                    mContext.startActivity(intent);

                }
            });*/

        }

    }

    private void startDownload(TrackObject song) {
        String url=song.getStream_url()+"?oauth_token="+new PrefsWrapper(mContext).getAccessToken();
        String fileName=song.getTitle().replaceAll("\\W+","");
        if(!UtilsManager.isExternalStorageWritable()){
            Toast.makeText(mContext, "SD Card not available", Toast.LENGTH_SHORT).show();
            return;
        }
        Uri downUri=Uri.parse(url);
        DownloadManager.Request req=new DownloadManager.Request(downUri);
        req.setTitle(fileName);
        req.setVisibleInDownloadsUi(false);
        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC,UtilsManager.getSongStorDir(fileName));
        DownloadManager mDownloadManager=(DownloadManager)mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadRef=mDownloadManager.enqueue(req);
        downloadingItems.put(song.getId(),downloadRef);
    }

    @Override
    public int getItemCount() {
        if (items.size() == 0) {
            return 0;
        }
        if (isScrollable) {
            return items.size() + 1;
        } else {
            return items.size();
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof LikesViewHolder) {
            ((LikesViewHolder) holder).artwork.setImageResource(0);
        }
    }

    public static class LikesViewHolder extends RecyclerView.ViewHolder {
        NetworkImageView artwork;
        TextView title;
        TextView artist;
        TextView duration;

        public LikesViewHolder(View itemView) {
            super(itemView);
            artwork = (NetworkImageView) itemView.findViewById(R.id.artwork_iv_li);
            artwork.setDefaultImageResId(R.drawable.albumart);
            title = (TextView) itemView.findViewById(R.id.title_tv_li);
            artist = (TextView) itemView.findViewById(R.id.artistn_tv_li);
            duration = (TextView) itemView.findViewById(R.id.time_tv_li);
        }
    }
}
