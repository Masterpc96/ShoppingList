package com.michaelsSoftware.ShoppingList.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.michaelsSoftware.ShoppingList.R;
import com.michaelsSoftware.ShoppingList.activities.CurrentItem;
import com.michaelsSoftware.ShoppingList.dataBase.DataBase;
import com.michaelsSoftware.ShoppingList.dialogs.EditProduct;
import com.michaelsSoftware.ShoppingList.own_classes.Product;


import java.util.ArrayList;

public class ProductRow extends RecyclerView.Adapter<ProductRow.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    private ArrayList<Product> toBuy;
    private Product current;
    private Context context;
    private RecyclerView mRecyclerView;
    private ArrayList<Product> bought;
    private CurrentItem parent;
    private MenuItem deleteItem;

    public ProductRow(ArrayList<Product> toBuy, RecyclerView mRecyclerView, ArrayList<Product> bought, CurrentItem parent, MenuItem deleteItem) {
        this.toBuy = toBuy;
        this.mRecyclerView = mRecyclerView;
        this.bought = bought;
        this.parent = parent;
        this.deleteItem = deleteItem;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_product, parent, false);
        context = view.getContext();
        view.setOnLongClickListener(this);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // if position is smaller than to buy size then current is item of to buy array list
        if (position < toBuy.size()) {
            current = toBuy.get(position);
        } else {

            // else current is item of bought array list
            current = bought.get(position - toBuy.size());
        }


        holder.txtName.setText(current.getProductName());

        if (current.getQuantity() != 0) {
            holder.txtAmount.setVisibility(View.VISIBLE);
            holder.txtAmount.setText(String.format("%.2f %s",current.getQuantity(), current.getUnit()));
        }
        else{
            holder.txtAmount.setVisibility(View.GONE);
        }
        if (current.getValue() != 0) {
            holder.txtPrice.setVisibility(View.VISIBLE);
            holder.txtPrice.setText(String.format("  %.2f %s", current.getValue(), context.getResources().getString(R.string.money)));
        }else{
            holder.txtPrice.setVisibility(View.GONE);
        }


        holder.checkBox.setChecked(current.isBought());

        if (current.isBought()) {
            holder.txtName.setPaintFlags(holder.txtName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else
            holder.txtName.setPaintFlags(holder.txtName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public int getItemCount() {
        return toBuy.size() + bought.size();
    }

    @Override
    public void onClick(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        if (position < toBuy.size()) {
            current = toBuy.get(position);
        } else {
            current = bought.get(position - toBuy.size());
        }

        if (current.isBought()) {
            current.setBought(false);

            DataBase.getInstance(context).getDao().updateProductQuery(
                    current.getId(), current.getProductName(), current.getQuantity(), current.getValue(),
                    current.getUnit(), false);

            bought.remove(current);
            toBuy.add(current);
            if(bought.size() == 0) deleteItem.setVisible(false);
        } else {
            current.setBought(true);


            DataBase.getInstance(context).getDao().updateProductQuery(
                    current.getId(), current.getProductName(), current.getQuantity(), current.getValue(),
                    current.getUnit(), true);

            bought.add(current);
            toBuy.remove(current);
            deleteItem.setVisible(true);
        }
        notifyDataSetChanged();
    }

    //     editing product
    @Override
    public boolean onLongClick(View v) {
        int position = mRecyclerView.getChildAdapterPosition(v);
        try {
            EditProduct edit = new EditProduct(context, toBuy.get(position), position);
            edit.setObserver(parent);
            edit.show();
        } catch (IndexOutOfBoundsException e) {
            Toast.makeText(context, R.string.alreadyBought, Toast.LENGTH_LONG).show();
        }
        return true;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtName;
        private TextView txtAmount;
        private TextView txtPrice;
        private CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.productName);
            txtAmount = itemView.findViewById(R.id.productAmount);
            txtPrice = itemView.findViewById(R.id.productPrice);
            checkBox = itemView.findViewById(R.id.productCheckBox);
        }
    }
}
