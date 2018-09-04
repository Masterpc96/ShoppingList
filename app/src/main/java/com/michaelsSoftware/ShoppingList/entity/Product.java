package com.michaelsSoftware.ShoppingList.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {

    public Product(int id, String listName, String productName, double quantity, double value, String unit, boolean bought) {
        this.id = id;
        this.listName = listName;
        this.productName = productName;
        this.quantity = quantity;
        this.value = value;
        this.unit = unit;
        this.bought = bought;
    }

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "list_name")
    public String listName;

    public String productName;

    public double quantity;

    public double value;

    public String unit;

    public boolean bought;

}
