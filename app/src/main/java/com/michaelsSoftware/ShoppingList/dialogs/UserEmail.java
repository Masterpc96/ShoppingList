package com.michaelsSoftware.ShoppingList.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.michaelsSoftware.ShoppingList.R;
import com.michaelsSoftware.ShoppingList.utils.Email;
import com.michaelsSoftware.ShoppingList.utils.Json;
import com.michaelsSoftware.ShoppingList.own_classes.ProductList;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserEmail extends Dialog {
    @BindView(R.id.emailToSend)
    EditText email;

    private ArrayList<ProductList> lists;
    private Context context;

    public UserEmail(@NonNull Context context, @NotNull ArrayList<ProductList> lists) {
        super(context, R.style.DialogTheme);
        setContentView(R.layout.dialog_send_to);
        ButterKnife.bind(this);

        this.lists = lists;
        this.context = context;

    }

    @OnClick(R.id.sendButton)
    public void send() {

        String _email = email.getText().toString();

        // email cannot be empty
        if (_email.equals("")) {
            Toast.makeText(context, R.string.emptyEmail, Toast.LENGTH_LONG).show();
        }

        // email isn't empty check if user exists
        else
            // checking if user exists
            FirebaseAuth.getInstance().fetchSignInMethodsForEmail(_email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                    final ProgressBar_Dialog dialog = new ProgressBar_Dialog(getContext());
                    dialog.setMessage(R.string.Sending);
                    dialog.show();
                    // if task is successfull then check if user list is empty
                    if (task.isSuccessful()) {

                        // check if user list is empty
                        boolean exist = !task.getResult().getSignInMethods().isEmpty();

                        // create flag if json file is saved
                        boolean saved = false;

                        // create file that will be upload to storage
                        File toShareFile;

                        // user doesn't exist
                        if (!exist) {
                            dialog.dismiss();
                            Toast.makeText(context, R.string.noUser, Toast.LENGTH_LONG).show();
                        }

                        // if user exists then create json file
                        else {
                            // create json array with lists to be shared
                            JSONArray jsonToSend = new JSONArray();
                            try {
                                jsonToSend = Json.getCoverter().convertIntoJson(lists);
                                Log.d("TAGG1", jsonToSend.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            // create file that will be send to storage
                            toShareFile = new File(context.getExternalCacheDir(), Email.convertEmail(email.getText().toString()));
                            FileWriter writer;

                            // save json to text file
                            try {

                                // create file writer
                                writer = new FileWriter(toShareFile);

                                // write to file
                                writer.write(jsonToSend.toString());

                                // close file
                                writer.close();

                                // set flag true
                                saved = true;

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (saved) {

                                // create uri from file
                                Uri file = Uri.fromFile(toShareFile);

                                // get references to main folder (root)
                                StorageReference storage = FirebaseStorage.getInstance().getReference();


                                // get references to child of main folder (Lists) and create file
                                StorageReference lists = storage.child("Lists/" + file.getLastPathSegment());

                                // create instance of Uploaded task and put file to storage references
                                UploadTask Uploadtask = lists.putFile(file);

                                // add on complete listener
                                Uploadtask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        Toast.makeText(context, R.string.uploaded, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        dismiss();
                                    }
                                });

                                // add on failure listener
                                task.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.dismiss();
                                        if (e instanceof FirebaseNetworkException) {
                                            Dialog dialog = new TextDialog(context, context.getString(R.string.noNet));
                                            dialog.show();
                                        } else {
                                            Dialog dialog = new TextDialog(context, context.getString(R.string.fatalError));
                                            dialog.show();
                                        }

                                    }
                                });
                            }

                        }
                    }
                }
            });
    }
}
