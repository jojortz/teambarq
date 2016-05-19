package com.teambarq.barq;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
    Firebase myFirebaseRef;
    private String barID;
    //TODO debounce buttons
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //create firebase reference
        myFirebaseRef = new Firebase("https://barq.firebaseio.com/");

        //clear authorization data
        myFirebaseRef.unauth();

//        // code to change color of status bar (not working properly)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.royalblue));
//        }



        //create objects for button and user inputs
        loginButton = (Button)findViewById(R.id.loginButton);
        userInput = (EditText)findViewById(R.id.username);
        passInput = (EditText)findViewById(R.id.password);
        nextButton = (Button) findViewById(R.id.nextButton);
        registerButton = (Button) findViewById(R.id.registerButton);

        //set fonts
        Typeface gothamExtraLight =Typeface.createFromAsset(getAssets(),"fonts/gothamExtraLight.TTF");
        Typeface gothamBold =Typeface.createFromAsset(getAssets(),"fonts/gothamBold.TTF");
        loginButton.setTypeface(gothamBold);
        userInput.setTypeface(gothamExtraLight);
        passInput.setTypeface(gothamExtraLight);
        nextButton.setTypeface(gothamBold);
        registerButton.setTypeface(gothamBold);

        //hide keyboard if click off edittext
        setupParent(findViewById(R.id.loginRelativeLayout));


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
                        System.out.println("InvalidEmail: " + FirebaseError.INVALID_EMAIL);
                        System.out.println("error: " + firebaseError.getCode());

                        switch (firebaseError.getCode()) {

                            case FirebaseError.INVALID_EMAIL:
                                // toast to input email
                                Toast.makeText(getApplicationContext(), R.string.invalidEmail, Toast.LENGTH_SHORT).show();
                                break;

                            //show user to register buttons
                            case FirebaseError.USER_DOES_NOT_EXIST:
                                // have user register
                                Toast.makeText(getApplicationContext(), R.string.registerToast, Toast.LENGTH_SHORT).show();
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

                        //wait for auth data to update to current user

                        myFirebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
                            @Override
                            public void onAuthStateChanged(AuthData authData) {
                                if (authData != null) {
                                    //Create a new bar user in FirebaseBase w/ devices and bartenders
                                    barID = myFirebaseRef.getAuth().getUid();
                                    addBartenders();
                                    addDevices();

                                    //create intent for control activity
                                    Intent intentControl = new Intent(LoginActivity.this, ShiftActivity.class);

                                    //launch control activity
                                    LoginActivity.this.startActivity(intentControl);
                                    finish();
                                }
                            }
                        });


                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                        Log.i("onError", "Error creating firebase user");
                        Toast.makeText(getApplicationContext(), R.string.registerError, Toast.LENGTH_SHORT).show();
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


                        //create intent for control activity
                        Intent intentControl = new Intent(LoginActivity.this, ShiftActivity.class);

                        //launch control activity
                        LoginActivity.this.startActivity(intentControl);
                        //make it so you cant go back to login activity
                        finish();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        System.out.println("error: " + FirebaseError.INVALID_PASSWORD);
                        System.out.println("error: " + firebaseError.getCode());

                        //something went wrong
                        Log.i("onAuthenticatedError", "Error authenticating");

                        if (firebaseError.getCode() == FirebaseError.INVALID_PASSWORD) {
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

    protected void setupParent(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }
        //If a layout container, iterate over children
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupParent(innerView);
            }
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    //Adding Bartenders to Firebase
    public void addBartenders() {
        Firebase newPostRef = myFirebaseRef.child(barID).child("BartenderList").push();
        newPostRef.setValue(new Bartender("Tara", "https://www.jordanmatter.com/images/gallery/Tara_Westwood.jpg"));
        updateBartenderID(newPostRef);
        newPostRef = myFirebaseRef.child(barID).child("BartenderList").push();
        newPostRef.setValue(new Bartender("Jojo", "https://scontent-sjc2-1.xx.fbcdn.net/v/t1.0-9/10487280_10207376636688276_3277337388999426495_n.jpg?oh=5a65fb3a25ff4d777d14e7298ff7f880&oe=57E27F4D"));
        updateBartenderID(newPostRef);
        newPostRef = myFirebaseRef.child(barID).child("BartenderList").push();
        newPostRef.setValue(new Bartender("Alan", "https://www.jordanmatter.com/images/gallery/Alan_Cumming.jpg"));
        updateBartenderID(newPostRef);
        newPostRef = myFirebaseRef.child(barID).child("BartenderList").push();
        newPostRef.setValue(new Bartender("Mallory", "https://www.jordanmatter.com/images/gallery/Mallory_Moye.jpg"));
        updateBartenderID(newPostRef);
        newPostRef = myFirebaseRef.child(barID).child("BartenderList").push();
        newPostRef.setValue(new Bartender("Penn", "https://www.jordanmatter.com/images/gallery/penn-jillette.jpg"));
        updateBartenderID(newPostRef);
        newPostRef = myFirebaseRef.child(barID).child("BartenderList").push();
        newPostRef.setValue(new Bartender("Abena", "https://www.jordanmatter.com/images/gallery/Abena_Koomson.jpg"));
        updateBartenderID(newPostRef);
    }

    public void updateBartenderID(Firebase newPostRef) {
        String postId = newPostRef.getKey();
        Map<String, Object> myMap1 = new HashMap<String, Object>();
        myMap1.put("id", postId);
        newPostRef.updateChildren(myMap1);
    }

    //Adding Devices to Firebase
    private void addDevices() {
        Firebase newPostRef = myFirebaseRef.child(barID).child("Devices");
        newPostRef.push().setValue(new Device("5ccf7f0fd6e4","Red","Left"));
        newPostRef.push().setValue(new Device("5ccf7f006c6c","Yellow","Center"));
        newPostRef.push().setValue(new Device("18fe34d45db8","Blue","Right"));
    }
}