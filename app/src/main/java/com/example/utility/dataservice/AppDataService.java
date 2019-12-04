package com.example.utility.dataservice;

import android.content.Context;

import com.example.utility.R;
import com.example.utility.apps.CoinFlipFragment;
import com.example.utility.apps.CurrencyExchangeAppFragment;
import com.example.utility.apps.StopWatchAppFragment;
import com.example.utility.models.AppItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AppDataService {
    private static AppDataService appDataService;

    private Context context;
    private List<AppItem> apps;

    public List<AppItem> getApps() {
        return apps;
    }

    public static AppDataService get(Context _context){
        if(appDataService == null){
            appDataService = new AppDataService(_context);
        }

        return appDataService;
    }

    private AppDataService(Context _context){
        this.context = _context.getApplicationContext();
        this.apps = ReturnAllApps();
    }

    private List<AppItem> ReturnAllApps(){
        List<AppItem> appList = new ArrayList<>();
        appList.add(new AppItem(context.getResources().getString(R.string.app_currency_exchange),
                R.mipmap.ic_currency_exchange,
                new CurrencyExchangeAppFragment()));

        appList.add(new AppItem(context.getResources().getString(R.string.app_stopwatch),
                R.mipmap.ic_stopwatch,
                new StopWatchAppFragment()));

        appList.add(new AppItem(context.getResources().getString(R.string.app_coin_flip),
                R.mipmap.ic_flip_coin,
                new CoinFlipFragment()));

        return appList;
    }

    public AppItem getAppById(UUID id){
        AppItem appItem = null;
        for(AppItem app : this.apps){
            if(app.getId().equals(id)){
                appItem = app;
            }
        }

        return appItem;
    }

    public List<AppItem> ReturnAppsByName(String name){
        List<AppItem> appList = new ArrayList<>();

        if (name.equals(null) || name.isEmpty()){
            return apps;
        }

        Integer size = name.length();

        for(AppItem app : apps){
            String appName = app.getName();
            Integer substringEndIndex = (size < appName.length() ? size : appName.length());
            if(appName.substring(0, substringEndIndex).toLowerCase().equals(name.toLowerCase())){
                appList.add(app);
            }
        }

        return appList;
    }

}
