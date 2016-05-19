package com.teambarq.barq;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class AnalyticsActivity extends AppCompatActivity {

    Firebase ref = new Firebase("https://barq.firebaseio.com/");
    private AuthData authData;
    private Firebase user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        BarChart chart = (BarChart) findViewById(R.id.chart);

        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("My Chart");
        chart.animateXY(2000, 2000);
        chart.invalidate();
    }

    private BarDataSet getDataSet() {
        //ArrayList<BarDataSet> dataSets = null;

        user.child("BartenderList").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Bartender bartender = itemSnapshot.getValue(Bartender.class);


                    //user.child("RunningQueue").child(itemSnapshot.getKey()).child("QueuePosition").setValue(newPosition);
//                            Log.e("FB", "Updating queue positions in FB");
                }
                //pos = pos - 1;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        BarEntry v1e1 = new BarEntry(110.000f, 0); // Jan
        valueSet1.add(v1e1);
        BarEntry v1e2 = new BarEntry(40.000f, 1); // Feb
        valueSet1.add(v1e2);
        BarEntry v1e3 = new BarEntry(60.000f, 2); // Mar
        valueSet1.add(v1e3);
        BarEntry v1e4 = new BarEntry(30.000f, 3); // Apr
        valueSet1.add(v1e4);
        BarEntry v1e5 = new BarEntry(90.000f, 4); // May
        valueSet1.add(v1e5);
        BarEntry v1e6 = new BarEntry(100.000f, 5); // Jun
        valueSet1.add(v1e6);





        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "");
        barDataSet1.setColors(ColorTemplate.JOYFUL_COLORS);
        return barDataSet1;

//        dataSets = new ArrayList<>();
//        dataSets.add(barDataSet1);
//        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        return xAxis;
    }
}









//public class AnalyticsActivity extends AppCompatActivity {
//    //Navigation drawer
//    private DrawerLayout mDrawerLayout;
//    private RecyclerView navRecyclerView;
//    RecyclerView.Adapter navAdapter;                        // Declaring Adapter For Recycler View
//    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager
//
//    private String drawerTitles[] = { "Analytics", "Shift Creator", "Serve" };
//    private int drawerIcons[] = {R.drawable.ic_analytics_icon,R.drawable.ic_add_person,R.drawable.ic_bar_icon};
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_analytics);
//
//
//
//
//
//
//
//
//
//
//
//
//
//        //Setting up navigation drawer
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.analytics_drawerLayout);
//
//        navRecyclerView = (RecyclerView) findViewById(R.id.navRecyclerView); // Assigning the RecyclerView Object to the xml View
//
//        navRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size
//
//        navAdapter = new NavAdapter(drawerTitles, drawerIcons, "BarQ", "BarQ@gmail.com", R.drawable.bar_icon);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
//        // And passing the titles,icons,header view name, header view email,
//        // and header view profile picture
//        navRecyclerView.setAdapter(navAdapter);                              // Setting the adapter to RecyclerView
//        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
//        navRecyclerView.setLayoutManager(mLayoutManager);
//        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
//        navRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager
//
//        //Adding touch listener for RecycleView items
//        navRecyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        if (position == 1)
//                        {
//                            //Close navigation drawer
//                            mDrawerLayout.closeDrawers();
//                        }
//                        else if(position == 2)
//                        {
//                            //Close navigation drawer
//                            mDrawerLayout.closeDrawers();
//
//                            //Launch Shift Activity
//                            Intent intent = new Intent(AnalyticsActivity.this, ShiftActivity.class);
//                            startActivity(intent);
//                        }
//                        else if(position == 3)
//                        {
//                            //Close navigation drawer
//                            mDrawerLayout.closeDrawers();
//
//                            //Launch Serve Activity
//                            Intent intent = new Intent(AnalyticsActivity.this, ServeActivity.class);
//                            startActivity(intent);
//                        }
//                    }
//                })
//        );
//    }
//
//}
