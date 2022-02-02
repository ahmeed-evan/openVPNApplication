package com.willdev.openvpn.view;

import static com.android.billingclient.api.BillingClient.SkuType.SUBS;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.willdev.openvpn.R;
import com.willdev.openvpn.utils.Config;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PurchaseActivity extends AppCompatActivity implements PurchasesUpdatedListener, BillingClientStateListener {

    private BillingClient billingClient;

    final String vpn1 = Config.all_month_id;
    final String vpn2 = Config.all_threemonths_id;
    final String vpn3 = Config.all_sixmonths_id;
    final String vpn4 = Config.all_yearly_id;

    private final Map<String, SkuDetails> skusWithSkuDetails = new HashMap<>();
    private final List<String> allSubs = new ArrayList<>(Arrays.asList(
            vpn1, vpn2, vpn3, vpn4));

    private MutableLiveData<Integer> all_check = new MutableLiveData<>();
    @BindView(R.id.one_month)
    RadioButton oneMonth;
    @BindView(R.id.three_month)
    RadioButton threeMonth;
    @BindView(R.id.six_month)
    RadioButton sixMonth;
    @BindView(R.id.one_year)
    RadioButton oneYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_all);
        ButterKnife.bind(this);
        all_check.setValue( -1);
        all_check.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer){
                    case 0:
                        threeMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 1:
                        oneMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 2:
                        threeMonth.setChecked(false);
                        oneMonth.setChecked(false);
                        oneYear.setChecked(false);
                        break;
                    case 3:
                        threeMonth.setChecked(false);
                        sixMonth.setChecked(false);
                        oneMonth.setChecked(false);
                        break;

                }
            }
        });

        oneMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) all_check.postValue(0);
            }
        });
        threeMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) all_check.postValue(1);
            }
        });
        sixMonth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) all_check.postValue(2);
            }
        });
        oneYear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) all_check.postValue(3);
            }
        });

        billingClient = BillingClient
                .newBuilder(this)
                .setListener(this)
                .enablePendingPurchases()
                .build();

        connectToBillingService();
    }

    private void unlock_all(int i) {

        SkuDetails skuDetails = null;

        switch (i) {
            case 0:
                skuDetails = skusWithSkuDetails.get(vpn1);
                break;

            case 1:
                skuDetails = skusWithSkuDetails.get(vpn2);
                break;

            case 2:
                skuDetails = skusWithSkuDetails.get(vpn3);
                break;

            case 3:
                skuDetails = skusWithSkuDetails.get(vpn4);
                break;
        }
        if (skuDetails != null)
            purchase(skuDetails);
        else
            Toast.makeText(PurchaseActivity.this, "Sorry, this subscription is currently unavailable", Toast.LENGTH_SHORT).show();
    }
    @OnClick(R.id.all_pur)
    void unlockAll(){
        if(all_check.getValue() != null)unlock_all(all_check.getValue());
    }

    @OnClick(R.id.btnBack)
    void back(){
        onBackPressed();
    }

    private void connectToBillingService() {
        if (!billingClient.isReady()) {
            billingClient.startConnection(this);
        }
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
        connectToBillingService();
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

    private void purchase(SkuDetails skuDetails) {
        BillingFlowParams params = BillingFlowParams
                .newBuilder()
                .setSkuDetails(skuDetails)
                .build();

        billingClient.launchBillingFlow(this, params);
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
            } else {
                Config.vip_subscription = false;
                Config.all_subscription = false;
            }
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        //if item subscribed
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            Toast.makeText(this, "Subscribed Successfully", Toast.LENGTH_SHORT).show();
            Config.vip_subscription = true;
            Config.all_subscription = true;
            finish();
        }
        //if item already subscribed then check and reflect changes
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(SUBS);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if(alreadyPurchases!=null){
                Toast.makeText(this, "You are already subscribed", Toast.LENGTH_SHORT).show();
            }
        }
        //if Purchase canceled
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(getApplicationContext(),"Purchase Canceled",Toast.LENGTH_SHORT).show();
        }
        // Handle any other error msgs
        else {
            Toast.makeText(getApplicationContext(),"Error "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
        }
    }
}
