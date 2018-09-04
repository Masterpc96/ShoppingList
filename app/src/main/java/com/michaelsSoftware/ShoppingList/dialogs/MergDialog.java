package com.michaelsSoftware.ShoppingList.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;

import com.michaelsSoftware.ShoppingList.R;


import butterknife.BindView;
import butterknife.ButterKnife;

public class MergDialog extends Dialog {
    @BindView(R.id.yesButton)
    Button yes;

    @BindView(R.id.noButton)
    Button no;

    public MergDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
        setContentView(R.layout.dialog_merge);
        ButterKnife.bind(this);

    }

    public void setListener(View.OnClickListener listener) {
        yes.setOnClickListener(listener);
        no.setOnClickListener(listener);
    }
}
