package com.example.clyde.Algorithm;

import com.example.clyde.HistoricalSite.HistoricalSite;

import java.util.ArrayList;
import java.util.List;

public class ClydeAlgorithm {

    public static List<HistoricalSite> distanceClyde(List<HistoricalSite> historicalSites) {
        double lat1, lat2, lon1, lon2;
        double matrix[][] = new double[historicalSites.size()][historicalSites.size()];
        ArrayList<HistoricalSite> answer = new ArrayList<>();

        for (int i = 0; i < historicalSites.size(); i++) {
            for (int j = 0; j < historicalSites.size(); j++) {
                lat1 = historicalSites.get(i).getLatitude();
                lon1 = historicalSites.get(i).getLongitude();
                lat2 = historicalSites.get(j).getLatitude();
                lon2 = historicalSites.get(j).getLongitude();
                if (( lat1 == lat2) && (lon1 == lon2 )) {
                    matrix[i][j] = 0.0;
                }
                else  {
                    double theta = lon1 - lon2;
                    double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
                    dist = Math.acos(dist);
                    dist = Math.toDegrees(dist);
                    dist = dist * 60 * 1.1515;
                    matrix[i][j] = dist;
                }
            }
        }
        int i = 0;
        int site = 0;
        double previousShortestDistanceSelected = 0;
        double distance = Double.MAX_VALUE;

        for (int k = 0; k < historicalSites.size() - 1; k++) {
            for (int j = 0; j < historicalSites.size(); j++) {
                if ((matrix[i][j] != 0) && (matrix[i][j] < distance) && (matrix[i][j] != previousShortestDistanceSelected)) {
                    distance = matrix[i][j];
                    site = j;
                }
            }
            previousShortestDistanceSelected = matrix[i][site];
            i = site;
            answer.add(historicalSites.get(i));
            distance = Double.MAX_VALUE;
        }
        return answer;
    }
}
