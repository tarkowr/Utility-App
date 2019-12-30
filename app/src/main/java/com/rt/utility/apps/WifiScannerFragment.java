package com.rt.utility.apps;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rt.utility.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@TargetApi(23)
public class WifiScannerFragment extends Fragment {

    private RecyclerView wifiRecyclerView;
    private WifiAdapter adapter;
    private Button scanBtn;
    private TextView statusTxt;
    private WifiManager wifiManager;
    private List<ScanResult> wifiList;
    private LocalBroadcastManager localBroadcastManager;

    private static final String APP_TAG = "WIFI_SCANNER_APP";
    private static final String SCAN_FAILURE = "SCAN FAILURE";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        unRegisterBroadcastReceiver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_scanner_app, container, false);

        wifiList = new ArrayList<>();
        adapter = new WifiAdapter(wifiList);

        wifiRecyclerView = view.findViewById(R.id.wifi_list);
        scanBtn = view.findViewById(R.id.btn_scan);
        statusTxt = view.findViewById(R.id.txtStatus);

        statusTxt.setText(R.string.empty_string);
        wifiRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        wifiRecyclerView.setAdapter(adapter);

        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanBtn.setEnabled(false);

                if (wifiManager != null && !wifiManager.isWifiEnabled()) {
                    statusTxt.setText(R.string.app_wifi_scanner_wifi_disabled);
                }
                else{
                    statusTxt.setText(R.string.app_wifi_scanner_scanning);

                    try{
                        scan();
                    }
                    catch (Exception ex){
                        Log.d(APP_TAG, Objects.requireNonNull(ex.getMessage()));
                        scanFailure();
                    }
                }
            }
        });

        return view;
    }

    private void scan() throws Exception{
        localBroadcastManager.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        boolean success = wifiManager.startScan();

        if(!success){
            unRegisterBroadcastReceiver();
            scanBtn.setEnabled(true);
            throw new Exception(SCAN_FAILURE);
        }

        Log.d("TAG", "SCAN STARTED!");
        // TODO: Setup timeout event to cancel wifi scan
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG", "RECEIVED!");
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

            if (success) {
                scanSuccess();
            } else {
                Log.d("TAG", "LATE SCANNING FAILURE");
                scanFailure();
            }

            unRegisterBroadcastReceiver();
            scanBtn.setEnabled(true);
        }
    };

    private void scanSuccess(){
        List<ScanResult> results = wifiManager.getScanResults();
        statusTxt.setText(0 + wifiList.size() + R.string.app_wifi_scanner_networks_found);

        wifiList.addAll(results);
        adapter.notifyDataSetChanged();
    }

    private void scanFailure(){
        statusTxt.setText(R.string.app_wifi_scanner_error);
    }

    private void unRegisterBroadcastReceiver(){
        Log.d("TAG", "UNREGISTERING");
        try{
            localBroadcastManager.unregisterReceiver(wifiReceiver);
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    private class WifiHolder extends RecyclerView.ViewHolder{
        private TextView name;

        private WifiHolder(View taskContainerView) {
            super(taskContainerView);
            name = taskContainerView.findViewById(R.id.wifi_name);
        }

        private void bindListWifi(ScanResult scanResult){
            name.setText(scanResult.BSSID);
        }
    }

    private class WifiAdapter extends RecyclerView.Adapter<WifiHolder>{
        private List<ScanResult> scans;

        private WifiAdapter(List<ScanResult> _scans){
            scans = _scans;
        }

        @NonNull
        @Override
        public WifiHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.wifi_list_item, parent, false);
            return new WifiHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WifiHolder holder, int position) {
            ScanResult scanResult = scans.get(position);
            holder.bindListWifi(scanResult);
        }

        @Override
        public int getItemCount() {
            return scans.size();
        }
    }
}
