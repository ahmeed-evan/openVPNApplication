package com.willdev.openvpn.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.bumptech.glide.Glide;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener;
import com.willdev.openvpn.CheckInternetConnection;
import com.willdev.openvpn.R;
import com.willdev.openvpn.SharedPreference;
import com.willdev.openvpn.api.WebAPI;
import com.willdev.openvpn.databinding.FragmentMainBinding;
import com.willdev.openvpn.interfaces.ChangeServer;
import com.willdev.openvpn.model.Server;
import com.willdev.openvpn.utils.Config;
import com.loopj.android.http.HttpGet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;
import de.blinkt.openvpn.core.VpnStatus;

import static android.app.Activity.RESULT_OK;
import static com.android.billingclient.api.BillingClient.SkuType.SUBS;

public class MainFragment extends Fragment implements View.OnClickListener, ChangeServer, PurchasesUpdatedListener, BillingClientStateListener {
    private Server server;

    private CheckInternetConnection connection;
    private OpenVPNThread vpnThread = new OpenVPNThread();
    private OpenVPNService vpnService = new OpenVPNService();
    boolean vpnStart = false;
    private SharedPreference preference;
    private FragmentMainBinding binding;
    private View mView;
    private static final int REQUEST_CODE = 101;
    private TextView ipConnection;

    private BillingClient billingClient;

    final String vpn1 = Config.all_month_id;
    final String vpn2 = Config.all_threemonths_id;
    final String vpn3 = Config.all_sixmonths_id;
    final String vpn4 = Config.all_yearly_id;

    private final Map<String, SkuDetails> skusWithSkuDetails = new HashMap<>();
    private final List<String> allSubs = new ArrayList<>(Arrays.asList(
            vpn1, vpn2, vpn3, vpn4));

    NativeAdLayout nativeAdLayout;
    FrameLayout frameLayout;
    private InterstitialAd mInterstitialAdMob;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        if (mView == null)
        {
            binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
            mView = binding.getRoot();
            nativeAdLayout = mView.findViewById(R.id.native_ad_container);
            frameLayout = binding.flAdplaceholder;
            ipConnection = mView.findViewById(R.id.tv_ip_address);

            MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                    for (String adapterClass : statusMap.keySet()) {
                        AdapterStatus status = statusMap.get(adapterClass);
                        Log.d("MyApp", String.format(
                                "Adapter name: %s, Description: %s, Latency: %d",
                                adapterClass, status.getDescription(), status.getLatency()));
                    }

                    initializeAll();
                }
            });

        } else {
            if (mView.getParent() != null) {
                ((ViewGroup) mView.getParent()).removeView(mView);
            }
        }
        showIP();

        return mView;
    }


    private void initializeAll()
    {

        showIP();

        //Todo add native ad
        if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_ADMOB))
        {
            AdRequest adRequest = new AdRequest.Builder().build();

            InterstitialAd.load(getActivity(),WebAPI.ADMOB_INTERSTITIAL, adRequest,
                    new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            // The mInterstitialAd reference will be null until
                            // an ad is loaded.
                            mInterstitialAdMob = interstitialAd;
                            Log.i("INTERSTITIAL", "onAdLoaded");

                            if (mInterstitialAdMob != null) {

                                mInterstitialAdMob.setFullScreenContentCallback(new FullScreenContentCallback(){
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        Log.d("TAG", "The ad was dismissed.");
                                        prepareVpn();
                                    }

                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        Log.d("TAG", "The ad failed to show.");
                                        prepareVpn();
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        mInterstitialAdMob = null;
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });

                            } else {
                                Log.d("TAG", "The interstitial ad wasn't ready yet.");
                                prepareVpn();
                            }
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.i("INTERSTITIAL", loadAdError.getMessage());
                            mInterstitialAdMob = null;
                        }
                    });

            AdLoader adLoader = new AdLoader.Builder(getActivity(), WebAPI.ADMOB_NATIVE)
                    .forNativeAd(new OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                            frameLayout.setVisibility(View.VISIBLE);
                            NativeAdView adView = (NativeAdView) getLayoutInflater()
                                    .inflate(R.layout.ad_unifined, null);
                            if ((!Config.vip_subscription && !Config.all_subscription))
                            {
                                populateUnifiedNativeAdView(nativeAd, adView);
                                frameLayout.removeAllViews();
                                frameLayout.addView(adView);
                            }
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            // Handle the failure by logging, altering the UI, and so on.
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder()
                            // Methods in the NativeAdOptions.Builder class can be
                            // used here to specify individual options settings.
                            .build())
                    .build();


            VideoOptions videoOptions = new VideoOptions.Builder()
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            adLoader.loadAd(new AdRequest.Builder()
                    .build());

            ((MainActivity) getActivity()).currentVipServer.observe(getActivity(), new Observer<Server>()
            {
                @Override
                public void onChanged(Server currentServer)
                {
                    server = currentServer;
                    if (vpnStart)
                    {
                        stopVpn();
                    }

                    binding.countryName.setText(server.getCountry());
                    binding.logTv.setText("Disconnected");
                    showIP();
                    updateCurrentVipServerIcon(server.getFlagUrl());

                    if (((MainActivity) getActivity()).isActivateServer())
                    {
                        if ((!Config.vip_subscription && !Config.all_subscription)) {
                            if (mInterstitialAdMob != null) {
                                mInterstitialAdMob.show(getActivity());
                            } else {
                                prepareVpn();
                                Log.d("TAG", "The interstitial ad wasn't ready yet.");
                            }
                        }
                        else prepareVpn();
                    }
                }
            });
            connection = new CheckInternetConnection();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));

            binding.purchaseLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(getActivity(), PurchaseActivity.class);
                    startActivity(intent);
                }
            });
        }
        else if (WebAPI.ADS_TYPE.equals(WebAPI.ADS_TYPE_FACEBOOK_ADS))
        {
            com.facebook.ads.InterstitialAd mInterstitialAd = new  com.facebook.ads.InterstitialAd(getActivity(), WebAPI.ADMOB_INTERSTITIAL);
            com.facebook.ads.InterstitialAdListener interstitialAdListener = new com.facebook.ads.InterstitialAdListener() {
                @Override
                public void onError(Ad ad, AdError adError)
                {
                    Log.d("TAG", "The interstitial wasn't loaded yet. " + adError.getErrorCode());
                    prepareVpn();
                }

                @Override
                public void onAdLoaded(Ad ad)
                {
                    if (mInterstitialAd.isAdLoaded())
                    {
                        if (!Config.vip_subscription && !Config.all_subscription)
                            mInterstitialAd.show();
                    }
                    else
                    {
                        Log.d("TAG", "The interstitial wasn't loaded yet.");
                    }
                }

                @Override
                public void onAdClicked(Ad ad)
                {

                }

                @Override
                public void onLoggingImpression(Ad ad)
                {

                }

                @Override
                public void onInterstitialDisplayed(Ad ad)
                {
                    prepareVpn();
                }

                @Override
                public void onInterstitialDismissed(Ad ad)
                {
                }
            };

            NativeAd nativeAd = new NativeAd(getContext(), WebAPI.ADMOB_NATIVE);
            NativeAdListener nativeAdListener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad)
                {
                }

                @Override
                public void onError(Ad ad, AdError adError)
                {
                    Log.w("AdLoader", WebAPI.ADMOB_NATIVE);
                    Log.w("AdLoader", "onAdFailedToLoad" + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    if (nativeAd == null || nativeAd != ad) {
                        return;
                    }
                    nativeAd.unregisterView();

                    if ((!Config.vip_subscription && !Config.all_subscription))
                    {
                        nativeAdLayout.setVisibility(View.VISIBLE);
                    }
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_layout, nativeAdLayout, false);
                    nativeAdLayout.addView(adView);

                    LinearLayout adChoicesContainer = getActivity().findViewById(R.id.ad_choices_container);
                    AdOptionsView adOptionsView = new AdOptionsView(getContext(), nativeAd, nativeAdLayout);
                    adChoicesContainer.removeAllViews();
                    adChoicesContainer.addView(adOptionsView, 0);

                   com.facebook.ads.MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
                    TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
                    com.facebook.ads.MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
                    TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
                    TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
                    TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
                    Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

                    nativeAdTitle.setText(nativeAd.getAdvertiserName());
                    nativeAdBody.setText(nativeAd.getAdBodyText());
                    nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                    nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                    nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                    sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

                   List<View> clickableViews = new ArrayList<>();
                    clickableViews.add(nativeAdTitle);
                    clickableViews.add(nativeAdCallToAction);

                    nativeAd.registerViewForInteraction(
                            adView, nativeAdMedia, nativeAdIcon, clickableViews);
                }

                @Override
                public void onAdClicked(Ad ad)
                {

                }

                @Override
                public void onLoggingImpression(Ad ad)
                {

                }
            };
             nativeAd.loadAd(
                    nativeAd.buildLoadAdConfig()
                            .withAdListener(nativeAdListener)
                            .build());


            ((MainActivity) getActivity()).currentVipServer.observe(getActivity(), new Observer<Server>()
            {
                @Override
                public void onChanged(Server currentServer)
                {
                    server = currentServer;
                    if (vpnStart)
                    {
                        stopVpn();
                    }
                    if (((MainActivity) getActivity()).isActivateServer())
                    {
                        if ((!Config.vip_subscription && !Config.all_subscription))
                        {
                            mInterstitialAd.loadAd(
                                    mInterstitialAd.buildLoadAdConfig()
                                            .withAdListener(interstitialAdListener)
                                            .build());
                        }
                        else
                        {
                            prepareVpn();
                        }
                    }

                    binding.countryName.setText(server.getCountry());
                    binding.logTv.setText("Disconnected");
                    updateCurrentVipServerIcon(server.getFlagUrl());
                }
            });
            connection = new CheckInternetConnection();
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));

            binding.purchaseLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(getActivity(), PurchaseActivity.class);
                    startActivity(intent);
                }
            });
        }
        binding.category.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (getActivity() != null) ((MainActivity) getActivity()).openCloseDrawer();

            }
        });

        billingClient = BillingClient
                .newBuilder(getContext())
                .setListener(this)
                .enablePendingPurchases()
                .build();

        connectToBillingService();
    }

    private void connectToBillingService() {
        if (!billingClient.isReady()) {
            billingClient.startConnection(this);
        }
    }

    private void populateUnifiedNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView adView) {
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

         adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

       if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }


        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.vpnBtn.setOnClickListener(this);
        binding.currentConnectionLayout.setOnClickListener(this);
       isServiceRunning();
        VpnStatus.initLogCache(getActivity().getCacheDir());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vpnBtn: {
               if (vpnStart) {
                    confirmDisconnect();
                } else {
                    prepareVpn();
                }
                break;
            }

            case R.id.currentConnectionLayout: {
                if (getActivity() != null)
                {
                    Intent mIntent = new Intent(this.getActivity(), Servers.class);
                    getActivity().startActivityForResult(mIntent, REQUEST_CODE);
                }
                break;

            }
        }
    }

    public void confirmDisconnect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getActivity().getString(R.string.connection_close_confirm));

        builder.setPositiveButton(getActivity().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                stopVpn();
            }
        });
        builder.setNegativeButton(getActivity().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void prepareVpn() {
        if (!vpnStart) {
            if (getInternetStatus()) {

               Intent intent = VpnService.prepare(getContext());

                if (intent != null) {
                    startActivityForResult(intent, 1);
                } else startVpn();//have already permission

               status("connecting");

            } else {

                showToast("you have no internet connection !!");
            }

        } else if (stopVpn()) {

              showToast("Disconnect Successfully");
        }
    }


    public boolean stopVpn() {
        try {
            vpnThread.stop();

            status("connect");
            vpnStart = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {


            startVpn();
        } else {
            showToast("Permission Deny !! ");
        }
    }


    public boolean getInternetStatus() {
        return connection.netCheck(getActivity());
    }


    public void isServiceRunning() {
        setStatus(vpnService.getStatus());
    }


    private void startVpn() {
        try {
            OpenVpnApi.startVpn(getContext(), server.getOvpn(), server.getCountry(), server.getOvpnUserName(), server.getOvpnUserPassword());

            binding.logTv.setText("Connecting...");
            vpnStart = true;

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void setStatus(String connectionState) {
        if (connectionState != null)
            switch (connectionState) {
                case "DISCONNECTED":
                    status("connect");
                    showIP();
                    vpnStart = false;
                    vpnService.setDefaultStatus();
                    binding.logTv.setText("Disocnnected");
                    binding.connectionStatusImage.setBackgroundResource(R.drawable.iconstart);
                    break;
                case "CONNECTED":
                    showIP();
                    vpnStart = true;
                    status("connected");
                    binding.logTv.setText("Connected");
                    binding.connectionStatusImage.setBackgroundResource(R.drawable.iconstop);
                    break;
                case "WAIT":
                    binding.logTv.setText("Waiting...!!");
                    binding.connectionStatusImage.setBackgroundResource(R.drawable.iconwait);
                    break;
                case "AUTH":
                    binding.logTv.setText("Please Wait.. !");
                    binding.connectionStatusImage.setBackgroundResource(R.drawable.iconwait);
                    break;
                case "RECONNECTING":
                    status("connecting");
                    binding.logTv.setText("Reconnecting...");
                    binding.connectionStatusImage.setBackgroundResource(R.drawable.iconwait);
                    break;
                case "NONETWORK":
                    binding.logTv.setText("No Network ");
                    binding.connectionStatusImage.setBackgroundResource(R.drawable.iconstart);
                    break;
            }

    }


    public void status(String status) {

        if (status.equals("connect")) {


        } else if (status.equals("connecting")) {

        } else if (status.equals("connected")) {



        } else if (status.equals("tryDifferentServer")) {


        } else if (status.equals("loading")) {
              } else if (status.equals("invalidDevice")) {
            binding.vpnBtn.setBackgroundResource(R.drawable.button_connected);
           } else if (status.equals("authenticationCheck")) {
            binding.vpnBtn.setBackgroundResource(R.drawable.button_connecting);
         }

    }


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                setStatus(intent.getStringExtra("state"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                String duration = intent.getStringExtra("duration");
                String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
                String byteIn = intent.getStringExtra("byteIn");
                String byteOut = intent.getStringExtra("byteOut");

                if (duration == null) duration = "00:00:00";
                if (lastPacketReceive == null) lastPacketReceive = "0";
                if (byteIn == null) byteIn = "Wait";
                if (byteOut == null) byteOut = "Wait";
                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    public void updateConnectionStatus(String duration, String lastPacketReceive, String byteIn, String byteOut) {
        binding.durationTv.setText("Time: " + duration);
        String byteinKb = byteIn.split("-")[0];
        String byteoutKb = byteOut.split("-")[0];
        binding.byteInTv.setText(byteinKb);
        binding.byteOutTv.setText(byteoutKb);
    }


    public void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


    public void updateCurrentVipServerIcon(String serverIcon) {
        Glide.with(getActivity())
                .load(serverIcon)
                .into(binding.selectedServerIcon);

    }


    @Override
    public void newServer(Server server) {
        this.server = server;

        if (vpnStart) {
            stopVpn();
        }

        prepareVpn();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Config.vip_subscription && Config.all_subscription) {
            nativeAdLayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.GONE);
        }
    }

    void showIP()
    {
        new RequestTask().execute("https://checkip.amazonaws.com/");
    }

    @Override
    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            querySkuDetailsAsync(
                    SUBS,
                    new ArrayList<>(allSubs)
            );
            queryPurchases();
        }
    }

    @Override
    public void onBillingServiceDisconnected() {

    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else{

                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ipConnection.setText(result);
        }
    }

    private void queryPurchases() {
        Purchase.PurchasesResult result = billingClient.queryPurchases(SUBS);
        List<Purchase> purchases = result.getPurchasesList();
        List<String> skus = new ArrayList<>();

        if (purchases != null) {
            for (Purchase purchase : purchases) {
                skus.add(purchase.getSku());
            }

            if (skus.contains(vpn1) ||
                    skus.contains(vpn2) ||
                    skus.contains(vpn3) ||
                    skus.contains(vpn4)
            ) {
                Config.vip_subscription = true;
                Config.all_subscription = true;
                nativeAdLayout.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
            } else {
                Config.vip_subscription = false;
                Config.all_subscription = false;
                nativeAdLayout.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void querySkuDetailsAsync(@BillingClient.SkuType String skuType, List<String> skuList) {
        SkuDetailsParams params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(skuType)
                .build();

        billingClient.querySkuDetailsAsync(
                params, (billingResult, skuDetailsList) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                        for (SkuDetails details : skuDetailsList) {
                            skusWithSkuDetails.put(details.getSku(), details);
                        }
                    }
                }
        );
    }
}
