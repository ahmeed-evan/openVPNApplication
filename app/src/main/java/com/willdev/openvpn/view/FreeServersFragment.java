package com.willdev.openvpn.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdsManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.willdev.openvpn.R;
import com.willdev.openvpn.adapter.NativeAdRecyclerAdapter;
import com.willdev.openvpn.api.WebAPI;
import com.willdev.openvpn.model.Server;
import com.victor.loading.rotate.RotateLoading;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Map;

public class FreeServersFragment extends Fragment implements  NativeAdsManager.Listener, NativeAdRecyclerAdapter.OnSelectListener {

    RecyclerView rcvServers;

    private ArrayList<Server> mPostItemList;
    private NativeAdsManager mNativeAdsManager;
    private RotateLoading progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        loadServers();
        String placement_id = WebAPI.ADMOB_NATIVE;
        mNativeAdsManager = new NativeAdsManager(getActivity(), placement_id, 5);

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
                mNativeAdsManager.loadAds();
            }
        });

        mNativeAdsManager.setListener(this);
        View view = inflater.inflate(R.layout.fragment_native_ad_recycler, container, false);
        rcvServers = view.findViewById(R.id.recyclerView);

        progressDialog = (RotateLoading) view.findViewById(R.id.rotateloading);
        progressDialog.start();

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void loadServers()
    {
        mPostItemList = new ArrayList<>();
        try
        {
            if (!TextUtils.isEmpty(WebAPI.FREE_SERVERS))
            {
                JSONArray jsonArray = new JSONArray(WebAPI.FREE_SERVERS);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject object = (JSONObject) jsonArray.get(i);
                    mPostItemList.add(new Server(object.getString("serverName"),
                            object.getString("flagURL"),
                            object.getString("ovpnConfiguration"),
                            object.getString("vpnUserName"),
                            object.getString("vpnPassword")
                    ));
                    Log.v("Servers", object.getString("ovpnConfiguration"));

                }
            }
        } 
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onAdsLoaded() {
        if (getActivity() == null) {
            return;
        }
        rcvServers.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        rcvServers.addItemDecoration(itemDecoration);
        NativeAdRecyclerAdapter adapter =
                new NativeAdRecyclerAdapter(getActivity(), mPostItemList, mNativeAdsManager, "loaded");
        adapter.setOnSelectListener(this);
        rcvServers.setAdapter(adapter);

        progressDialog.stop();
    }

    @Override
    public void onAdError(AdError adError)
    {
        if (getActivity() == null) {
            return;
        }
        rcvServers.setLayoutManager(new LinearLayoutManager(getActivity()));
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        rcvServers.addItemDecoration(itemDecoration);
        NativeAdRecyclerAdapter adapter =
                new NativeAdRecyclerAdapter(getActivity(), mPostItemList, mNativeAdsManager, "error");
        adapter.setOnSelectListener(this);
        rcvServers.setAdapter(adapter);

        progressDialog.stop();
        //Toast.makeText(getActivity(), adError.getErrorCode() + " " + adError.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSelected(Server server)
    {
        if (getActivity() != null)
        {
            Intent mIntent = new Intent();
            mIntent.putExtra("server", server);
            getActivity().setResult(getActivity().RESULT_OK, mIntent);
            getActivity().finish();
        }
    }
}