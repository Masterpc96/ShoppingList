package com.michaelsSoftware.ShoppingList.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.michaelsSoftware.ShoppingList.R;
import com.michaelsSoftware.ShoppingList.dialogs.ProgressBar_Dialog;
import com.michaelsSoftware.ShoppingList.dialogs.TextDialog;
import com.michaelsSoftware.ShoppingList.utils.Email;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ResetPassword extends Fragment {

    @BindView(R.id.emailReset)
    EditText email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.resendButtonPassword)
    public void send() {

        // email address from field
        String _email = email.getText().toString();

        // validate if _email is not null and it is a valid email
        if (!_email.equals("") && Email.isEmailValid(_email)) {

            // show progress bar dialog
            final ProgressBar_Dialog sending = new ProgressBar_Dialog(getActivity());
            sending.setMessage(R.string.Sending);
            sending.show();

            // send email reset password
            // get instance and set complete listener
            FirebaseAuth.getInstance().sendPasswordResetEmail(email.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sending.dismiss();
                            if (task.isSuccessful()) {
                                getActivity().finish();
                                Toast.makeText(getActivity(), R.string.EmailSend, Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    Dialog dialog = new TextDialog(getActivity(), getString(R.string.noUser));
                                    dialog.show();
                                } catch (FirebaseNetworkException e) {
                                    Dialog dialog = new TextDialog(getActivity(), getString(R.string.noNet));
                                    dialog.show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        } else {
            Dialog dialog = new TextDialog(getActivity(), getString(R.string.wrongEmail));
            dialog.show();
        }

    }
}
