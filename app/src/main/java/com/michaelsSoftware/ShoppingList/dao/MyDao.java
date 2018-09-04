package com.michaelsSoftware.ShoppingList.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.michaelsSoftware.ShoppingList.entity.Product;



@Dao
public interface MyDao {

    /**
     * deletes
     */
    @Query("DELETE FROM PRODUCTS where productName= :productName and quantity = :quantity and value = :value and unit = :unit and bought = :bought")
    public void deleteProductQuery(String productName, double quantity, double value, String unit, boolean bought);

    @Query("DELETE FROM PRODUCTS where list_name = :listName")
    public void deleteListQuery(String listName);

    @Query("DELETE FROM PRODUCTS where id= :id")
    public void deleteById(int id);

    /**
     * inserts
     */
    @Insert()
    public void insertProduct(Product product);


    /**
     * get counts
     */
    // get bought count from given list name
    @Query("SELECT COUNT(*) FROM PRODUCTS WHERE bought = 1 AND list_name = :listName")
    public int getBoughtCount(String listName);

    @Query("SELECT COUNT(*) FROM PRODUCTS")
    public int getListCount();

    // get to buy count from given list name
    @Query("SELECT COUNT(*) FROM PRODUCTS WHERE bought = 0 AND list_name = :listName")
    public int getToBuyCount(String listName);


    /**
     * get all
     */
    // get all products from given list name
    @Query("SELECT * FROM PRODUCTS WHERE list_name = :listName")
    public Product[] getProducts(String listName);

    // get all to buy elements from given list name
    @Query("SELECT id, productName, quantity,value,unit, bought FROM PRODUCTS WHERE bought = 0 AND list_name = :listName")
    public com.michaelsSoftware.ShoppingList.own_classes.Product[] getToBuy(String listName);

    @Query("SELECT productName FROM PRODUCTS WHERE bought = 0 AND list_name = :listName")
    public String[] getToBuy1(String listName);

    @Query("SELECT id, productName, quantity,value,unit, bought FROM PRODUCTS WHERE bought = 1 AND list_name = :listName")
    public com.michaelsSoftware.ShoppingList.own_classes.Product[] getBought(String listName);

    @Query("SELECT DISTINCT(list_name) FROM PRODUCTS")
    public String[] getListsName();

    /**
     * updates
     */
    @Query("UPDATE PRODUCTS SET productName= :productName, quantity = :quantity, value = :value, unit = :unit, bought = :bought  WHERE id = :position")
    public void updateProductQuery(int position, String productName, double quantity, double value, String unit, boolean bought);

    @Query("UPDATE PRODUCTS SET productName= :productName, quantity = :quantity, value = :value, unit = :unit  WHERE id = :position")
    public void updateProductChangedQuery(int position, String productName, double quantity, double value, String unit);
}