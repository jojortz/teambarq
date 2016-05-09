package com.teambarq.barq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
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
    private ArrayList<Bartender> ServerList = new ArrayList();
    private Firebase ref;
    private String BarID;
    private AuthData authData;
    private Button createShiftButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);


        //Initializing Firebase database for HistoryActivity
      /*  ref = new Firebase("https://barq.firebaseio.com/");
        authData = ref.getAuth();
        BarID = authData.getUid();*/

        Bartender testBartender = new Bartender();
        testBartender.profilePic = "https://scontent-sjc2-1.xx.fbcdn.net/v/t1.0-9/10487280_10207376636688276_3277337388999426495_n.jpg?oh=5a65fb3a25ff4d777d14e7298ff7f880&oe=57E27F4D";
        testBartender.name = "Jojo Ortiz";
        BartenderList.add(testBartender);

        final GridView gridview = (GridView) findViewById(R.id.bartender_gridview);
        gridview.setAdapter(new MyAdapter(this,BartenderList));
        gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        createShiftButton = (Button) findViewById(R.id.newShift_Button);
        createShiftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Save current ServerList to Firebase
                SparseBooleanArray checkedItems = gridview.getCheckedItemPositions();
                if (checkedItems != null) {
                    for (int i=0; i<checkedItems.size(); i++) {
                        if (checkedItems.valueAt(i)) {
                            Bartender newBartender = BartenderList.get(checkedItems.keyAt(i));
                            ServerList.add(newBartender);
                            Toast.makeText(getApplicationContext(),newBartender.getName(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                //Launch Queue Activity

            }
        });
    }


}
