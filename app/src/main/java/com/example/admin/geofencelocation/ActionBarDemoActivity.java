/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.admin.geofencelocation;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.google.android.gms.plus.PlusOneDummyView.TAG;

/**
 * A sample showing how to use the ActionBar as an overlay when the video is playing in fullscreen.
 *
 * The ActionBar is the only view allowed to overlay the player, so it is a useful place to put
 * custom application controls when the video is in fullscreen. The ActionBar can not change back
 * and forth between normal mode and overlay mode, so to make sure our application's content
 * is not covered by the ActionBar we want to pad our root view when we are not in fullscreen.
 */
@TargetApi(11)
public class ActionBarDemoActivity extends YouTubeFailureRecoveryActivity implements
    YouTubePlayer.OnFullscreenListener {

  private ActionBarPaddedFrameLayout viewContainer;
  private YouTubePlayerFragment playerFragment;
  private View tutorialTextView;

    String oynat;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.action_bar_demo);


    Log.d("Yusuf", "onCreate: ");





    viewContainer = (ActionBarPaddedFrameLayout) findViewById(R.id.view_container);
    playerFragment =
        (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.player_fragment);
    tutorialTextView = findViewById(R.id.tutorial_text);
    playerFragment.initialize(DeveloperKey.DEVELOPER_KEY, this);
    viewContainer.setActionBar(getActionBar());

    // Action bar background is transparent by default.
    getActionBar().setBackgroundDrawable(new ColorDrawable(0xAA000000));
  }

  @Override
  public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer player,
                                      boolean wasRestored) {


          final String location = getIntent().getExtras().getString("location");
          Log.d("Yusuf", "onCreate: location is " + location);




          DatabaseReference oku = FirebaseDatabase.getInstance().getReference().child("konumlar");
          ValueEventListener listener = new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  long size = dataSnapshot.getChildrenCount();
                  int kayit=1;

                  for (int i = 1; i <= size; i++) {
                      String holdName = dataSnapshot.child("" + i).child("isim").getValue(String.class);
                      if (holdName.equals(location))  {
                          kayit=i;
                          break;
                      }
                  }

                  String youtube = dataSnapshot.child("" + kayit).child("link").getValue(String.class);
                  oynat=youtube;
                  Log.d("Yusuf", "BBBBBBBBBBBBBBBBB " + youtube);

                  player.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                  player.setOnFullscreenListener(ActionBarDemoActivity.this);
                  Log.d("Yusuf", "CCCCCCCCCCCCCCCCCCCC " + oynat);
                  player.cueVideo(oynat);



              };

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          };oku.addListenerForSingleValueEvent(listener);










  }

  @Override
  protected YouTubePlayer.Provider getYouTubePlayerProvider() {
    return (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.player_fragment);
  }

  @Override
  public void onFullscreen(boolean fullscreen) {
    viewContainer.setEnablePadding(!fullscreen);

    ViewGroup.LayoutParams playerParams = playerFragment.getView().getLayoutParams();
    if (fullscreen) {
      tutorialTextView.setVisibility(View.GONE);
      playerParams.width = MATCH_PARENT;
      playerParams.height = MATCH_PARENT;
    } else {
      tutorialTextView.setVisibility(View.VISIBLE);
      playerParams.width = 0;
      playerParams.height = WRAP_CONTENT;
    }
  }

  /**
   * This is a FrameLayout which adds top-padding equal to the height of the ActionBar unless
   * disabled by {@link #setEnablePadding(boolean)}.
   */
  public static final class ActionBarPaddedFrameLayout extends FrameLayout {

    private ActionBar actionBar;
    private boolean paddingEnabled;

    public ActionBarPaddedFrameLayout(Context context) {
      this(context, null);
    }

    public ActionBarPaddedFrameLayout(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
    }

    public ActionBarPaddedFrameLayout(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);
      paddingEnabled = true;
    }

    public void setActionBar(ActionBar actionBar) {
      this.actionBar = actionBar;
      requestLayout();
    }

    public void setEnablePadding(boolean enable) {
      paddingEnabled = enable;
      requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      int topPadding =
          paddingEnabled && actionBar != null && actionBar.isShowing() ? actionBar.getHeight() : 0;
      setPadding(0, topPadding, 0, 0);

      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

  }

}
