package com.rt.utility;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rt.utility.dataservice.AppDataService;
import com.rt.utility.models.AppItem;

import java.util.ArrayList;
import java.util.List;

/*
Learned about Recycler lists from https://antonioleiva.com/recyclerview-listener/
    and https://stackoverflow.com/questions/24471109/recyclerview-onclick
    and Android Programming by The Big Nerd Ranch
 */
public class AppListFragment extends Fragment {
    private RecyclerView appList;
    private List<AppItem> apps;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        AppDataService appDataService = AppDataService.get(getActivity());
        this.apps = new ArrayList<>(appDataService.getApps());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final int NUM_COLS = 3;

        View view = inflater.inflate(R.layout.fragment_app_list, container, false);

        appList = view.findViewById(R.id.app_recycler_list);
        appList.setLayoutManager(new GridLayoutManager(getActivity(), NUM_COLS));

        setupAdapter();

        return view;
    }

    /*
    Initializes the recycler list adapter with the app list and attaches onClick listeners to each app
    Learned about android adapters from Android Programming by The Big Nerd Ranch
     */
    private void setupAdapter(){
        if(isAdded()){ // Ensures the fragment is added to the activity
            AppAdapter adapter = new AppAdapter(this.apps, onAppClickListener);
            appList.setAdapter(adapter);
        }
    }

    /*
    The onClick listener attached to each app. Launches the app activity and passes the app ID
     */
    private OnItemClickListener onAppClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AppItem app) {
            Intent intent = AppActivity.newIntent(getActivity(), app.getId());
            startActivity(intent);
        }
    };

    /*
    Represents each item in the app list recycler view. Handles the custom binding process
    Learned about View Holders from Android Programming by The Big Nerd Ranch
     */
    private static class AppHolder extends RecyclerView.ViewHolder {
        private TextView appName;
        private ImageView appImage;

        private AppHolder(View appContainerView){
            super(appContainerView);
            appName = appContainerView.findViewById(R.id.app_title);
            appImage = appContainerView.findViewById(R.id.app_icon);
        }

        /*
        Binds data to each app in the app recycler view and attaches an onClick listener
         */
        private void bindListApp(final AppItem app, final OnItemClickListener listener) {
            appName.setText(app.toString());
            appImage.setImageResource(app.getResId());

            appImage.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(app);
                }
            });
        }
    }

    /*
    Interface between app objects and recycler list object in the UI
    Learned about Adapters from Android Programming by The Big Nerd Ranch
     */
    private class AppAdapter extends RecyclerView.Adapter<AppHolder> {

        private List<AppItem> apps;
        private OnItemClickListener listener;

        private AppAdapter(List<AppItem> _apps, OnItemClickListener _listener){
            this.apps = _apps;
            this.listener = _listener;
        }

        @NonNull
        @Override
        public AppHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.app_list_item, viewGroup, false);
            return new AppHolder(view);
        }

        @Override
        public void onBindViewHolder(AppHolder appHolder, int position){
            AppItem appItem = apps.get(position);
            appHolder.bindListApp(appItem, listener);
        }

        @Override
        public int getItemCount() {
            return apps.size();
        }
    }

    /*
    Custom interface for app onClick events
     */
    private interface OnItemClickListener {
        void onItemClick(AppItem app);
    }
}
