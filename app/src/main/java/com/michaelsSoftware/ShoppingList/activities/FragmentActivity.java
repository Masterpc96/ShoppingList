package com.michaelsSoftware.ShoppingList.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.michaelsSoftware.ShoppingList.R;
import com.michaelsSoftware.ShoppingList.fragments.Confirmation;
import com.michaelsSoftware.ShoppingList.fragments.Login;

import butterknife.ButterKnife;

public class FragmentActivity extends AppCompatActivity {
    Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        ButterKnife.bind(this);
        int confirm = getIntent().getIntExtra("confirm", 0);

        // get fragment manager and fragment transaction
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction;


        if (confirm == 1) {

            // get fragment manager and fragment transaction
            transaction = manager.beginTransaction();

            // replace fragment on register form
            transaction.add(R.id.fragment, new Confirmation());

            // commit transaction
            transaction.commit();
        } else {
            // get fragment manager and fragment transaction
            transaction = manager.beginTransaction();

            // replace fragment on register form
            transaction.add(R.id.fragment, new Login());

            // commit transaction
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        fragment = getFragmentManager().findFragmentById(R.id.fragment);

        if (fragment instanceof Confirmation) {
            // do nothing because it's a confirmation fragment
        } else {
            super.onBackPressed();
        }

    }
}
