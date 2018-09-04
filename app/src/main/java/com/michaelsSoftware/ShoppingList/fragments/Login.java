package com.michaelsSoftware.ShoppingList.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.michaelsSoftware.ShoppingList.R;
import com.michaelsSoftware.ShoppingList.dialogs.ProgressBar_Dialog;
import com.michaelsSoftware.ShoppingList.dialogs.TextDialog;
import com.michaelsSoftware.ShoppingList.utils.Email;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    // sign in option for google sign in dialog
    private GoogleSignInOptions gso;

    // create sign in client
    private GoogleApiClient mGoogleApiClient;

    // sign in code
    private static final int RC_SIGN_IN = 50;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // fragment_login field
    @BindView(R.id.loginLogin)
    EditText _login;

    // password field
    @BindView(R.id.passwordLogin)
    EditText _password;

    // when fragment_login button was clicked make this code
    @OnClick(R.id.loginButtonLogin)
    public void login() {

        // email value from field
        String email = _login.getText().toString();

        // password value from field
        String password = _password.getText().toString();

        if (!email.equals("") && !password.equals("")) {

            // show progress bar dialog
            final ProgressBar_Dialog logging = new ProgressBar_Dialog(getActivity());
            logging.setMessage(R.string.Logging);
            logging.show();

            // log in firebase using email and password
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            logging.dismiss();
                            if (task.isSuccessful()) {

                                // Sign in success, update UI with the signed-in user's information
                                Toast toast = new Toast(getActivity());
                                toast.setDuration(Toast.LENGTH_LONG);

                                // get layout inflater from activity and get view from it
                                LayoutInflater inflater = getActivity().getLayoutInflater();
                                View view = inflater.inflate(R.layout.toast_layout, (ViewGroup) getActivity().findViewById(R.id.toast_layout_linear));

                                // set image and text
                                ImageView image = view.findViewById(R.id.userPhoto);
                                image.setVisibility(View.GONE);
                                TextView text = view.findViewById(R.id.userName);
                                text.setText(getString(R.string.comeBack) + " " + Email.getNameFromEmail(mAuth.getCurrentUser().getEmail()));

                                toast.setView(view);
                                mAuth.getCurrentUser().getPhotoUrl();

                                toast.show();

                                if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                    getActivity().finish();
                                } else {

                                    // get fragment manager and fragment transaction
                                    FragmentManager manager = getFragmentManager();
                                    FragmentTransaction transaction = manager.beginTransaction();

                                    // replace fragment on register form
                                    transaction.replace(R.id.fragment, new Confirmation());

                                    // commit transaction
                                    transaction.commit();
                                }

                            } else {
                                // and throw exception
                                try {
                                    throw task.getException();
                                } catch (FirebaseNetworkException e) {
                                    Dialog dialog = new TextDialog(getActivity(), getString(R.string.noNet));
                                    dialog.show();
                                } catch (FirebaseAuthInvalidCredentialsException | FirebaseAuthInvalidUserException e) {
                                    Dialog dialog = new TextDialog(getActivity(), getString(R.string.InvalidPassword));
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

    // forget password action
    @OnClick(R.id.forgetPassword)
    public void resetPassword() {

        // get fragment manager and fragment transaction
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // replace fragment on register form
        transaction.replace(R.id.fragment, new ResetPassword());

        // commit transaction
        transaction.commit();

    }

    // fragment_login with google button clicked
    @OnClick(R.id.signInGoogle)
    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // when text view was clicked, replace fragment
    @OnClick(R.id.noAccountYet)
    public void goToRegister() {

        // get fragment manager and fragment transaction
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // replace fragment on register form
        transaction.replace(R.id.fragment, new Register());

        mGoogleApiClient.stopAutoManage((AppCompatActivity) getActivity());
        mGoogleApiClient.disconnect();

        // commit transaction
        transaction.commit();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);
        // init GoogleSignInOption
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getActivity().getResources().getString(R.string.key))
                .requestEmail()
                .build();


        // init GoogleSignInClient
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(((AppCompatActivity) getActivity()), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStop() {
        mGoogleApiClient.stopAutoManage((AppCompatActivity) getActivity());
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        // show progress bar dialog
        final ProgressBar_Dialog logging = new ProgressBar_Dialog(getActivity());
        logging.setMessage(R.string.Logging);
        logging.show();

        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);

            firebaseAuthWithGoogle(account, logging);
        } catch (ApiException e) {

            // Google Sign In failed, update UI appropriately
            logging.dismiss();
            Toast.makeText(getActivity(),R.string.errorWithGoogle + " error " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct, final ProgressBar_Dialog logging) {

        // authenticate with Firebase
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        logging.dismiss();
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            Toast toast = new Toast(getActivity());
                            toast.setDuration(Toast.LENGTH_LONG);

                            // get layout inflater from activity and get view from it
                            LayoutInflater inflater = getActivity().getLayoutInflater();
                            View view = inflater.inflate(R.layout.toast_layout, (ViewGroup) getActivity().findViewById(R.id.toast_layout_linear));

                            // set image and text
                            ImageView image = view.findViewById(R.id.userPhoto);

                            // load image to image view
                            Picasso.get().load(mAuth.getCurrentUser().getPhotoUrl()).into(image);

                            TextView text = view.findViewById(R.id.userName);
                            text.setText(getString(R.string.comeBack) + " " + mAuth.getCurrentUser().getDisplayName());

                            toast.setView(view);
                            toast.show();

                            getActivity().finish();
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
    }
}
