package com.michaelsSoftware.ShoppingList.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.michaelsSoftware.ShoppingList.Interfaces.EditProductListener;
import com.michaelsSoftware.ShoppingList.Interfaces.EnterProductListener;
import com.michaelsSoftware.ShoppingList.R;
import com.michaelsSoftware.ShoppingList.adapters.ProductRow;
import com.michaelsSoftware.ShoppingList.dataBase.DataBase;
import com.michaelsSoftware.ShoppingList.dialogs.AddProduct;
import com.michaelsSoftware.ShoppingList.own_classes.Product;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrentItem extends AppCompatActivity implements EnterProductListener, EditProductListener, View.OnClickListener, Toolbar.OnMenuItemClickListener {

    // Image button to add new product
    @BindView(R.id.imageButton)
    ImageButton addB;

    // recycler view with products
    @BindView(R.id.products)
    RecyclerView listViewP;

    // no products text
    @BindView(R.id.noProductText)
    TextView noProducts;

    // Text view in toolbar which represent list name
    @BindView(R.id.toolbar_title2)
    TextView title;

    // toolbar
    @BindView(R.id.toolbar2)
    Toolbar toolbar;

    // application context
    private final Context context = this;

    // list with product with will be bought
    private ArrayList<Product> toBuy;

    // list with bought products
    private ArrayList<Product> bought;

    // list title
    private String listName;

    // adapter for recycler view
    private ProductRow rowAdapter;

    // list position from previous activity
    private int position;

    private MenuItem deleteItem;


    /***********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_selected_list);
        ButterKnife.bind(this);

        // set back arrow on toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        // set menu on toolbar
        toolbar.inflateMenu(R.menu.current_item_menu);


        deleteItem = toolbar.getMenu().findItem(R.id.action_delete_1);

        // set action when user click on menu item
        toolbar.setOnMenuItemClickListener(this);

        // action for arrow back, come back to main activity and return data
        toolbar.setNavigationOnClickListener(this);

        // add button- show dialog
        addB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProduct dial = new AddProduct(context);
                dial.setObserver(CurrentItem.this);
                dial.show();
            }
        });


        // read to buy list
        toBuy = getIntent().getParcelableArrayListExtra("TO_BUY");

        // read bought list
        bought = getIntent().getParcelableArrayListExtra("BOUGHT");

        if (bought.size() != 0) deleteItem.setVisible(true);

        // read list name
        listName = getIntent().getStringExtra("LIST_NAME");

        // read list position
        position = getIntent().getIntExtra("POSITION", 0);


        // set fixed size on recycler view
        listViewP.setHasFixedSize(true);

        // create layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        // set layout manager
        listViewP.setLayoutManager(mLayoutManager);

        // set item animator
        listViewP.setItemAnimator(new DefaultItemAnimator());

        // create row adapter
        rowAdapter = new ProductRow(toBuy, listViewP, bought, CurrentItem.this, deleteItem);

        // set row adapter on recycler view
        listViewP.setAdapter(rowAdapter);

        // check status
        checkStatus();

        // set toolbar title
        title.setText(listName);

    }

    /***********************************************************************************************/


    // action to come back to main activity when arrow back was clicked
    @Override
    public void onBackPressed() {

        // create intent instance
        Intent data = new Intent();

        // put to buy list
        data.putParcelableArrayListExtra("TO_BUY", toBuy);

        // put bought list
        data.putParcelableArrayListExtra("BOUGHT", bought);

        // put position of list
        data.putExtra("POSITION", position);

        // set result, result code as success and intent instance
        setResult(0, data);

        // call parent constructor
        super.onBackPressed();
    }

    // listener for arrow back
    @Override
    public void onClick(View v) {
        // create intent instance
        Intent data = new Intent();

        // put to buy list
        data.putParcelableArrayListExtra("TO_BUY", toBuy);

        // put bought list
        data.putParcelableArrayListExtra("BOUGHT", bought);

        // put position of list
        data.putExtra("POSITION", position);

        // set result, result code as success and intent instance
        setResult(0, data);

        // finish this activity
        finish();
    }

    // delete item action on toolbar
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (!bought.isEmpty()) {
            for (Product p : bought) {
                toBuy.remove(p);
                DataBase.getInstance(context).getDao().deleteById(p.getId());
            }

            // notify that data set has been changed
            rowAdapter.notifyDataSetChanged();
            // clear bought list
            bought.clear();

            // check if list is empty
            checkStatus();
        }
        return true;
    }

    /***********************************************************************************************/

    // check if list is empty, if true show text else hide text
    private void checkStatus() {
        if (toBuy.isEmpty() && bought.isEmpty()) {
            noProducts.setVisibility(View.VISIBLE);
        } else {
            noProducts.setVisibility(View.GONE);
        }
    }

    // lister for add product
    @Override
    public void addProduct(Product product) {

        // add product
        toBuy.add(product);

        // notify adapter
        rowAdapter.notifyDataSetChanged();

        // check status
        checkStatus();

        // add in room database
        DataBase.getInstance(context).getDao().insertProduct(new com.michaelsSoftware.ShoppingList.entity.Product(product.getId(),
                listName, product.getProductName(), product.getQuantity(), product.getValue(), product.getUnit(), false));
    }


    // editing product listener
    @Override
    public void updateProduct(Product product, int position) {
        // swap product
        toBuy.set(position, product);

        // notify adapter
        rowAdapter.notifyDataSetChanged();

        // check status
        checkStatus();

        // update room database
        DataBase.getInstance(context).getDao().updateProductChangedQuery(
                product.getId(), product.getProductName(), product.getQuantity(), product.getValue(), product.getUnit());
    }

}
