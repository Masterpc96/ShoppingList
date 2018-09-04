package com.michaelsSoftware.ShoppingList.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.michaelsSoftware.ShoppingList.R;
import com.michaelsSoftware.ShoppingList.own_classes.ProductList;

import java.util.ArrayList;
import java.util.List;


public class ListRow extends ArrayAdapter<ProductList> {
    private Context context;
    private int resource;
    private ArrayList<ProductList> data = null;
    private RowBeanHolder holder = null;
    private boolean longPressed = false;

    // row_lists adapter - product list

    public ListRow(@NonNull Context context, @LayoutRes int resource, @NonNull List<ProductList> data) {
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = (ArrayList<ProductList>) data;
    }

    /***********************************************************************************************/

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View row = convertView;


        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new RowBeanHolder();
            holder.txtTitle = (TextView) row.findViewById(R.id.name);
            holder.indicator = (TextView) row.findViewById(R.id.indicator);
            holder.checkBox = (CheckBox) row.findViewById(R.id.checkBoxDelete);
            row.setTag(holder);
            holder.checkBox.setTag(position);
        } else {
            holder = (RowBeanHolder) row.getTag();
            ((RowBeanHolder) row.getTag()).checkBox.setTag(position);

        }

        if (longPressed) {
            showCheckbox();
        } else {
            hideCheckbox();
            unchecked();
        }

        ProductList object = data.get(position);
        holder.txtTitle.setText(object.getName());
        holder.indicator.setText(object.getBoughtSize() + "/" + object.getToBuySize());
        holder.checkBox.setChecked(data.get(position).isSelected());

        return row;
    }

    /***********************************************************************************************/

    static class RowBeanHolder {
        TextView txtTitle;
        TextView indicator;
        CheckBox checkBox;
    }

    /***********************************************************************************************/

    public void showCheckbox() {
        holder.checkBox.setVisibility(View.VISIBLE);
        longPressed = true;
        notifyDataSetChanged();
    }

    /***********************************************************************************************/

    public void hideCheckbox() {
        holder.checkBox.setVisibility(View.GONE);
        longPressed = false;
        notifyDataSetChanged();
    }

    /***********************************************************************************************/

    public void unchecked() {
        holder.checkBox.setChecked(false);
        notifyDataSetChanged();
    }

    /***********************************************************************************************/

    public ArrayList<ProductList> getData() {
        return data;
    }
}