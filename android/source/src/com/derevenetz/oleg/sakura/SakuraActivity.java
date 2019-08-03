package com.derevenetz.oleg.sakura;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import org.qtproject.qt5.android.bindings.QtActivity;

import com.google.ads.mediation.admob.AdMobAdapter;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class SakuraActivity extends QtActivity
{
    private boolean        showPersonalizedAds = false;
    private AdView         bannerView          = null;
    private InterstitialAd interstitial        = null;

    private static native void bannerViewHeightUpdated(int height);

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (bannerView != null) {
            bannerView.resume();
        }
    }

    @Override
    public void onPause()
    {
        if (bannerView != null) {
            bannerView.pause();
        }

        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        if (bannerView != null) {
            bannerView.destroy();

            bannerView = null;
        }

        super.onDestroy();
    }

    public int getScreenDPI()
    {
        DisplayMetrics metrics = getResources().getDisplayMetrics();

        return metrics.densityDpi;
    }

    public void shareImage(String image_file)
    {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);

            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", new File(image_file)));

            startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_image_chooser_title)));
        } catch (Exception ex) {
            Log.w("SakuraActivity", "shareImage() : " + ex.toString());
        }
    }

    public void initAds(String app_id, String interstitial_unit_id)
    {
        final String  f_app_id               = app_id;
        final String  f_interstitial_unit_id = interstitial_unit_id;
        final Context f_context              = this;

        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                MobileAds.initialize(f_context, f_app_id);

                interstitial = new InterstitialAd(f_context);

                interstitial.setAdUnitId(f_interstitial_unit_id);

                interstitial.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed()
                    {
                        if (interstitial != null) {
                            AdRequest.Builder builder = new AdRequest.Builder();

                            if (showPersonalizedAds) {
                                interstitial.loadAd(builder.setMaxAdContentRating(AdRequest.MAX_AD_CONTENT_RATING_G)
                                                           .tagForChildDirectedTreatment(true)
                                                           .build());
                            } else {
                                Bundle extras = new Bundle();

                                extras.putString("npa", "1");

                                interstitial.loadAd(builder.setMaxAdContentRating(AdRequest.MAX_AD_CONTENT_RATING_G)
                                                           .tagForChildDirectedTreatment(true)
                                                           .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                                           .build());
                            }
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode)
                    {
                        if (interstitial != null) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run()
                                {
                                    if (interstitial != null) {
                                        AdRequest.Builder builder = new AdRequest.Builder();

                                        if (showPersonalizedAds) {
                                            interstitial.loadAd(builder.setMaxAdContentRating(AdRequest.MAX_AD_CONTENT_RATING_G)
                                                                       .tagForChildDirectedTreatment(true)
                                                                       .build());
                                        } else {
                                            Bundle extras = new Bundle();

                                            extras.putString("npa", "1");

                                            interstitial.loadAd(builder.setMaxAdContentRating(AdRequest.MAX_AD_CONTENT_RATING_G)
                                                                       .tagForChildDirectedTreatment(true)
                                                                       .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                                                       .build());
                                        }
                                    }
                                }
                            }, 60000);
                        }
                    }
                });

                AdRequest.Builder builder = new AdRequest.Builder();

                if (showPersonalizedAds) {
                    interstitial.loadAd(builder.setMaxAdContentRating(AdRequest.MAX_AD_CONTENT_RATING_G)
                                               .tagForChildDirectedTreatment(true)
                                               .build());
                } else {
                    Bundle extras = new Bundle();

                    extras.putString("npa", "1");

                    interstitial.loadAd(builder.setMaxAdContentRating(AdRequest.MAX_AD_CONTENT_RATING_G)
                                               .tagForChildDirectedTreatment(true)
                                               .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                               .build());
                }
            }
        });
    }

    public void setAdsPersonalization(boolean personalized)
    {
        final boolean f_personalized = personalized;

        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                showPersonalizedAds = f_personalized;
            }
        });
    }

    public void showBannerView(String unit_id)
    {
        final String  f_unit_id = unit_id;
        final Context f_context = this;

        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                View view = getWindow().getDecorView().getRootView();

                if (view instanceof ViewGroup) {
                    ViewGroup view_group = (ViewGroup)view;

                    if (bannerView != null) {
                        view_group.removeView(bannerView);

                        bannerView.destroy();

                        bannerViewHeightUpdated(0);

                        bannerView = null;
                    }

                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                                                                   FrameLayout.LayoutParams.WRAP_CONTENT,
                                                                                   Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

                    bannerView = new AdView(f_context);

                    bannerView.setAdSize(AdSize.SMART_BANNER);
                    bannerView.setAdUnitId(f_unit_id);
                    bannerView.setLayoutParams(params);
                    bannerView.setVisibility(View.GONE);

                    bannerView.setAdListener(new AdListener() {
                        @Override
                        public void onAdLoaded()
                        {
                            if (bannerView != null) {
                                bannerView.setVisibility(View.VISIBLE);

                                bannerView.post(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        if (bannerView != null) {
                                            bannerViewHeightUpdated(bannerView.getHeight());
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(int errorCode)
                        {
                            if (bannerView != null) {
                                bannerView.setVisibility(View.VISIBLE);

                                bannerView.post(new Runnable() {
                                    @Override
                                    public void run()
                                    {
                                        if (bannerView != null) {
                                            bannerViewHeightUpdated(bannerView.getHeight());
                                        }
                                    }
                                });
                            }
                        }
                    });

                    view_group.addView(bannerView);

                    AdRequest.Builder builder = new AdRequest.Builder();

                    if (showPersonalizedAds) {
                        bannerView.loadAd(builder.setMaxAdContentRating(AdRequest.MAX_AD_CONTENT_RATING_G)
                                                 .tagForChildDirectedTreatment(true)
                                                 .build());
                    } else {
                        Bundle extras = new Bundle();

                        extras.putString("npa", "1");

                        bannerView.loadAd(builder.setMaxAdContentRating(AdRequest.MAX_AD_CONTENT_RATING_G)
                                                 .tagForChildDirectedTreatment(true)
                                                 .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                                 .build());
                    }
                }
            }
        });
    }

    public void hideBannerView()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                View view = getWindow().getDecorView().getRootView();

                if (view instanceof ViewGroup) {
                    ViewGroup view_group = (ViewGroup)view;

                    if (bannerView != null) {
                        view_group.removeView(bannerView);

                        bannerView.destroy();

                        bannerViewHeightUpdated(0);

                        bannerView = null;
                    }
                }
            }
        });
    }

    public void showInterstitial()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                if (interstitial != null && interstitial.isLoaded()) {
                    interstitial.show();
                }
            }
        });
    }
}
