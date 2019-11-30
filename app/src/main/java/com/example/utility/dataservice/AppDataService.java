package com.example.utility.dataservice;

import android.content.Context;

import com.example.utility.R;
import com.example.utility.apps.CurrencyExchangeAppActivity;
import com.example.utility.models.AppItem;

import java.util.ArrayList;
import java.util.List;

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
                CurrencyExchangeAppActivity.class));

        return appList;
    }
}
