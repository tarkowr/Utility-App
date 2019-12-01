package com.example.utility;

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

import com.example.utility.dataservice.AppDataService;
import com.example.utility.models.AppItem;

import java.util.ArrayList;
import java.util.List;

/*
https://antonioleiva.com/recyclerview-listener/
https://stackoverflow.com/questions/24471109/recyclerview-onclick
 */
public class AppListFragment extends Fragment {

    private RecyclerView appList;
    private final int NUM_COLS = 3;
    private List<AppItem> apps = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        this.apps = InitializeApps();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        appList = view.findViewById(R.id.app_recycler_list);
        appList.setLayoutManager(new GridLayoutManager(getActivity(), NUM_COLS));

        setupAdapter();
        return view;
    }

    private List<AppItem> InitializeApps(){
        return AppDataService.get(getActivity()).getApps();
    }

    private void setupAdapter(){
        if(isAdded()){
            appList.setAdapter(new AppAdapter(apps, onAppClickListener));
        }
    }

    private OnItemClickListener onAppClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AppItem app) {
            // Intent intent = new Intent(getActivity(), app.getActivity());
            // startActivity(intent);
            Intent intent = AppActivity.newIntent(getActivity(), app.getId());
            startActivity(intent);
        }
    };

    private class AppHolder extends RecyclerView.ViewHolder {
        private TextView appName;
        private ImageView appImage;

        public AppHolder(View appContainerView){
            super(appContainerView);
            appName = appContainerView.findViewById(R.id.app_title);
            appImage = appContainerView.findViewById(R.id.app_icon);
        }

        public void bindListApp(final AppItem app, final OnItemClickListener listener) {
            appName.setText(app.toString());
            appImage.setImageResource(app.getResId());

            appImage.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(app);
                }
            });
        }
    }

    private class AppAdapter extends RecyclerView.Adapter<AppHolder> {

        private List<AppItem> apps;
        private OnItemClickListener listener;

        public AppAdapter(List<AppItem> _apps, OnItemClickListener _listener){
            this.apps = _apps;
            this.listener = _listener;
        }

        @Override
        public AppHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
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

    private interface OnItemClickListener {
        void onItemClick(AppItem app);
    }
}
