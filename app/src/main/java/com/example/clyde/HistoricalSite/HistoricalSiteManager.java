package com.example.clyde.HistoricalSite;

import android.content.Context;

import java.util.ArrayList;

public class HistoricalSiteManager {

    private static ArrayList<HistoricalSite> historicalSiteList = new ArrayList<>();

    public static ArrayList<HistoricalSite> getHistoricalSiteList(Context context) {
        return historicalSiteList;

    }
}


