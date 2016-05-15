package com.teambarq.barq;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class ShiftActivity extends AppCompatActivity {
    private ArrayList<Bartender> BartenderList;
    private ArrayList<String> ActiveBartenderList;
    private Firebase ref;
    private Firebase activeListRef;
    private GridView gridview;
    private String BarID;
    private AuthData authData;
    private Button createShiftButton;
    private MyAdapter adapter;
    private ListView DrawerList;
    private ArrayAdapter<String> navAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;


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

        //Setting up Bartender grid view
        gridview = (GridView) findViewById(R.id.bartender_gridview);
        adapter = new MyAdapter(this,BartenderList);
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

        //Setting up navigation drawer
        DrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        addDrawerItems();

        DrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ShiftActivity.this, "Position = "+position, Toast.LENGTH_SHORT).show();
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

    private void addDrawerItems() {
        String[] drawerArray = { "Analytics", "Shift Creator", "Serve" };
        navAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerArray);
        DrawerList.setAdapter(navAdapter);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
             //   getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
               // getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

}