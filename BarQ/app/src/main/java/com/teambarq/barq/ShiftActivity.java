package com.teambarq.barq;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.ArrayList;

public class ShiftActivity extends AppCompatActivity {
    private ArrayList<Bartender> BartenderList = new ArrayList<>();
    private Firebase ref;
    private String BarID;
    private AuthData authData;
    Button newShiftButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);


        //Initializing Firebase database for HistoryActivity
      /*  ref = new Firebase("https://barq.firebaseio.com/");
        authData = ref.getAuth();
        BarID = authData.getUid();*/

        Bartender testBartender = new Bartender();
        testBartender.profilePic = R.drawable.bar_icon;
        testBartender.name = "Jojo Ortiz";
        BartenderList.add(testBartender);

        GridView gridview = (GridView) findViewById(R.id.bartender_gridview);
        gridview.setAdapter(new MyAdapter(this,BartenderList));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });


        //add onclick listener to go to serve activitiy
        newShiftButton = (Button) findViewById(R.id.newShift_Button);
        newShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShiftActivity.this, ServeActivity.class);
                startActivity(intent);
            }
        });
    }



}
