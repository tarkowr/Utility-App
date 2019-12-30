package com.rt.utility.dataservice;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.rt.utility.R;
import com.rt.utility.apps.CoinFlipFragment;
import com.rt.utility.apps.CurrencyExchangeAppFragment;
import com.rt.utility.apps.StopWatchAppFragment;
import com.rt.utility.apps.TaskManagerFragment;
import com.rt.utility.apps.WifiScannerFragment;
import com.rt.utility.models.AppItem;

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

    /*
    Ensures only one instance of this class exists during the app's lifecycle
    Referenced Android Programming by The Big Nerd Ranch Guide
     */
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

    /*
    Initialize all apps in the Utility app
     */
    private List<AppItem> ReturnAllApps(){
        List<AppItem> appList = new ArrayList<>();
        appList.add(new AppItem(context.getResources().getString(R.string.app_currency_exchange),
                R.mipmap.ic_currency_exchange,
                0,
                new CurrencyExchangeAppFragment()));

        appList.add(new AppItem(context.getResources().getString(R.string.app_stopwatch),
                R.mipmap.ic_stopwatch,
                0,
                new StopWatchAppFragment()));

        appList.add(new AppItem(context.getResources().getString(R.string.app_coin_flip),
                R.mipmap.ic_flip_coin,
                0,
                new CoinFlipFragment()));

        appList.add(new AppItem(context.getResources().getString(R.string.app_task_manager),
                R.mipmap.ic_task_manager_icon,
                0,
                new TaskManagerFragment()));

        appList.add(new AppItem(context.getResources().getString(R.string.app_wifi_scanner),
                R.mipmap.ic_wifi,
                27,
                new WifiScannerFragment()));

        appList = RemoveIncompatibleApps(appList);

        return appList;
    }

    /*
    Return an app by its ID
     */
    public AppItem getAppById(UUID id){
        AppItem appItem = null;
        for(AppItem app : this.apps){
            if(app.getId().equals(id)){
                appItem = app;
            }
        }

        return appItem;
    }

    /*
    Return apps with a name (or part of the name) that matches the query name
     */
    public List<AppItem> ReturnAppsByName(String name){
        List<AppItem> appList = new ArrayList<>();

        if (name == null || name.isEmpty()){
            return apps;
        }

        int size = name.length();

        for(AppItem app : apps){
            String appName = app.getName();
            int substringEndIndex = (size < appName.length() ? size : appName.length());
            if(appName.substring(0, substringEndIndex).toLowerCase().equals(name.toLowerCase())){
                appList.add(app);
            }
        }

        return appList;
    }

    /*
    Removes apps from the app list with an incompatible sdk version
     */
    private List<AppItem> RemoveIncompatibleApps(List<AppItem> appList){
        int deviceVersion = Build.VERSION.SDK_INT;

        for(AppItem app : appList){
            if(app.getMinSdk() > deviceVersion){
                appList.remove(app);
            }
        }

        return appList;
    }
}
