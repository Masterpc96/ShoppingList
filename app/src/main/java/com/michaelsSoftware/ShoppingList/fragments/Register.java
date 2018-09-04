package com.michaelsSoftware.ShoppingList.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.michaelsSoftware.ShoppingList.R;
import com.michaelsSoftware.ShoppingList.dialogs.ProgressBar_Dialog;
import com.michaelsSoftware.ShoppingList.dialogs.TextDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Register extends Fragment {

    @BindView(R.id.login)
    EditText _login;

    @BindView(R.id.password)
    EditText _password;

    @BindView(R.id.repassword)
    EditText _repassword;

    @OnClick(R.id.haveAccount)
    public void changeFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.fragment, new Login());

        transaction.commit();


    }

    @OnClick(R.id.register)
    public void register() {
        String email = _login.getText().toString();
        String password = _password.getText().toString();
        String repassword = _repassword.getText().toString();

        if (!password.equals(repassword)) {
            Dialog dialog = new TextDialog(getActivity(), getString(R.string.notSamePassword));
            dialog.show();
        } else if (!email.equals("") && !password.equals("") && !repassword.equals("")) {

            // show progress bar dialog
            final ProgressBar_Dialog registering = new ProgressBar_Dialog(getActivity());
            registering.setMessage(R.string.Registering);
            registering.show();

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            registering.dismiss();
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
//                                Toast.makeText(getActivity(), "Authentication passed. ",
//                                        Toast.LENGTH_SHORT).show();

                                // send email with verification code
                                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();

                                // get fragment manager to swap fragment
                                FragmentManager manager = getFragmentManager();
                                FragmentTransaction transaction = manager.beginTransaction();

                                transaction.replace(R.id.fragment, new Confirmation());

                                // apply changes
                                transaction.commit();

                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    // If sign in fails, display a message to the user.
                                    Dialog dialog = new TextDialog(getActivity(), getString(R.string.invalidPassword));
                                    dialog.show();
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Dialog dialog = new TextDialog(getActivity(), getString(R.string.wrongEmailFormation));
                                    dialog.show();
                                } catch (FirebaseNetworkException e) {
                                    Dialog dialog = new TextDialog(getActivity(), getString(R.string.noNet));
                                    dialog.show();
                                } catch (Exception e) {
                                    Dialog dialog = new TextDialog(getActivity(), e.toString());
                                    dialog.show();
                                }
                            }
                        }
                    });
        } else {
            Dialog dialog = new TextDialog(getActivity(), getString(R.string.emptyEmailPassword));
            dialog.show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
