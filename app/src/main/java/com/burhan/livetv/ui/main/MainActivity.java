package com.burhan.livetv.ui.main;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;


import com.burhan.livetv.R;
import com.burhan.livetv.model.Channel;
import com.burhan.livetv.model.ChannelList;
import com.burhan.livetv.ui.main.adapter.ChannelsAdapter;
import com.burhan.livetv.ui.main.mvp.MainPresenter;
import com.burhan.livetv.ui.main.mvp.MainPresenterImpl;
import com.burhan.livetv.ui.main.mvp.MainView;
import com.burhan.livetv.ui.player.BurhansLiveTvPlayer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView, BurhansLiveTvPlayer.PlayerClickCallBack {
    private static final String TAG = MainActivity.class.getName();

    private BurhansLiveTvPlayer player;
    private MainPresenter presenter;
    private RecyclerView rvChannels;
    private ChannelsAdapter adapter;
    private ProgressBar progressBar;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
*/

        // Grabs a reference to the player view
        player = (BurhansLiveTvPlayer) findViewById(R.id.player);
        player.setCallback(new BurhansLiveTvCallBack());
        player.setAutoPlay(true);
        player.setPlayerClickCallBack(this);
        // To play files, you can use Uri.fromFile(new File("..."))
        rvChannels = (RecyclerView) findViewById(R.id.rv_channels);
        RecyclerView.LayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvChannels.setLayoutManager(horizontalLayoutManager);
        adapter = new ChannelsAdapter(new ArrayList<Channel>(), this);
        rvChannels.setAdapter(adapter);

        progressBar = new ProgressBar(this);

        presenter = new MainPresenterImpl(this);
        //presenter.loadChannels();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference channelsRef = database.getReference("channeldata");
        channelsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d(TAG, "onDataChange: ");
                ChannelList channelList = dataSnapshot.getValue(ChannelList.class);
                onChannelsLoaded(channelList.getChannels());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
                FirebaseCrash.log("Data coulnt read from Firebase realtimeDB.");
            }
        });


    }

    @Override
    protected void onPause() {
        if (player != null) player.pause();
        super.onPause();
    }


    @Override
    public void onStartProgress() {
        Log.d(TAG, "onStartProgress() called");
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onChannelsLoaded(List<Channel> channels) {
        Log.d(TAG, "onChannelsLoaded() called with: channels = [" + channels.size() + "] channels");
        if (channels != null && channels.size() > 0 && channels.get(0).getUrl() != null) {
            Log.d(TAG, "player setSource: " + channels.get(0).getUrl());
            player.setSource(Uri.parse(channels.get(0).getUrl()));
            adapter.setData(channels);
        }
    }

    @Override
    public void onChannelSelected(Channel channel) {
        Log.d(TAG, "onChannelSelected() called with: channel = [" + channel + "]");
        if (channel != null && channel.getUrl() != null) {
            player.setSource(Uri.parse(channel.getUrl()));
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onHideLoading() {
        Log.d(TAG, "onHideLoading() called");
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPlayerClicked(boolean isControlsShown) {
        Log.d(TAG, "onPlayerClicked() called");
        rvChannels.setVisibility( isControlsShown ? View.VISIBLE: View.GONE);

    }
}
