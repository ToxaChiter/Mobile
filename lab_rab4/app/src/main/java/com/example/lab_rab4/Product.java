package com.example.lab_rab4;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private String name;
    private String price;
    private String weight;
    private boolean isSelected;

    public Product(String name, String price, String weight) {
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.isSelected = false;
    }

    protected Product(Parcel in) {
        name = in.readString();
        price = in.readString();
        weight = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getWeight() {
        return weight;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(price);
        dest.writeString(weight);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }
}
