package com.michaelsSoftware.ShoppingList.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.michaelsSoftware.ShoppingList.R;
import com.michaelsSoftware.ShoppingList.dialogs.ProgressBar_Dialog;
import com.michaelsSoftware.ShoppingList.dialogs.TextDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Confirmation extends Fragment {

    @BindView(R.id.emailConfirm)
    TextView email;

    @BindView(R.id.confirmConfirm)
    TextView confirmation;

    // sign out
    @OnClick(R.id.signOutButtonConfirm)
    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        getActivity().finish();
    }

    // resend email verification
    @OnClick(R.id.resendButtonConfirm)
    public void resend() {

        // show progress bar dialog
        final ProgressBar_Dialog logging = new ProgressBar_Dialog(getActivity());
        logging.setMessage(R.string.Sending);
        logging.show();

        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                logging.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), R.string.emailReset, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseNetworkException e) {
                        Dialog dialog = new TextDialog(getActivity(), getString(R.string.noNet));
                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // email is confirmed log in
    @OnClick(R.id.confirmedText)
    public void confirmed() {
        final ProgressBar_Dialog logging = new ProgressBar_Dialog(getActivity());
        logging.setMessage(R.string.EmailChecking);
        logging.show();

        FirebaseAuth.getInstance().getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                    logging.dismiss();
                    getActivity().finish();
                    Toast.makeText(getActivity(), R.string.successfullyVerified, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_email, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        email.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            confirmation.setText(getString(R.string.Verified));
        } else {
            confirmation.setText(getString(R.string.noVerified));
        }
    }
}
