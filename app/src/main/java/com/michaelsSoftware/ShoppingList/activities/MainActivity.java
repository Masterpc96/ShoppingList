package com.michaelsSoftware.ShoppingList.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.michaelsSoftware.ShoppingList.Interfaces.EnterListNameListener;
import com.michaelsSoftware.ShoppingList.R;
import com.michaelsSoftware.ShoppingList.adapters.ListRow;
import com.michaelsSoftware.ShoppingList.dialogs.MergDialog;
import com.michaelsSoftware.ShoppingList.dialogs.ProgressBar_Dialog;
import com.michaelsSoftware.ShoppingList.utils.Email;
import com.michaelsSoftware.ShoppingList.utils.Json;
import com.michaelsSoftware.ShoppingList.dataBase.DataBase;
import com.michaelsSoftware.ShoppingList.dialogs.ListName;
import com.michaelsSoftware.ShoppingList.dialogs.SendList;
import com.michaelsSoftware.ShoppingList.dialogs.TextDialog;
import com.michaelsSoftware.ShoppingList.own_classes.Product;
import com.michaelsSoftware.ShoppingList.own_classes.ProductList;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements AbsListView.MultiChoiceModeListener, GoogleApiClient.OnConnectionFailedListener, EnterListNameListener{

    // app context
    private final Context context = this;

    // time interval for pressing back key
    private static final long TIME_INTERVAL = 2000;

    // current time of pressing
    private long mBackPress;

    // get data from file
    private static final int LOGIN = 52;

    // code for backing back from current list
    private int REQUEST_CODE_LIST = 1;

    // unique id for each row in product database
    public static int id;

    // firebase auth instance
    private FirebaseAuth mAuth;

    // array list with shopping lists
    private ArrayList<ProductList> items;

    // array list with lists that will be deleted
    private ArrayList<ProductList> toDelete;

    // row adapter for listView
    private ListRow adapter;

    // amount of selected list
    private int selectedAmount = 0;

    // fragment_login option in menu
    private MenuItem login;

    // logout option in menu
    private MenuItem logout;

    // select all option in menu
    private MenuItem selectAll = null;

    // add button in main view which open a dialog to add list
    @BindView(R.id.addButtonMainVIew)
    ImageButton addButton;

    // toolbar with label and menu
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    // listView with lists
    @BindView(R.id.list)
    ListView listView;

    // text which is show when user doesn't have lists
    @BindView(R.id.noItemsText)
    TextView noItemText;

    @OnClick(R.id.addButtonMainVIew)
    void showAddDialog() {
        Dialog dialog = new ListName(context);
        ((ListName) dialog).setObserver(this);
        dialog.show();
    }


    /***********************************************************************************************/


    //back press function
    @Override
    public void onBackPressed() {
        if (mBackPress + TIME_INTERVAL >= System.currentTimeMillis()) { // check if application should be closed
            moveTaskToBack(true);
        } else {
            Toast.makeText(context, getString(R.string.oneMore), Toast.LENGTH_SHORT).show(); // if not show toast to press one more time
        }
        mBackPress = System.currentTimeMillis(); // time of pushing back key is saved to variable mBackPress
    }


    /***********************************************************************************************/


    // Override on Stop method save information that app was opened
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences preferences = getSharedPreferences("com.example.michal.shoppinglist.PREFERENCES", MODE_PRIVATE); // open preferences
        SharedPreferences.Editor editor = preferences.edit(); // get editor of preferences
        editor.putBoolean("opened", true);
        editor.putInt("currentID", id); // put boolean value that application was opend minimum one times
        editor.apply(); // apply changes
    }

    // Override on Destroy method to sign out
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.signOut();
    }


    /***********************************************************************************************/


    // create app
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_main);

        // init butter knife
        ButterKnife.bind(this);

        // init firebase auth
        mAuth = FirebaseAuth.getInstance();

        // init lists
        items = new ArrayList<>();
        toDelete = new ArrayList<>();

        // convert toolbar into Action Bar
        setSupportActionBar(toolbar);

        // set multi choice mode on list view that enable choosing more than one row
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);

        // set listener for multi choice mode
        listView.setMultiChoiceModeListener(this);

        // initialize stetho for inspecting app
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());


        // reading from file whether it is first app start
        final SharedPreferences preferences = getSharedPreferences("com.example.michal.shoppinglist.PREFERENCES", MODE_PRIVATE);
        id = preferences.getInt("currentID", 1);

        if (!preferences.getBoolean("opened", false)) {
            // show welcoming dialog when app is first use
            Dialog dialog1 = new TextDialog(this, getString(R.string.description));
            dialog1.show();
        }


        // set on item click listener to open a new activity with clicked item
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // create intent from CurrentItem class
                Intent intent = new Intent(context, CurrentItem.class);

                // put position of clicked row
                intent.putExtra("POSITION", position);

                // put to buy list
                intent.putParcelableArrayListExtra("TO_BUY", items.get(position).getToBuy());

                // put bought list
                intent.putParcelableArrayListExtra("BOUGHT", items.get(position).getBought());

                // put list name
                intent.putExtra("LIST_NAME", items.get(position).getName());

                // start activity to get result
                startActivityForResult(intent, REQUEST_CODE_LIST);
            }

        });

        // read data from database

        // get list name from database
        String[] listName = DataBase.getInstance(context).getDao().getListsName();

        // create shopping list from database
        for (int i = 0; i < listName.length; i++) {
            // get list name from array
            ProductList temp = new ProductList(listName[i]);

            // get products that should be bought
            Product[] toBuy = DataBase.getInstance(context).getDao().getToBuy(listName[i]);

            // get products that is bought
            Product[] bought = DataBase.getInstance(context).getDao().getBought(listName[i]);

            // set to buy list
            temp.setToBuy(new ArrayList<>(Arrays.asList(toBuy)));

            // set bought list
            temp.setBought(new ArrayList<>(Arrays.asList(bought)));

            // add restored shopping list to list that contain all shopping list
            items.add(temp);
        }

        // init adapter
        adapter = new ListRow(context, R.layout.row_lists, items);

        // set adapter to list view
        listView.setAdapter(adapter);

        // check status- this method check if main list is empty or not
        // and set visibility of noItemText
        checkStatus();
        onPostCreate(savedInstanceState);

    }

    // return result of activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // back from shopping list to main activity
        if (requestCode == REQUEST_CODE_LIST) {

            // get position of list row which invoke to start CurrentItem activity
            int position = data.getIntExtra("POSITION", 0);

            // get to buy list
            ArrayList<Product> temp = data.getParcelableArrayListExtra("TO_BUY");

            // set to buy list on main shopping list
            items.get(position).setToBuy(temp);

            // get bought list
            temp = data.getParcelableArrayListExtra("BOUGHT");

            // set bought list on main shopping list
            items.get(position).setBought(temp);

            // notify adapter that data set is changed
            adapter.notifyDataSetChanged();

            // button sign in
        } else if (requestCode == LOGIN) {
            updateUI(mAuth.getCurrentUser());
        } else {
            Toast.makeText(context, R.string.errorWithLogin, Toast.LENGTH_SHORT).show();
        }
    }

    // create option menu in toolbar- normal mode
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.open_menu, menu);
        login = menu.findItem(R.id.loginI);
        logout = menu.findItem(R.id.logoutI);
        updateUI(mAuth.getCurrentUser());
        setTitle("");

        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().reload();
            if (!mAuth.getCurrentUser().isEmailVerified()) {
                Intent intent = new Intent(context, FragmentActivity.class);
                intent.putExtra("confirm", 1);
                startActivityForResult(intent, LOGIN);
            }
        }
        return true;
    }


    // listener for toolbar menu items- normal mode
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.open) {
            if (mAuth.getCurrentUser() != null) {

                // show progress dialog
                final ProgressBar_Dialog downloading = new ProgressBar_Dialog(context);
                downloading.setMessage(R.string.download);
                downloading.show();

                String userEmail = Email.convertEmail(mAuth.getCurrentUser().getEmail());

                // get storage instance
                FirebaseStorage storage = FirebaseStorage.getInstance();

                // get references to main folder (root)
                StorageReference root = storage.getReferenceFromUrl(context.getString(R.string.URL));
                StorageReference ref = root.child("Lists/" + userEmail);
                try {
                    final File tempFile = File.createTempFile(userEmail, "json");
                    ref.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            try {

                                // create new scanner
                                Scanner s = new Scanner(tempFile);

                                // get json string from file
                                String in = s.next();

                                // close file
                                s.close();

                                // array with items to insert
                                ArrayList<ProductList> toInsert = Json.getCoverter().convertFromJson(in);

                                // inserting data to database
                                for (final ProductList pL : toInsert) {
                                    ArrayList<Product> _toBuy = pL.getToBuy();
                                    ArrayList<Product> _bought = pL.getBought();

                                    for (Product p : _toBuy) {
                                        DataBase.getInstance(context).getDao().insertProduct(
                                                new com.michaelsSoftware.ShoppingList.entity.Product(p.getId(), pL.getName(), p.getProductName(),
                                                        p.getQuantity(), p.getValue(), p.getUnit(), p.isBought()));
                                    }

                                    for (Product p : _bought) {
                                        DataBase.getInstance(context).getDao().insertProduct(
                                                new com.michaelsSoftware.ShoppingList.entity.Product(p.getId(), pL.getName(), p.getProductName(),
                                                        p.getQuantity(), p.getValue(), p.getUnit(), p.isBought()));
                                    }
                                    // merging list
                                    final int pos = items.indexOf(pL);
                                    if (pos > -1) {
                                        final MergDialog dialog = new MergDialog(context);
                                        dialog.setListener(new View.OnClickListener() {
                                        @Override
                                            public void onClick(View view) {
                                                dialog.dismiss();
                                                switch (view.getId()) {
                                                    case R.id.noButton:
                                                        break;
                                                    case R.id.yesButton:
                                                        ProductList temp = items.get(pos);
                                                        temp // temporary item
                                                                .getBought() // get bought arraylist of this item
                                                                .addAll(pL.getBought()); // add all pL bought products

                                                        temp
                                                                .getToBuy() // get to buy arraylist
                                                                .addAll(pL.getToBuy());
                                                        checkAdapter();

                                                }
                                            }
                                        });
                                        dialog.show();
                                    } else {
                                        items.add(pL);
                                        checkAdapter();
                                    }
                                }

                                // close progress dialog
                                downloading.dismiss();
                                checkStatus();

                            } catch (JSONException | FileNotFoundException e) {
                                downloading.dismiss();
                                e.printStackTrace();
                                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // close progress dialog
                            downloading.dismiss();
                            Toast.makeText(context, R.string.noList, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {

                    // fatal error close dialog
                    downloading.dismiss();
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(context, R.string.needLogin, Toast.LENGTH_LONG).show();
            }

        } else if (item.getItemId() == R.id.loginI) {
            // start fragment activity for fragment_login or fragment_register
            Intent login = new Intent(context, FragmentActivity.class);
            startActivityForResult(login, LOGIN);
            return true;

        } else if (item.getItemId() == R.id.logoutI)

        {
            mAuth.signOut();
            updateUI(mAuth.getCurrentUser());
            return true;
        }
        return false;

    }

    /***********************************************************************************************/


// method for multi choice mode listener
// clicked row
    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id,
                                          boolean checked) {
        // get check box of clicked row
        CheckBox rowCheckBox = listView.getChildAt(position).findViewById(R.id.checkBoxDelete);

        // if item wasn't checked so make it checked
        if (!rowCheckBox.isChecked()) {
            rowCheckBox.setChecked(true);
            items.get(position).setSelected(true);
            selectedAmount++;
        }
        // item was checked so make it unchecked
        else {
            rowCheckBox.setChecked(false);
            items.get(position).setSelected(false);
            selectedAmount--;
        }

        // change icon of "select all" item in toolbar menu
        // all rows are selected
        if (selectedAmount == items.size()) {
            selectAll.setChecked(true);
            selectAll.setIcon(R.drawable.ic_checked);
        }
        // all rows are unselected
        else if (selectAll.isChecked()) {
            selectAll.setChecked(false);
            selectAll.setIcon(R.drawable.ic_unchecked);
        }
    }


    // create action mode for toolbar
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {

        // get inflater of menu
        MenuInflater inflater = getMenuInflater();

        // set new menu on toolbar
        inflater.inflate(R.menu.delete_menu, menu);

        // disable the add button
        addButton.setEnabled(false);

        // show check boxes to select lists
        adapter.showCheckbox();

        // set selected amount to zero
        selectedAmount = 0;

        // find select all menu item for selecting all option
        selectAll = menu.findItem(R.id.action_select_all);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    // contextual toolbar mode, menu item listener
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            // delete action
            case R.id.action_delete:

                // iterate for checking which item should be deleted
                for (ProductList p : items) {
                    if (p.isSelected()) toDelete.add(p);
                }

                //  delete lists
                for (ProductList p : toDelete) {
                    items.remove(p);

                    // delete from database
                    DataBase.getInstance(context).getDao().deleteListQuery(p.getName());
                }


                // notify that data set was changed
                adapter.notifyDataSetChanged();

                toDelete.clear();

                //finish action mode
                mode.finish();

                // check amount of items
                checkStatus();

                return true;

            // this is select all action change flags of each item and change state of checkboxes and change menu item icon
            case R.id.action_select_all:
                if (!item.isChecked()) {
                    item.setChecked(!item.isChecked());
                    item.setIcon(R.drawable.ic_checked);
                    for (int i = 0; i < items.size(); i++) {
                        if (!listView.isItemChecked(i)) {
//                            items.get(i).setBought(true);
                            listView.setItemChecked(i, true);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    return true;
                } else {
                    // this is a reverse steps of upper case
                    item.setChecked(!item.isChecked());
                    item.setIcon(R.drawable.ic_unchecked);
                    selectedAmount = 0;
                    for (int i = 0; i < items.size(); i++) {
                        if (listView.isItemChecked(i)) {
//                            items.get(i).setBought(false);
                            listView.setItemChecked(i, false);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    return true;
                }

                //share action of selected items
            case R.id.action_share:


                ArrayList<ProductList> toShare = new ArrayList<>();

                // create to share list
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        items.get(i).setSelected(false);
                        toShare.add(items.get(i));
                    }
                }

                // open dialog to share list
                Dialog dialog = new SendList(context, toShare);
                dialog.show();
                mode.finish();
                return true;

            default:
                return false;
        }
    }

    // finish mode without any action
    @Override
    public void onDestroyActionMode(ActionMode mode) {
        toDelete.clear();
        adapter.hideCheckbox();
        addButton.setEnabled(true);
        for (ProductList p : items) {
            p.setSelected(false);
        }
        selectedAmount = 0;
    }

    // i don't know what it is
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /***********************************************************************************************/


// listener for dialog to create new list
    @Override
    public void nameEntered(String name) {
        // create new list
        items.add(new ProductList(name));

        // if adapter is null create new adapter and set it to listView
        if (adapter == null) {
            adapter = new ListRow(context, R.layout.row_lists, items);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        // check status
        checkStatus();
    }

    public void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            login.setVisible(true);
            logout.setVisible(false);
        } else {
            login.setVisible(false);
            logout.setVisible(true);
        }
    }

    // check if list is empty, show/hide text and reset counter
    private void checkStatus() {
        if (items.isEmpty()) {
            // show text that inform user that he doesn't have any list
            noItemText.setVisibility(View.VISIBLE);
            id = 1;
        } else {
            // hide informing text
            noItemText.setVisibility(View.GONE);
        }
    }


    private void checkAdapter(){
        // setting adapter
        if (adapter == null) {
            adapter = new ListRow(context, R.layout.row_lists, items);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
