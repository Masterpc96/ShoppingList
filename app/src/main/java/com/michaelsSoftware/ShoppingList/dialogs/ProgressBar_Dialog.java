package com.michaelsSoftware.ShoppingList.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.michaelsSoftware.ShoppingList.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProgressBar_Dialog extends Dialog {
    @BindView(R.id.Message)
    TextView _message;


    public ProgressBar_Dialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
        setContentView(R.layout.dialog_progress_bar);
        ButterKnife.bind(this);

        setCanceledOnTouchOutside(false);
    }

    public void setMessage(int message) {
        _message.setText(message);
    }
}
