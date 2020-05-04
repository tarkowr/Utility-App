package com.rt.utility.apps;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rt.utility.R;
import com.rt.utility.helpers.JavaUtils;
import com.rt.utility.models.Wifi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/*
Followed this tutorial to build the wifi scanner: https://medium.com/@ssaurel/develop-a-wifi-scanner-android-application-daa3b77feb73
 */
@TargetApi(23)
public class WifiScannerFragment extends Fragment {

    private RecyclerView wifiRecyclerView;
    private WifiAdapter adapter;
    private Button scanBtn;
    private TextView statusTxt;
    private WifiManager wifiManager;
    private List<Wifi> wifiList = new ArrayList<>();
    private Timer scanningTimer;
    private Timer delayScanTimer;
    private WifiScannerReceiver wifiReceiver;
    private Boolean useCachedResults;

    private static final String APP_TAG = "WIFI_SCANNER_APP";
    private static final String SCAN_FAILURE = "SCAN FAILURE";
    private static final int SCAN_INTERVAL = 30000;
    private static final int TIMEOUT = 10000;
    private static final int UPDATE_STATUS = 3000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        useCachedResults = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        unRegisterBroadcastReceiver();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wifi_scanner_app, container, false);

        wifiRecyclerView = view.findViewById(R.id.wifi_list);
        scanBtn = view.findViewById(R.id.btn_scan);
        statusTxt = view.findViewById(R.id.txtStatus);

        adapter = new WifiAdapter(wifiList);
        wifiRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        wifiRecyclerView.setAdapter(adapter);
        wifiManager = (WifiManager) Objects.requireNonNull(getActivity()).getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        scanBtn.setOnClickListener(scanClick);

        return view;
    }

    /*
    onClick Scan Button Event - Start scan and handle errors
     */
    private View.OnClickListener scanClick = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if(useCachedResults){
                return;
            }

            wifiList.clear();
            adapter.notifyDataSetChanged();

            if (wifiManager != null && !wifiManager.isWifiEnabled()) {
                statusTxt.setText(R.string.app_wifi_scanner_wifi_disabled);
                return;
            }

            try{
                scan();
            }
            catch (Exception ex){
                Log.d(APP_TAG, Objects.requireNonNull(ex.getMessage()));
                scanError(R.string.app_wifi_scanner_enable_location_permission);

                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(myIntent);
                Objects.requireNonNull(getActivity()).finish();
            }
        }
    };

    /*
    Start WiFi Scan and schedule timeout and status update timer tasks
     */
    private void scan() throws Exception{
        useCachedResults = true;
        scanBtn.setEnabled(false);

        delayScanTimer = new Timer();
        delayScanTimer.schedule(new EnableScanTimerTask(), SCAN_INTERVAL);

        statusTxt.setText(R.string.app_wifi_scanner_scanning);

        wifiReceiver = new WifiScannerReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        Objects.requireNonNull(getActivity()).registerReceiver(wifiReceiver, intentFilter);
        boolean success = wifiManager.startScan();

        if(!success){
            throw new Exception(SCAN_FAILURE);
        }

        scanningTimer = new Timer();
        scanningTimer.schedule(new ScanningTimerTask(), UPDATE_STATUS);
        scanningTimer.schedule(new ScannerTimeoutTimerTask(), TIMEOUT);
    }

    /*
    Handles a successful scan
     */
    private void scanSuccess(){
        List<ScanResult> results = wifiManager.getScanResults();
        final int NUM_LEVELS = 101;

        for(ScanResult result : results){
            int strength = WifiManager.calculateSignalLevel(result.level, NUM_LEVELS);
            String name = result.SSID;

            if (!JavaUtils.CheckIfEmptyString(name)){
                // name = getString(R.string.app_wifi_scanner_nameless_network);
                wifiList.add(new Wifi(name, strength, result));
            }
        }

        Collections.sort(wifiList, new Comparator<Wifi>() {
            @Override
            public int compare(Wifi a, Wifi b) {
                return Integer.compare(b.getStrength(), a.getStrength());
            }
        });

        statusTxt.setText(getString(R.string.app_wifi_scanner_networks_found, wifiList.size()));

        if (results.size() == 0){
            statusTxt.setText(R.string.app_wifi_scanner_ensure_location_granted);
        }

        scanBtn.setEnabled(true);

        adapter.notifyDataSetChanged();
        unRegisterBroadcastReceiver();
    }

    /*
    Handles a failed scan
     */
    private void scanError(int strId){
        statusTxt.setText(strId);
        unRegisterBroadcastReceiver();
        cancelDelayTimer();
        scanBtn.setEnabled(true);
    }

    /*
    Cancels all scheduled timer events
     */
    private void cancelTimer(){
        try{
            this.scanningTimer.cancel();
            this.scanningTimer.purge();
        }
        catch (Exception e){
            // Do nothing
        }
    }

    /*
    Cancels the delay timer event
     */
    private void cancelDelayTimer(){
        this.delayScanTimer.cancel();
        this.delayScanTimer.purge();
    }

    /*
    Unregisters the WiFi Scan Broadcast Receiver
     */
    private void unRegisterBroadcastReceiver(){
        try{
            Objects.requireNonNull(getActivity()).unregisterReceiver(wifiReceiver);
        }
        catch (Exception e){
            Log.d(APP_TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    /*
     Broadcast Receiver event to handle incoming WiFi Scan Results
      */
    public class WifiScannerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(Objects.equals(intent.getAction(), WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
                cancelTimer();
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

                if (success) {
                    scanSuccess();
                } else {
                    scanError(R.string.app_wifi_scanner_error);
                }
            }
        }
    }

    /*
    ViewHolder to represent each WiFi list item view in the WiFi RecyclerView
     */
    private class WifiHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView strength;

        private WifiHolder(View taskContainerView) {
            super(taskContainerView);
            name = taskContainerView.findViewById(R.id.wifi_name);
            strength = taskContainerView.findViewById(R.id.wifi_strength);
        }

        private void bindListWifi(Wifi wifi){
            name.setText(wifi.getName());
            strength.setText(Integer.toString(wifi.getStrength()));
        }
    }

    /*
    Adapter to connect the WiFi RecyclerView with the ScanResults and inflate the list item views
     */
    private class WifiAdapter extends RecyclerView.Adapter<WifiHolder>{
        private List<Wifi> scans;

        private WifiAdapter(List<Wifi> _scans){
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
            Wifi wifi = scans.get(position);
            holder.bindListWifi(wifi);
        }

        @Override
        public int getItemCount() {
            return scans.size();
        }
    }

    /*
    Update Status for Extended Scan Event
     */
    private class ScanningTimerTask extends TimerTask {
        @Override
        public void run() {
            if (getActivity() == null){
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    statusTxt.setText(R.string.app_wifi_scanner_still_scanning);
                }
            });

            this.cancel();
        }
    }

    /*
    Scan Timeout Event to kill the scan process if its taking too long
     */
    private class ScannerTimeoutTimerTask extends TimerTask {
        @Override
        public void run() {
            if (getActivity() == null){
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    scanError(R.string.app_wifi_scanner_error);
                }
            });

            this.cancel();
        }
    }

    /*
    Toggle cached results events
     */
    private class EnableScanTimerTask extends TimerTask {
        @Override
        public void run() {
            if (getActivity() == null) {
                return;
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    useCachedResults = false;
                }
            });

            this.cancel();
        }
    }
}
