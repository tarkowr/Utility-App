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
import java.util.Timer;
import java.util.TimerTask;

@TargetApi(23)
public class WifiScannerFragment extends Fragment {

    private WifiAdapter adapter;
    private Button scanBtn;
    private TextView statusTxt;
    private WifiManager wifiManager;
    private List<ScanResult> wifiList;
    private LocalBroadcastManager localBroadcastManager;
    private Timer timer;
    private WifiScannerReceiver wifiReceiver;

    private static final String APP_TAG = "WIFI_SCANNER_APP";
    private static final String SCAN_FAILURE = "SCAN FAILURE";
    private static final int TIMEOUT = 10000;
    private static final int UPDATE_STATUS = 5000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        wifiList = new ArrayList<>();
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

        RecyclerView wifiRecyclerView = view.findViewById(R.id.wifi_list);
        adapter = new WifiAdapter(wifiList);
        scanBtn = view.findViewById(R.id.btn_scan);
        statusTxt = view.findViewById(R.id.txtStatus);

        wifiRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        wifiRecyclerView.setAdapter(adapter);
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        scanBtn.setOnClickListener(scanClick);

        return view;
    }

    /*
    onClick Scan Button Event - Start scan and handle errors
     */
    private View.OnClickListener scanClick = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            if (wifiManager != null && !wifiManager.isWifiEnabled()) {
                statusTxt.setText(R.string.app_wifi_scanner_wifi_disabled);
                return;
            }

            scanBtn.setEnabled(false);
            statusTxt.setText(R.string.app_wifi_scanner_scanning);

            try{
                scan();
            }
            catch (Exception ex){
                Log.d(APP_TAG, Objects.requireNonNull(ex.getMessage()));
                scanFailure();
            }
        }
    };

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
                    scanFailure();
                }

                unRegisterBroadcastReceiver();
                scanBtn.setEnabled(true);
            }
        }
    }

    /*
    Start WiFi Scan and schedule timeout and status update timer tasks
     */
    private void scan() throws Exception{
        wifiReceiver = new WifiScannerReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        localBroadcastManager.registerReceiver(wifiReceiver, intentFilter);
        boolean success = wifiManager.startScan();

        if(!success){
            unRegisterBroadcastReceiver();
            scanBtn.setEnabled(true);
            throw new Exception(SCAN_FAILURE);
        }

        timer = new Timer();
        timer.schedule(new ScanningTimerTask(), UPDATE_STATUS);
        timer.schedule(new ScannerTimeoutTimerTask(), TIMEOUT);
    }

    /*
    Handles a successful scan
     */
    private void scanSuccess(){
        List<ScanResult> results = wifiManager.getScanResults();
        statusTxt.setText(getString(R.string.app_wifi_scanner_networks_found, wifiList.size()));

        wifiList.addAll(results);
        adapter.notifyDataSetChanged();
    }

    /*
    Handles a failed scan
     */
    private void scanFailure(){
        statusTxt.setText(R.string.app_wifi_scanner_error);
    }

    /*
    Cancels all scheduled timer events
     */
    private void cancelTimer(){
        this.timer.cancel();
        this.timer.purge();
    }

    /*
    Unregisters the WiFi Scan Broadcast Receiver
     */
    private void unRegisterBroadcastReceiver(){
        try{
            cancelTimer();
            localBroadcastManager.unregisterReceiver(wifiReceiver);
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        catch (Exception e){
            Log.d(APP_TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    /*
    ViewHolder to represent each WiFi list item view in the WiFi RecyclerView
     */
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

    /*
    Adapter to connect the WiFi RecyclerView with the ScanResults and inflate the list item views
     */
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
                    scanFailure();
                    unRegisterBroadcastReceiver();
                    scanBtn.setEnabled(true);
                }
            });

            this.cancel();
        }
    }
}
