package com.example.utility;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
    private List<AppItem> apps;
    private AppAdapter adapter;
    private AppDataService appDataService;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        this.appDataService = AppDataService.get(getActivity());
        this.apps = new ArrayList<>(this.appDataService.getApps());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        appList = view.findViewById(R.id.app_recycler_list);
        appList.setLayoutManager(new GridLayoutManager(getActivity(), NUM_COLS));

        setupAdapter();

        final EditText searchBar = view.findViewById(R.id.app_search_bar);
        searchBar.addTextChangedListener(search);

        TextView cancel = view.findViewById(R.id.txtCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                searchBar.clearFocus();

                if(!searchBar.getText().equals("")){
                    searchBar.setText(null);
                    handleAppSearch("");
                }
            }
        });

        return view;
    }

    private TextWatcher search = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            handleAppSearch(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) { }
    };

    private void handleAppSearch(String search){
        apps.clear();
        List<AppItem> appList = appDataService.ReturnAppsByName(search);

        for(AppItem app : appList){
            apps.add(app);
        }

        adapter.notifyDataSetChanged();
    }

    private void setupAdapter(){
        if(isAdded()){
            this.adapter = new AppAdapter(this.apps, onAppClickListener);
            appList.setAdapter(this.adapter);
        }
    }

    private OnItemClickListener onAppClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AppItem app) {
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
