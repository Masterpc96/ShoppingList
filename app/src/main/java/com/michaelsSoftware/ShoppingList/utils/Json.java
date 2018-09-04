package com.michaelsSoftware.ShoppingList.utils;

import com.michaelsSoftware.ShoppingList.activities.MainActivity;
import com.michaelsSoftware.ShoppingList.own_classes.Product;
import com.michaelsSoftware.ShoppingList.own_classes.ProductList;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



/* json structure

[
  [
    {
      "listName": "l1"
    },
    [
      {
        "productName": "p1",
        "quantity": 0,
        "value": 0,
        "unit": "Gram"
        "bought": 0
      },
      {
        "productName": "p3",
        "quantity": 0,
        "value": 0,
        "unit": "Gram"
        "bought": 0
      }
    ],
    [
      {
        "productName": "p2",
        "quantity": 0,
        "value": 0,
        "unit": "Gram"
        "bought": 0
      },
      {
        "productName": "p4",
        "quantity": 0,
        "value": 0,
        "unit": "Gram"
        "bought": 0
      }
    ]
  ],
  next lists
]
        */


public class Json {

    private static Json coverter = new Json();

    public static Json getCoverter() {
        return coverter;
    }

    private Json() {
    }

    // convert data into json
    public JSONArray convertIntoJson(@NotNull ArrayList<ProductList> toShare) throws JSONException {

        // json array of list to be shared
        JSONArray listToShare = new JSONArray();

        for (int i = 0; i < toShare.size(); i++) {
            // temporary current list
            ProductList temp = toShare.get(i);

            // array list of products to buy
            ArrayList<Product> tobuy = temp.getToBuy();

            // array list of bought products
            ArrayList<Product> bought1 = temp.getBought();

            // json array of products that will be bought
            JSONArray toBuy = convertIntoJsonArray(tobuy);

            // json array of products that is bought
            JSONArray bought = convertIntoJsonArray(bought1);

            // put this to list to main json array
            listToShare.put(new JSONArray()
                    .put(new JSONObject().put("listName", temp.getName()))
                    .put(toBuy)
                    .put(bought));
        }

        // return json
        return listToShare;
    }

    // read data from json
    public ArrayList<ProductList> convertFromJson(@NotNull String json) throws JSONException {

        // get all json array from file
        JSONArray reader = new JSONArray(json);

        // create new Product List array
        ArrayList<ProductList> toInsert = new ArrayList<>();

        for (int i = 0; i < reader.length(); i++) {

            // get one of
            JSONArray array = reader.getJSONArray(i);

            // get list name
            JSONObject o = array.getJSONObject(0);

            // get to buy list
            JSONArray t = array.getJSONArray(1);

            // get bought list
            JSONArray b = array.getJSONArray(2);

            String listName = o.getString("listName");

            ArrayList<Product> toBuy = restoreProduct(t);

            ArrayList<Product> bought = restoreProduct(b);

            // create new list
            ProductList temp = new ProductList(listName);

            // set to buy
            temp.setToBuy(toBuy);

            // set bought
            temp.setBought(bought);

            // add one restored list to all list array
            toInsert.add(temp);
        }

        return toInsert;
    }


    private ArrayList<Product> restoreProduct(JSONArray array) throws JSONException {

        ArrayList<Product> temporaryList = new ArrayList<>();
        ArrayList<com.michaelsSoftware.ShoppingList.entity.Product> toInsetDB = new ArrayList<>();

        for (int y = 0; y < array.length(); y++) {

            // get one product
            JSONObject temp = array.getJSONObject(y);

            // get name
            String productName = temp.getString("productName");

            // get quantity
            Double quantity = Double.parseDouble(temp.getString("quantity"));

            // get value
            Double value = Double.parseDouble(temp.getString("value"));

            // get unit
            String unit = temp.getString("unit");

            // get bought
            boolean bought = temp.getBoolean("bought");

            // insert to array
            temporaryList.add(new Product(MainActivity.id, productName, quantity, value, unit, bought));
            MainActivity.id++;
        }
        return temporaryList;
    }

    private JSONArray convertIntoJsonArray(ArrayList<Product> products) throws JSONException {
        JSONArray temp = new JSONArray();

        for (int j = 0; j < products.size(); j++) {
            temp.put(new JSONObject()
                    .put("productName", products.get(j).getProductName())
                    .put("quantity", products.get(j).getQuantity())
                    .put("value", products.get(j).getValue())
                    .put("unit", products.get(j).getUnit())
                    .put("bought", products.get(j).isBought()));
        }
        return temp;
    }
}
