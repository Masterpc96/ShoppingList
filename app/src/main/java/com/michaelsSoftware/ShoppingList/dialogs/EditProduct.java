package com.michaelsSoftware.ShoppingList.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.michaelsSoftware.ShoppingList.Interfaces.EditProductListener;
import com.michaelsSoftware.ShoppingList.R;
import com.michaelsSoftware.ShoppingList.own_classes.Product;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProduct extends Dialog {
    // spinner for select unit
    @BindView(R.id.unit)
    Spinner unit;

    // button add
    @BindView(R.id.addB)
    Button add;

    // product name
    @BindView(R.id.nameP)
    EditText productName;

    // product quantity
    @BindView(R.id.amount)
    EditText amount;

    // product price
    @BindView(R.id.price)
    EditText price;

    // price value per
    @BindView(R.id.valuePer)
    TextView valuePer;

    ArrayAdapter<CharSequence> adapter;

    private EditProductListener observer;

    // editing product
    public EditProduct(@NonNull final Context context, @NotNull final Product product, final int position) {
        super(context, R.style.DialogTheme);

        setContentView(R.layout.dialog_add_product);

        ButterKnife.bind(this);

        // create instance of variables

        // create adapter
        adapter = ArrayAdapter.createFromResource(context, R.array.Unit, R.layout.support_simple_spinner_dropdown_item);

        // set drop down view for spinner in adapter
        adapter.setDropDownViewResource(R.layout.unit_spinner);

        // set adapter on spinner
        unit.setAdapter(adapter);

        // setting value of variables
        add.setText(getContext().getString(R.string.edit));
        productName.setText(product.getProductName());
        amount.setText(String.valueOf(product.getQuantity()));
        price.setText(String.valueOf(product.getValue()));

        // get position of current unit
        final int pos = adapter.getPosition(product.getUnit());


        unit.setSelection(pos);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int uidd = product.getId();
                Product p;
                if (!productName.getText().toString().equals("")) {
                    if (!amount.getText().toString().equals("")) {
                        if (!price.getText().toString().equals("")) {
                            p = new Product(uidd, productName.getText().toString(), Double.parseDouble(amount.getText().toString()), Double.parseDouble(price.getText().toString()), unit.getSelectedItem().toString());
                        } else {
                            p = new Product(uidd, productName.getText().toString(), Double.parseDouble(amount.getText().toString()), 0.00, unit.getSelectedItem().toString());
                        }
                    } else if (!price.getText().toString().equals("")) {
                        p = new Product(uidd, productName.getText().toString(), 0, Double.parseDouble(price.getText().toString()), unit.getSelectedItem().toString());
                    } else {
                        p = new Product(uidd, productName.getText().toString(), 0, 0.00, unit.getSelectedItem().toString());
                    }

                    observer.updateProduct(p, position);

                    dismiss();
                }
            }
        });
    }

    public void setObserver(EditProductListener observer) {
        this.observer = observer;
    }

}
