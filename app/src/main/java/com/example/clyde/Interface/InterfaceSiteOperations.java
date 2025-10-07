package com.example.clyde.Interface;

import android.view.View;
import com.example.clyde.HistoricalSite.HistoricalSite;
import java.util.List;

public interface InterfaceSiteOperations {

    void onItemSelected(int selectedItemIndex, View v);
    List<HistoricalSite> onGetAllHistoricalSites();
}