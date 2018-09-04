package com.michaelsSoftware.ShoppingList.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.michaelsSoftware.ShoppingList.R;
import com.michaelsSoftware.ShoppingList.own_classes.Product;
import com.michaelsSoftware.ShoppingList.own_classes.ProductList;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SendList extends Dialog {

    // application context
    private Context context;

    // product list to be shared
    private ArrayList<ProductList> toShare;

    @BindString(R.string.money)
    String money;

    public SendList(@NonNull Context context, @NonNull ArrayList<ProductList> toShare) {
        super(context, R.style.DialogTheme);
        setContentView(R.layout.dialog_share_as);

        ButterKnife.bind(this);

        this.context = context;
        this.toShare = toShare;
    }

    // share as text
    @OnClick(R.id.asText)
    public void setShareAsText() {

        // convert shopping list to string
        String toShareString = createString(toShare);

        // create intent to open new sending activity (from os)
        Intent intentToSend = new Intent(Intent.ACTION_SEND);

        // set sending type
        intentToSend.setType("text/plain");

        // put string to intent
        intentToSend.putExtra(Intent.EXTRA_TEXT, toShareString);

        // start sending activity
        context.startActivity(Intent.createChooser(intentToSend, context.getString(R.string.sendTo)));

        // close this dialog
        dismiss();
    }

    // share as file upload to server
    @OnClick(R.id.asFile)
    public void setShareAsFile() {

        // if user is logged in then he can upload list to server
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {

            // show dialog to enter user email
            Dialog dial = new UserEmail(context, toShare);

            // show dialog
            dial.show();

            // close current dialog
            dismiss();
        } else {

            // if user is not logged in notify him
            Toast.makeText(context, R.string.firstLogin, Toast.LENGTH_SHORT).show();
        }

    }

    // create string from toShare list
    private String createString(ArrayList<ProductList> toSend) {

        // create string with list
        String toShare = "";

        // iterate array list to create string
        for (ProductList p : toSend) {
            toShare += p.getName() + "\n";
            for (Product product : p.getToBuy()) {
                if (product.getValue() != 0) {
                    if (product.getUnit().equals("Butelek")) {
                        toShare += product.getProductName() + " " + product.getQuantity() + " " + product.getUnit() + " " + String.format("%.2f", product.getValue()) + " " +
                                money + context.getString(R.string.unitButelka) + "\n";
                    }
                    toShare += product.getProductName() + " " + product.getQuantity() + " " + product.getUnit() + " " + String.format("%.2f", product.getValue()) + " " +
                            money + "/" + product.getUnit() + "\n";
                } else {
                    toShare += product.getProductName() + " " + product.getQuantity() + " " + product.getUnit() + "\n";
                }
            }
        }

        // return created string
        return toShare;
    }
}
