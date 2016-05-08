package com.teambarq.barq;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText txtUsername;
    private EditText txtPassword;
    private Button newUserButton;
    private AlertDialog.Builder userDialogBuilder;
    private EditText txtNewUsername;
    private EditText txtNewPassword;
    private Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ref = new Firebase("https://barq.firebaseio.com/");

        //Initializing login button
        loginButton = (Button) findViewById(R.id.login_Button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String thisUsername = txtUsername.getText().toString();
                final String thisPassword = txtPassword.getText().toString();

                ref.authWithPassword(thisUsername, thisPassword,
                        new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                ref.child("bars").child(authData.getUid()).child("provider").setValue(authData.getProvider());
                                //Launch Shift activity
                                Intent intent = new Intent(LoginActivity.this, ShiftActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            @Override
                            public void onAuthenticationError(FirebaseError error) {
                                //Display toast to indicate invalid login
                                int errorToast;
                                Context context = getApplicationContext();
                                int duration = Toast.LENGTH_SHORT;
                                if (error.toString().matches("FirebaseError: The specified password is incorrect.")){
                                    errorToast = R.string.incorrect_password;
                                }else if(error.toString().matches("FirebaseError: The specified user does not exist.")){
                                    errorToast = R.string.invalid_user;
                                }else if ((thisUsername.matches("")) && (thisPassword.matches(""))) {
                                    errorToast = R.string.missing_username_and_password;
                                }else if(txtUsername.getText().toString().matches("")){
                                    errorToast = R.string.missing_username;
                                }else if(txtPassword.getText().toString().matches("")){
                                    errorToast = R.string.missing_password;
                                }else{
                                    errorToast = R.string.failed_authentication;
                                }
                                Toast.makeText(context,errorToast, duration).show();
                                Log.e("Error authenticating", error.toString());
                            }
                        });

            }
        });

        //Initializing new user button
        newUserButton = (Button) findViewById(R.id.newUser_Button);
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUserDialog();
                userDialogBuilder.show();
            }
        });

        //Find TextViews
        txtUsername = (EditText) findViewById(R.id.username_EditText);
        txtPassword = (EditText) findViewById(R.id.password_EditText);
    }

    public void createNewUserDialog() {
        userDialogBuilder = new AlertDialog.Builder(this);
        userDialogBuilder.setTitle(getString(R.string.new_user_dialog_title));

        LayoutInflater inflater = this.getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_new_user, null);
        userDialogBuilder.setView(view);

        //Find Bike ID EditText
        txtNewUsername = (EditText) view.findViewById(R.id.newUsername_editText);
        txtNewPassword = (EditText) view.findViewById(R.id.newPassword_editText);


        // set positive button
        userDialogBuilder.setPositiveButton(R.string.new_user_dialog_positive, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Toast variables to indicate invalid registration
                String toastString;
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;

                String newUsername = txtNewUsername.getText().toString();
                String newPassword = txtNewPassword.getText().toString();
                ref.createUser(newUsername, newPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        //See next slide
                    }
                    @Override
                    public void onError(FirebaseError firebaseError) {
                        int errorToast;
                        if (firebaseError.toString().matches("FirebaseError: The specified email address is already in use.")){
                            errorToast = R.string.new_user_username_used;
                        }else if(firebaseError.toString().matches("FirebaseError: The specified email address is invalid.")){
                            errorToast = R.string.new_user_invalid_email;
                        }else if (firebaseError.toString().matches("FirebaseError: The specified password is incorrect.")){
                            errorToast = R.string.new_user_invalid_email;
                        }else{
                            errorToast = R.string.new_user_creation_error;
                        }
                        Toast.makeText(getApplicationContext(), errorToast,Toast.LENGTH_SHORT).show();
                        Log.e("Error creating user", firebaseError.toString());
                    }
                });
            }
        });

        // set negative button
        userDialogBuilder.setNegativeButton(R.string.new_user_dialog_negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // cancel the alert box
                dialog.cancel();
            }
        });
    }
}
