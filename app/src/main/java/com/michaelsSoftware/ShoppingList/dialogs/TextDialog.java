package com.michaelsSoftware.ShoppingList.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.michaelsSoftware.ShoppingList.R;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextDialog extends android.app.Dialog {

    @BindView(R.id.welcome)
    TextView textView;

    @BindView(R.id.enter)
    Button button;


    public TextDialog(@NonNull Context context, @NotNull String text) {
        super(context, R.style.DialogTheme);
        setContentView(R.layout.dialog_with_text);

        setCanceledOnTouchOutside(false);
        ButterKnife.bind(this);

        textView.setText(text);

        button.setText("OK");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
}
