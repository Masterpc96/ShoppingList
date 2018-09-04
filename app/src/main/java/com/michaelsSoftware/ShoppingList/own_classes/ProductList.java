package com.michaelsSoftware.ShoppingList.own_classes;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Michał on 24.04.2017.
 */

public class ProductList implements Serializable {
    private String name;
    private ArrayList<Product> toBuy;
    private ArrayList<Product> bought;
    private boolean selected = false;

    /**
     * product list class
     *
     * @param name - list name
     */


    public ProductList(String name) {
        this.name = name;
        toBuy = new ArrayList<>();
        bought = new ArrayList<>();
    }

    /**
     * GETTERS
     */

    public String getName() {
        return name;
    }


    public int getToBuySize() {
        return toBuy.size();
    }


    public ArrayList<Product> getToBuy() {
        return toBuy;
    }

    public ArrayList<Product> getBought() {
        return bought;
    }

    public int getBoughtSize() {
        return bought.size();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }


    public void setToBuy(ArrayList<Product> toBuy) {
        this.toBuy = toBuy;
    }

    public void setBought(ArrayList<Product> bought) {
        this.bought = bought;
    }

    @Override
    public boolean equals(Object obj) {
        boolean exist = this.name.equals(((ProductList) obj).name);
        return exist;
    }
}
