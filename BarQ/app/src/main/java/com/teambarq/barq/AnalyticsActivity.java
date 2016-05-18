package com.teambarq.barq;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

public class AnalyticsActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_analytics);

        //Setting up navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.analytics_drawerLayout);

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
                            //Close navigation drawer
                            mDrawerLayout.closeDrawers();
                        }
                        else if(position == 2)
                        {
                            //Close navigation drawer
                            mDrawerLayout.closeDrawers();

                            //Launch Shift Activity
                            Intent intent = new Intent(AnalyticsActivity.this, ShiftActivity.class);
                            startActivity(intent);
                        }
                        else if(position == 3)
                        {
                            //Close navigation drawer
                            mDrawerLayout.closeDrawers();

                            //Launch Serve Activity
                            Intent intent = new Intent(AnalyticsActivity.this, ServeActivity.class);
                            startActivity(intent);
                        }
                    }
                })
        );
    }

}
