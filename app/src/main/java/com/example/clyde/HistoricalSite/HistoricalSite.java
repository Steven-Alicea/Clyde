package com.example.clyde.HistoricalSite;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoricalSite implements Parcelable {

    private long id;
    private double longitude, latitude;
    private int numberOfVisits;
    private String name;

    public HistoricalSite(long id, String name, double latitude, double longitude, int numberOfVisits) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numberOfVisits = numberOfVisits;
    }

    protected HistoricalSite(Parcel in) {
        id = in.readLong();
        longitude = in.readDouble();
        latitude = in.readDouble();
        numberOfVisits = in.readInt();
        name = in.readString();
    }

    public static final Creator<HistoricalSite> CREATOR = new Creator<HistoricalSite>() {
        @Override
        public HistoricalSite createFromParcel(Parcel in) {
            return new HistoricalSite(in);
        }

        @Override
        public HistoricalSite[] newArray(int size) {
            return new HistoricalSite[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getNumberOfVisits() { return numberOfVisits; }

    public void setNumberOfVisits(int numberOfVisits) { this.numberOfVisits = numberOfVisits; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeDouble(longitude);
        parcel.writeDouble(latitude);
        parcel.writeInt(numberOfVisits);
        parcel.writeString(name);
    }
}
