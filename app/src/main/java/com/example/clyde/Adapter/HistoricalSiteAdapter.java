package com.example.clyde.Adapter;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.clyde.HistoricalSite.HistoricalSite;
import com.example.clyde.Interface.InterfaceSiteOperations;
import com.example.clyde.R;

import java.util.List;

public class HistoricalSiteAdapter extends RecyclerView.Adapter<HistoricalSiteAdapter.ViewHolder> {

    private Context context;

    public HistoricalSiteAdapter(Context context) {
        if (context instanceof InterfaceSiteOperations) {
            this.context = context;
            return;
        }
        throw new ClassCastException("Expected context to be instanceof InterfaceSiteOperations");
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView siteName, coordinates, numberOfVisits;
        private CardView historicalSite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bindControls();
            registerHandlers();
        }

        private void bindControls() {
            siteName = itemView.findViewById(R.id.siteName);
            coordinates = itemView.findViewById(R.id.siteCoordinates);
            numberOfVisits = itemView.findViewById(R.id.siteVisits);
            historicalSite = itemView.findViewById(R.id.site);
            historicalSite.setCardBackgroundColor(Color.TRANSPARENT);
            historicalSite.setActivated(false);
        }

        private void registerHandlers() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int selectedItemIndex = (int) view.getTag();
                    view = historicalSite;
                    ((InterfaceSiteOperations) context).onItemSelected(selectedItemIndex, view);
                }
            });
        }

        private void displayData(HistoricalSite historicalSite) {
            siteName.setText(historicalSite.getName());
            coordinates.setText("(" + historicalSite.getLatitude() + ", " + historicalSite.getLongitude() + ")");
            numberOfVisits.setText(String.valueOf(historicalSite.getNumberOfVisits()) + " Visits");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new HistoricalSiteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<HistoricalSite> historicalSiteList = ((InterfaceSiteOperations) context).onGetAllHistoricalSites();
        HistoricalSite historicalSite = historicalSiteList.get(position);
        holder.displayData(historicalSite);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return ((InterfaceSiteOperations) context).onGetAllHistoricalSites().size();
    }
}