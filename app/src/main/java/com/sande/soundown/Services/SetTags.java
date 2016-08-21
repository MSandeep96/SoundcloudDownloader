package com.sande.soundown.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.sande.soundown.GsonFiles.TrackObject;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.Utils.ProjectConstants;
import com.sande.soundown.Utils.UtilsManager;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagOptionSingleton;
import org.jaudiotagger.tag.datatype.Artwork;
import org.jaudiotagger.tag.id3.ID3v23Tag;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class SetTags extends IntentService {

    private TrackObject mObj;

    public SetTags() {
        super("SetTags");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mObj=intent.getParcelableExtra(ProjectConstants.TRACKOBJECT);
        final File mFile= UtilsManager.getDestinationFile(mObj);
        TagOptionSingleton.getInstance().setAndroid(true);
        try {
            final AudioFile audioFile= AudioFileIO.read(mFile);
            audioFile.setTag(new ID3v23Tag());
            final Tag tag=audioFile.getTag();
            String title=mObj.getTitle();
            if(title.contains("-")){
                int pos=title.indexOf("-");
                String artist=title.substring(0,pos-1);
                String track=title.substring(pos+2);
                tag.setField(FieldKey.TITLE,track);
                tag.setField(FieldKey.ARTIST,artist);
            }else{
                tag.setField(FieldKey.TITLE,title);
                tag.setField(FieldKey.ARTIST,mObj.getUser().getUsername());
            }
            tag.setField(FieldKey.ALBUM,"Soundcloud");
            // TODO: 24-05-2016 make album selectable
            tag.setField(FieldKey.COMMENT,"Downloaded with Soundown");
            String url=mObj.getArtwork_url().replace("large","t300x300");
            ImageRequest mReq=new ImageRequest(url, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    FileOutputStream out = null;
                    try {
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        String imageFileName = "JPEG_" + timeStamp + "_";
                        File storageDir = getCacheDir();
                        File cover = File.createTempFile(
                                imageFileName, /* prefix */
                                ".jpg", /* suffix */
                                storageDir /* directory */
                        );
                        out = new FileOutputStream(cover);
                        response.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        Artwork artwork=Artwork.createArtworkFromFile(cover);
                        tag.setField(artwork);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            commitAudio(getBaseContext(),audioFile,mFile);
                            if (out != null) {
                                out.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, 0, 0, null, null, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                        commitAudio(getBaseContext(),audioFile,mFile);
                }
            });
            mReq.setShouldCache(false);
            VolleySingleton.getInstance(this).addToRequestQueue(mReq);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void commitAudio(Context context, AudioFile f, File file) {
        try {
            f.commit();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);
            } else {
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + UtilsManager.getContainingFolder(mObj.getUser().getUsername()))));
            }
        } catch (CannotWriteException e) {
            e.printStackTrace();
        }
    }
}
