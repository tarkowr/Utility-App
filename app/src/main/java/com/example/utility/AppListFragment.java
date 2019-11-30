package com.example.utility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utility.apps.CurrencyExchangeAppActivity;
import com.example.utility.models.AppItem;

import java.util.ArrayList;
import java.util.List;

public class AppListFragment extends Fragment {

    private RecyclerView appList;
    private final int NUM_COLS = 3;
    private List<AppItem> apps = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        InitializeApps();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        appList = view.findViewById(R.id.app_recycler_list);
        appList.setLayoutManager(new GridLayoutManager(getActivity(), NUM_COLS));

        setupAdapter();
        return view;
    }

    private void InitializeApps(){
        apps.add(new AppItem(getResources().getString(R.string.app_currency_exchange),
                R.mipmap.currency_icon,
                CurrencyExchangeAppActivity.class));
    }

    private void setupAdapter(){
        if(isAdded()){
            appList.setAdapter(new AppAdapter(apps));
        }
    }

    private class AppHolder extends RecyclerView.ViewHolder {
        private TextView appName;

        public AppHolder(View appView){
            super(appView);
            appName = (TextView) appView;
        }

        public void bindListApp(AppItem app) {
            appName.setText(app.toString());
        }
    }

    private class AppAdapter extends RecyclerView.Adapter<AppHolder> {
        private List<AppItem> apps;

        public AppAdapter(List<AppItem> _apps){
            this.apps = _apps;
        }

        @Override
        public AppHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            TextView appName = new TextView(getActivity());
            return new AppHolder(appName);
        }

        @Override
        public void onBindViewHolder(AppHolder appHolder, int position){
            AppItem appItem = apps.get(position);
            appHolder.bindListApp(appItem);
        }

        @Override
        public int getItemCount() {
            return apps.size();
        }
    }
}
