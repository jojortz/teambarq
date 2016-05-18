package com.teambarq.barq;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShiftActivity extends AppCompatActivity {
    private ArrayList<Bartender> BartenderList;
    private ArrayList<String> ActiveBartenderList;
    private Firebase ref;
    private Firebase activeListRef;
    private GridView gridview;
    private String BarID;
    private AuthData authData;
    private Button createShiftButton;
    private ShiftAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);



        //Initializing Bartender arrays
        BartenderList = new ArrayList<>();
        ActiveBartenderList = new ArrayList<>();

        //Initializing Firebase database for HistoryActivity
        ref = new Firebase("https://barq.firebaseio.com/");
        authData = ref.getAuth();
        BarID = authData.getUid();

        activeListRef = ref.child("Bars").child(BarID).child("ActiveBartenderList").push();

        gridview = (GridView) findViewById(R.id.bartender_gridview);
        adapter = new ShiftAdapter(this,BartenderList);
        gridview.setAdapter(adapter);
        gridview.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        loadBartenderList();

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
                            ActiveBartenderList.add(newBartender.getId());
                        }
                    }
                }

                //Add active list to Firebase
                activeListRef.setValue(ActiveBartenderList);
                //Launch Queue Activity
                Intent intent = new Intent(ShiftActivity.this, ServeActivity.class);
                startActivity(intent);
            }
        });
    }

    //Loads bartender list from Firebase and populates it into gridview
    private void loadBartenderList(){
        BartenderList.clear();
        ref.child("Bars").child(BarID).child("BartenderList").addChildEventListener(new ChildEventListener() {
            // Retrieve new posts as they are added to the database
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                Bartender thisBartender = snapshot.getValue(Bartender.class);
                BartenderList.add(thisBartender);
                Log.i("NewBartender", thisBartender.name);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String childKey) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String childKey) {

            }
            @Override
            public void onCancelled(FirebaseError error) {

            }
        });
    }

}