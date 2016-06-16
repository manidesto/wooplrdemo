package com.manidesto.wooplrdemo.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Room implements Parcelable{
    private double latitude;
    private double longitude;
    private String name;
    private int price;
    private String image;

    public Room(double latitude, double longitude, String name, int price, String image) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    protected Room(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        name = in.readString();
        price = in.readInt();
        image = in.readString();
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return price;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(name);
        dest.writeInt(price);
        dest.writeString(image);
    }
}
