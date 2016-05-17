//package com.teambarq.barq;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.firebase.client.AuthData;
//import com.firebase.client.Firebase;
//import com.firebase.client.FirebaseError;
//
//import java.util.ArrayList;
//import java.util.Map;
//
//public class LoginActivity extends AppCompatActivity {
//    private Button loginButton;
//    private EditText txtUsername;
//    private EditText txtPassword;
//    private Button newUserButton;
//    private AlertDialog.Builder userDialogBuilder;
//    private EditText txtNewUsername;
//    private EditText txtNewPassword;
//    private Firebase ref;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        ref = new Firebase("https://barq.firebaseio.com/");
//
//        //Initializing login button
//        loginButton = (Button) findViewById(R.id.login_Button);
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final String thisUsername = txtUsername.getText().toString();
//                final String thisPassword = txtPassword.getText().toString();
//
//                ref.authWithPassword(thisUsername, thisPassword,
//                        new Firebase.AuthResultHandler() {
//                            @Override
//                            public void onAuthenticated(AuthData authData) {
//                                ref.child("Bars").child(authData.getUid()).child("provider").setValue(authData.getProvider());
//                                //Launch Shift activity
//                                Intent intent = new Intent(LoginActivity.this, ShiftActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                            @Override
//                            public void onAuthenticationError(FirebaseError error) {
//                                //Display toast to indicate invalid login
//                                int errorToast;
//                                Context context = getApplicationContext();
//                                int duration = Toast.LENGTH_SHORT;
//                                if (error.toString().matches("FirebaseError: The specified password is incorrect.")){
//                                    errorToast = R.string.incorrect_password;
//                                }else if(error.toString().matches("FirebaseError: The specified user does not exist.")){
//                                    errorToast = R.string.invalid_user;
//                                }else if ((thisUsername.matches("")) && (thisPassword.matches(""))) {
//                                    errorToast = R.string.missing_username_and_password;
//                                }else if(txtUsername.getText().toString().matches("")){
//                                    errorToast = R.string.missing_username;
//                                }else if(txtPassword.getText().toString().matches("")){
//                                    errorToast = R.string.missing_password;
//                                }else{
//                                    errorToast = R.string.failed_authentication;
//                                }
//                                Toast.makeText(context,errorToast, duration).show();
//                                Log.e("Error authenticating", error.toString());
//                            }
//                        });
//
//            }
//        });
//
//        //Initializing new user button
//        newUserButton = (Button) findViewById(R.id.newUser_Button);
//        newUserButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createNewUserDialog();
//                userDialogBuilder.show();
//            }
//        });
//
//        //Find TextViews
//        txtUsername = (EditText) findViewById(R.id.username_EditText);
//        txtPassword = (EditText) findViewById(R.id.password_EditText);
//    }
//
//    public void createNewUserDialog() {
//        userDialogBuilder = new AlertDialog.Builder(this);
//        userDialogBuilder.setTitle(getString(R.string.new_user_dialog_title));
//
//        LayoutInflater inflater = this.getLayoutInflater();
//
//        // Inflate and set the layout for the dialog
//        // Pass null as the parent view because its going in the dialog layout
//        final View view = inflater.inflate(R.layout.dialog_new_user, null);
//        userDialogBuilder.setView(view);
//
//        //Find Bike ID EditText
//        txtNewUsername = (EditText) view.findViewById(R.id.newUsername_editText);
//        txtNewPassword = (EditText) view.findViewById(R.id.newPassword_editText);
//
//
//        // set positive button
//        userDialogBuilder.setPositiveButton(R.string.new_user_dialog_positive, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                //Toast variables to indicate invalid registration
//                String toastString;
//                Context context = getApplicationContext();
//                int duration = Toast.LENGTH_SHORT;
//
//                String newUsername = txtNewUsername.getText().toString();
//                String newPassword = txtNewPassword.getText().toString();
//                ref.createUser(newUsername, newPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
//                    @Override
//                    public void onSuccess(Map<String, Object> result) {
//                        //See next slide
//                    }
//                    @Override
//                    public void onError(FirebaseError firebaseError) {
//                        int errorToast;
//                        if (firebaseError.toString().matches("FirebaseError: The specified email address is already in use.")){
//                            errorToast = R.string.new_user_username_used;
//                        }else if(firebaseError.toString().matches("FirebaseError: The specified email address is invalid.")){
//                            errorToast = R.string.new_user_invalid_email;
//                        }else if (firebaseError.toString().matches("FirebaseError: The specified password is incorrect.")){
//                            errorToast = R.string.new_user_invalid_email;
//                        }else{
//                            errorToast = R.string.new_user_creation_error;
//                        }
//                        Toast.makeText(getApplicationContext(), errorToast,Toast.LENGTH_SHORT).show();
//                        Log.e("Error creating user", firebaseError.toString());
//                    }
//                });
//            }
//        });
//
//        // set negative button
//        userDialogBuilder.setNegativeButton(R.string.new_user_dialog_negative, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // cancel the alert box
//                dialog.cancel();
//            }
//        });
//    }
//}
package com.teambarq.barq;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import java.util.Map;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

/**
 * Created by jaredostdiek on 4/4/16.
 *File Description: Java file to control the Login Screen.
 */

public class LoginActivity extends AppCompatActivity {

    Button loginButton, nextButton, registerButton;
    EditText userInput, passInput;
    Context context = this;
    String usernameStr;
    Bundle loginBundle;
    Firebase myFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //create firebase reference
        myFirebaseRef = new Firebase("https://barq.firebaseio.com/");

//        Window window = this.getWindow();
//
//// clear FLAG_TRANSLUCENT_STATUS flag:
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//
//// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//
//// finally change the color
//        window.setStatusBarColor(this.getResources().getColor(R.color.lipRed));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.bluegray));
        }



        //create objects for button and user inputs
        loginButton = (Button)findViewById(R.id.loginButton);
        userInput = (EditText)findViewById(R.id.username);
        passInput = (EditText)findViewById(R.id.password);
        nextButton = (Button) findViewById(R.id.nextButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        //when next button is pressed determine if already a user or need to register
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameStr = userInput.getText().toString();

                //hack line to test if email is already in database
                myFirebaseRef.authWithPassword(usernameStr, "aakdafnngnkdjf129nf8vJaMmmadksn", new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                    }

                    //examine error code to determine if registered
                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {

                        System.out.println("UserDoesNotExist: " + FirebaseError.USER_DOES_NOT_EXIST);
                        System.out.println("InvalidPassword: " + FirebaseError.INVALID_PASSWORD);
                        System.out.println("NetworkError: " + FirebaseError.NETWORK_ERROR);
                        System.out.println("error: " + firebaseError.getCode());

                        switch (firebaseError.getCode()) {

                            //show user to register buttons
                            case FirebaseError.USER_DOES_NOT_EXIST:
                                // have user register
                                Toast.makeText(getApplicationContext(), R.string.registerToast, Toast.LENGTH_LONG).show();
                                passInput.setVisibility(View.VISIBLE);
                                passInput.setHint(R.string.setPasswordHint);
                                registerButton.setVisibility(View.VISIBLE);
                                nextButton.setVisibility(View.GONE);
                                break;

                            //already registered. show login buttons
                            case FirebaseError.INVALID_PASSWORD:
                                //already registered case
                                passInput.setVisibility(View.VISIBLE);
                                passInput.setHint(R.string.passwordHint);
                                loginButton.setVisibility(View.VISIBLE);
                                nextButton.setVisibility(View.GONE);
                                break;

                            default:
                                // handle other errors
                                break;
                        }
                    }
                });
            }
        });

        //register new user
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //create a new firebase user
                myFirebaseRef.createUser(userInput.getText().toString(), passInput.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {

                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        System.out.println("Successfully created user account with uid: " + result.get("uid"));

                        //authenticate user
                        myFirebaseRef.authWithPassword(userInput.getText().toString(), passInput.getText().toString(), new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                //authenticated successfully
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError firebaseError) {
                                //something went wrong
                                Log.i("onAuthenticatedError", "Error authenticating");
                            }
                        });

                        Toast.makeText(getApplicationContext(), R.string.registerThanks, Toast.LENGTH_SHORT).show();

                        //Create Bundle to pass username to ControlActivity
                        loginBundle = new Bundle();
                        //assign the values (key, value pairs)
                        loginBundle.putString(context.getString(R.string.username), userInput.getText().toString());

                        //create intent for control activity
                        Intent intentControl = new Intent(LoginActivity.this, ShiftActivity.class);

                        //assign the bundle to the intent
                        intentControl.putExtras(loginBundle);

                        //launch control activity
                        LoginActivity.this.startActivity(intentControl);
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                        Log.i("onError", "Error creating firebase user");
                        Toast.makeText(getApplicationContext(), R.string.registerError, Toast.LENGTH_LONG).show();
                        passInput.setText("");
                    }
                });
            }
        });

        //set login button callback for previously registered users
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //conditional to test if user and password are correct
                usernameStr = userInput.getText().toString();

                //authenticate user
                myFirebaseRef.authWithPassword(usernameStr, passInput.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        //authenticated successfully
                        //Create Bundle to pass username to ControlActivity
                        loginBundle = new Bundle();
                        //assign the values (key, value pairs)
                        loginBundle.putString(context.getString(R.string.username), usernameStr);

                        //create intent for control activity
                        Intent intentControl = new Intent(LoginActivity.this, ShiftActivity.class);

                        //assign the bundle to the intent
                        intentControl.putExtras(loginBundle);

                        //launch control activity
                        LoginActivity.this.startActivity(intentControl);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        System.out.println("error: " + firebaseError.INVALID_PASSWORD);
                        System.out.println("error: " + firebaseError.getCode());

                        //something went wrong
                        Log.i("onAuthenticatedError", "Error authenticating");

                        if (firebaseError.getCode() == firebaseError.INVALID_PASSWORD) {
                            passInput.setText("");
                            //display toast for incorrect login attempt
                            Toast.makeText(getApplicationContext(), R.string.incorrectLogin, Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), R.string.somethingWrong, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}