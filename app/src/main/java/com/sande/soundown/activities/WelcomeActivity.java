package com.sande.soundown.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroViewPager;
import com.sande.soundown.Fragments.Welcome2Fragment;
import com.sande.soundown.Fragments.Welcome3Fragment;
import com.sande.soundown.Fragments.WelcomeFragment;
import com.sande.soundown.Interfaces.CallBackWelcome;
import com.sande.soundown.Utils.PrefsWrapper;

public class WelcomeActivity extends AppIntro implements CallBackWelcome{

    private Welcome3Fragment mFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        addSlide(new WelcomeFragment());
        addSlide(new Welcome2Fragment());
        mFrag=new Welcome3Fragment();
        addSlide(mFrag);
        showSkipButton(false);
        setDoneText("Get Started!");
        setSeparatorColor(Color.parseColor("#ffffff"));
        askForPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.GET_ACCOUNTS},1);
        //check if being called by intent filter
        getAccessTokenFromUrl();
    }

    /*
    First screen can have swipe and progress button
    Second screen show have both disabled
    Thired screen has done button
    Disable swipe on third screen
     */

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        if(newFragment instanceof Welcome2Fragment){
            toggleSwipeLock();
            toggleProgressButton();
            toggleNextPageSwipeLock();
        }else if(newFragment instanceof Welcome3Fragment) {
            toggleProgressButton();
        }
    }


    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        PrefsWrapper.with(this).setIsLoggedIn(true);
        loadMainActivity();
    }

    public void loadMainActivity(){
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }

    public void toggleNextPageSwipeLock( ) {
        AppIntroViewPager pager = getPager();
        boolean pagingState = pager.isNextPagingEnabled();
        setNextPageSwipeLock(pagingState);
    }

    public void toggleSwipeLock() {
        AppIntroViewPager pager = getPager();
        boolean pagingState = pager.isPagingEnabled();
        setSwipeLock(pagingState);
    }

    public void toggleProgressButton() {
        boolean progressButtonState = isProgressButtonEnabled();
        progressButtonState = !progressButtonState;
        setProgressButtonEnabled(progressButtonState);
    }

    private void getAccessTokenFromUrl() {
        final Uri data = this.getIntent().getData();
        if(data != null && data.getScheme().equals("sociallogin") && data.getFragment() != null) {
            String accessToken = data.getFragment().replaceFirst("access_token=", "");
            int amperPos=accessToken.indexOf("&");
            accessToken=accessToken.substring(0,amperPos);
            PrefsWrapper.with(this).setAccessToken(accessToken);
            gotAccessToken();
        }
    }

    @Override
    public void gotAccessToken() {
        AppIntroViewPager pager = getPager();
        mFrag.getUserID();
        pager.setCurrentItem(2);

    }
}
