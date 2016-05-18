package com.teambarq.barq;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    //Navigation drawer
    private DrawerLayout mDrawerLayout;
    private RecyclerView navRecyclerView;
    RecyclerView.Adapter navAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager

    private String drawerTitles[] = { "Analytics", "Shift Creator", "Serve" };
    private int drawerIcons[] = {R.drawable.ic_analytics_icon,R.drawable.ic_add_person,R.drawable.ic_bar_icon};

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
<<<<<<< HEAD
        adapter = new MyAdapter(this, BartenderList);
=======
        adapter = new ShiftAdapter(this,BartenderList);
>>>>>>> 0a2a154e78ef2b9b5a066e8ab9743f8fde44d249
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
                    for (int i = 0; i < checkedItems.size(); i++) {
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

        //Setting up navigation drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navRecyclerView = (RecyclerView) findViewById(R.id.navRecyclerView); // Assigning the RecyclerView Object to the xml View

        navRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        navAdapter = new NavAdapter(drawerTitles, drawerIcons, "BarQ", "BarQ@gmail.com", R.drawable.bar_icon);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        navRecyclerView.setAdapter(navAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        navRecyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        navRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager

        //Adding touch listener for RecycleView items
        navRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position == 1)
                        {
                            Toast.makeText(getApplicationContext(), "Launching Analytics", Toast.LENGTH_SHORT).show();
                        }
                        else if(position == 2)
                        {
                            mDrawerLayout.closeDrawers();
                        }
                        else if(position == 3)
                        {
                            //Launch Queue Activity
                            Intent intent = new Intent(ShiftActivity.this, ServeActivity.class);
                            startActivity(intent);
                        }
                    }
                })
        );
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