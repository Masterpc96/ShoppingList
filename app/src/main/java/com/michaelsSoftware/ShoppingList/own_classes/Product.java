package com.michaelsSoftware.ShoppingList.own_classes;

import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Michał on 24.04.2017.
 */

public class Product implements Parcelable, Serializable {
    private int id = 1;
    private String productName;
    private double quantity;
    private double value;
    private String unit;
    private Boolean bought = false;

    private Product(Parcel in) {
        this.id = in.readInt();
        this.productName = in.readString();
        this.quantity = in.readDouble();
        this.value = in.readDouble();
        this.unit = in.readString();
        this.bought = in.readInt() == 1 ? true : false;
    }

    /***********************************************************************************************/


    @Ignore
    public Product(int id, String productName, double quantity, double value, String unit) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.value = value;
        this.unit = unit;
    }

    public Product(int id, String productName, double quantity, double value, String unit, Boolean bought) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.value = value;
        this.unit = unit;
        this.bought = bought;
    }

    /***********************************************************************************************/

    @Override
    public int describeContents() {
        return 0;
    }

    /***********************************************************************************************/

    /**
     * saving data to parcel
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(productName);
        dest.writeDouble(quantity);
        dest.writeDouble(value);
        dest.writeString(unit);
        dest.writeInt(bought ? 1 : 0);
    }

    /***********************************************************************************************/
    /**
     * restore data from parcel
     */

    public static final Parcelable.Creator<Product> CREATOR
            = new Parcelable.Creator<Product>() {
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    /**
     * Name
     * -- GETTER --
     * get product name
     * <p>
     * Quantity
     * -- GETTER --
     * get product quantity
     * <p>
     * Value
     * -- GETTER --
     * get product value
     * <p>
     * Unit
     * -- GETTER --
     * get quantity unit
     * <p>
     * Selected
     * -- GETTER --
     * get true when product was bought otherwise return false
     * <p>
     * -- SETTER --
     * set true when product was bought otherwise set fals e
     */

    public String getProductName() {
        return productName;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }

    public int getId() {
        return id;
    }

    public Boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Product) {
            Product p = (Product) obj;
            if (p.getId() == id && p.getProductName().equals(productName) && p.getUnit().equals(unit) && p.getQuantity() == quantity && p.getValue() == value && p.bought == bought) {
                return true;
            }
        }

        return false;
    }
}
