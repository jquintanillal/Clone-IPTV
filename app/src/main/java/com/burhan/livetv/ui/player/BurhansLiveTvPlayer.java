package com.burhan.livetv.ui.player;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;


/**
 * Created by Burhan on 10/08/2017.
 */

public class BurhansLiveTvPlayer extends EasyVideoPlayer {

    private static final String TAG = BurhansLiveTvPlayer.class.getName();
    private PlayerClickCallBack playerClickCallBack;

    public BurhansLiveTvPlayer(Context context) {
        super(context);
        init();
    }

    public BurhansLiveTvPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BurhansLiveTvPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    @Override
    public void toggleControls() {
        super.toggleControls();
        Log.d(TAG, "toggleControls() called");
        if(playerClickCallBack!=null) playerClickCallBack.onPlayerClicked(!isControlsShown());

    }

    public PlayerClickCallBack getPlayerClickCallBack() {
        return playerClickCallBack;
    }

    public void setPlayerClickCallBack(PlayerClickCallBack playerClickCallBack) {
        this.playerClickCallBack = playerClickCallBack;
    }

    public interface PlayerClickCallBack{
        void onPlayerClicked(boolean isControlsShown);
    }
}
