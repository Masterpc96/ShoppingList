package com.michaelsSoftware.ShoppingList.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.EditText;

import com.michaelsSoftware.ShoppingList.Interfaces.EnterListNameListener;
import com.michaelsSoftware.ShoppingList.R;
import com.michaelsSoftware.ShoppingList.dataBase.DataBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ListName extends Dialog {

    // application context
    private Context context;

    // observer
    private EnterListNameListener observer;

    // field with list name
    @BindView(R.id.listName)
    EditText editText;

    private static List<String> listsName = new ArrayList<>();

    // add button, when field isn't empty then notify observer and close dialog
    @OnClick(R.id.addListButton)
    public void addList() {
        if (editText.getText().toString().equals("")) {
            editText.setError(context.getString(R.string.emptyListName));
        } else if (listsName.contains(editText.getText().toString())) {
            TextDialog dialog = new TextDialog(context, context.getString(R.string.exists));
            dialog.show();
        } else {
            observer.nameEntered(editText.getText().toString());
            listsName.add(editText.getText().toString());
            dismiss();
        }
    }

    public ListName(@NonNull Context context) {
        super(context, R.style.DialogTheme);
        setContentView(R.layout.dialog_add_list);
        this.context = context;
        ButterKnife.bind(this);

        listsName.addAll(Arrays.asList(DataBase.getInstance(context).getDao().getListsName()));
    }

    // setter for observer
    public void setObserver(EnterListNameListener observer) {
        this.observer = observer;
    }
}
