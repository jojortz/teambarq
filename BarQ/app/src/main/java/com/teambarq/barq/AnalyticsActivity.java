package com.teambarq.barq;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.util.ArrayList;

public class AnalyticsActivity extends FragmentActivity implements ActionBar.TabListener {
    //Fragment
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    Context context = this;
    static BarChart chart;
    static PieChart pieChart;

    //Navigation drawer
    private DrawerLayout mDrawerLayout;
    private RecyclerView navRecyclerView;
    private RecyclerView.Adapter navAdapter;                        // Declaring Adapter For Recycler View
    private RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager

    private String drawerTitles[] = {"Analytics", "Shift Creator", "Feedback", "Help"};
    private int drawerIcons[] = {R.drawable.ic_analytics_icon, R.drawable.ic_add_person,  R.drawable.ic_feedback_icon, R.drawable.ic_help_icon};

    private ImageButton navigationMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_analytics);

        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        //   final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        // actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        //    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                //      actionBar.setSelectedNavigationItem(position);
                pieChart.animateY(1400);
                chart.animateY(3000);
                chart.invalidate();
                pieChart.invalidate();
            }
        });

        //Setting up navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.analytics_drawerLayout);

        navRecyclerView = (RecyclerView) findViewById(R.id.analytics_navRecyclerView); // Assigning the RecyclerView Object to the xml View

        navRecyclerView.setHasFixedSize(true);                            // Letting the system know that the list objects are of fixed size

        navAdapter = new NavAdapter(drawerTitles, drawerIcons, "BarQ", "BarQ@gmail.com", R.drawable.barq_logo_white_text);       // Creating the Adapter of MyAdapter class(which we are going to see in a bit)
        // And passing the titles,icons,header view name, header view email,
        // and header view profile picture
        navRecyclerView.setAdapter(navAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        navRecyclerView.setLayoutManager(mLayoutManager);
        mLayoutManager = new LinearLayoutManager(this);                 // Creating a layout Manager
        navRecyclerView.setLayoutManager(mLayoutManager);                 // Setting the layout Manager

        //Adding touch listener for RecycleView items
        navRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (position == 1) {
                            //Close navigation drawer
                            mDrawerLayout.closeDrawers();
                        } else if (position == 2) {
                            //Close navigation drawer
                            mDrawerLayout.closeDrawers();

                            //Launch Shift Activity
                            Intent intent = new Intent(AnalyticsActivity.this, ShiftActivity.class);
                            startActivity(intent);
                        }
                    }
                })
        );

        navigationMenuButton = (ImageButton) findViewById(R.id.analyticsMenu_imageButton);

        navigationMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Save current ServerList to Firebase
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        mDrawerLayout.closeDrawers();

    }


    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                    return new BarChartFragment();

                case 1:
                    Fragment pieChartFrag = new PieChartFragment();
                    return pieChartFrag;

                default:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new PieChartFragment();
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }
    }


    /**
     * --------------------------------------------------------------------------------------------------------
     * Bar chart that displays average time it takes each bartender to serve customers
     * --------------------------------------------------------------------------------------------------------
     */


    public static class BarChartFragment extends Fragment {
        private ArrayList<BarEntry> yAxisBarData;
        private ArrayList<String> xAxisBarLabel;
        private BarDataSet barDataSet;

        Firebase ref = new Firebase("https://barq.firebaseio.com/");
        private AuthData authData;
        private Firebase user;
        private LimitLine line;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_bar_chart, container, false);

            //Initializing Firebase database for HistoryActivity
            authData = ref.getAuth();
            String authUid = authData.getUid();
            user = ref.child(authUid);
            Log.i("user", authUid);

            chart = (BarChart) rootView.findViewById(R.id.chart);
//            chart.setVisibility(View.INVISIBLE);

            //set yaxis label
            TextView yAxisLabel = (TextView) rootView.findViewById(R.id.barchartYLabel);
            Typeface gothamRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/gothamRegular.TTF");
            Typeface gothamMedium = Typeface.createFromAsset(getContext().getAssets(), "fonts/gothamMedium.TTF");

            yAxisLabel.setTextColor(getResources().getColor(R.color.darkgray));
            yAxisLabel.setTypeface(gothamMedium);

            TextView barchartTitle = (TextView) rootView.findViewById(R.id.barchartTitle);
            barchartTitle.setTypeface(gothamMedium);
            barchartTitle.setTextColor(getResources().getColor(R.color.darkgray));



            //get data
            getDataSet();


            return rootView;
        }

        private void getDataSet() {
            //ArrayList<BarDataSet> dataSets = null;

            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("inside", "of function");
                    yAxisBarData = new ArrayList<>();
                    xAxisBarLabel = new ArrayList<>();
                    int idx = 0;
                    for (DataSnapshot bartenderSnapshot : dataSnapshot.child("BartenderList").getChildren()) {
                        Bartender bartender = bartenderSnapshot.getValue(Bartender.class);
                        //get ave time for each bartender
                        if (bartender.totalOrdersServed != 0) {
                            float duration = new Float((float) bartender.totalDuration).longValue();
                            float aveMillis = duration / bartender.totalOrdersServed;
                            float aveSecs = aveMillis / 1000;

                            BarEntry barEntry = new BarEntry(aveSecs, idx);
                            yAxisBarData.add(barEntry);

                            //add bartender to x axis array
                            xAxisBarLabel.add(bartender.getName());
                            idx++;
                        }
                    }

                    int allOrdersCount = 0;
                    float allOrdersDur = 0;

                    for (DataSnapshot orderSnapshot : dataSnapshot.child("AllOrders").getChildren()) {
                        Order order = orderSnapshot.getValue(Order.class);

                        float orderDur = new Float((float) order.Duration).longValue();
                        allOrdersDur = allOrdersDur + orderDur;
                        allOrdersCount++;

                    }

                    float aveOrderDur = (allOrdersDur / allOrdersCount) / 1000;

                    Log.i("aveLine", String.valueOf(aveOrderDur));
                    line = new LimitLine(aveOrderDur);
                    line.setLineColor(getResources().getColor(R.color.darkgray));
                    line.setLineWidth(4);
                    line.enableDashedLine(20f, 10f, 0f);
                    line.setLabel(getResources().getString(R.string.bar_ave_wait));
                    line.setTextSize(15);
                    line.setTextColor(getResources().getColor(R.color.darkgray));


                    Typeface gothamRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/gothamRegular.TTF");
                    Typeface gothamMedium = Typeface.createFromAsset(getContext().getAssets(), "fonts/gothamMedium.TTF");

                    barDataSet = new BarDataSet(yAxisBarData, "");

                    barDataSet.setColors(getBarChartColors(getContext()));

                    BarData data = new BarData(xAxisBarLabel, barDataSet);


                    //add average value limitLine
                    YAxis yAxis = chart.getAxisLeft();
                    yAxis.addLimitLine(line);
                    yAxis.setTypeface(gothamMedium);
                    yAxis.setTextColor(getResources().getColor(R.color.darkgray));
                    yAxis.setTextSize(20);
                    yAxis.setDrawGridLines(false);
                    yAxis.setDrawLabels(true);
                    yAxis.setValueFormatter(new IntForBarChartValueFormatter());

                    XAxis xAxis = chart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setTypeface(gothamMedium);
                    xAxis.setTextColor(getResources().getColor(R.color.darkgray));
                    xAxis.setTextSize(16f);
                    xAxis.setDrawGridLines(false);

                    //format chart
                    chart.setDescription("");
                    chart.getAxisRight().setEnabled(false); //turn off right axis
                    chart.setHighlightPerTapEnabled(false);
                    chart.setDrawHighlightArrow(false);
                    chart.setHighlightPerDragEnabled(false);
                    chart.getLegend().setEnabled(false);
                    chart.setDrawGridBackground(false);
                    chart.setPinchZoom(false);
                    chart.setDoubleTapToZoomEnabled(false);
                    chart.setData(data);
                    chart.animateY(3000);
                    chart.getData().setDrawValues(false);
                    chart.invalidate();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }

        public int[] getBarChartColors(Context context) {

            int[] barChartColors = {Color.rgb(Color.red(context.getResources().getColor(R.color.redorange)),
                    Color.green(context.getResources().getColor(R.color.redorange)),
                    Color.blue(context.getResources().getColor(R.color.redorange))),

                    Color.rgb(Color.red(context.getResources().getColor(R.color.bluegray)),
                            Color.green(context.getResources().getColor(R.color.bluegray)),
                            Color.blue(context.getResources().getColor(R.color.bluegray))),

                    Color.rgb(Color.red(context.getResources().getColor(R.color.softyellow)),
                            Color.green(context.getResources().getColor(R.color.softyellow)),
                            Color.blue(context.getResources().getColor(R.color.softyellow))),

                    Color.rgb(Color.red(context.getResources().getColor(R.color.bluegreen)),
                            Color.green(context.getResources().getColor(R.color.bluegreen)),
                            Color.blue(context.getResources().getColor(R.color.bluegreen))),

                    Color.rgb(Color.red(context.getResources().getColor(R.color.brightpurple)),
                            Color.green(context.getResources().getColor(R.color.brightpurple)),
                            Color.blue(context.getResources().getColor(R.color.brightpurple))),

                    Color.rgb(Color.red(context.getResources().getColor(R.color.sweetpink)),
                            Color.green(context.getResources().getColor(R.color.sweetpink)),
                            Color.blue(context.getResources().getColor(R.color.sweetpink)))};

            return barChartColors;
        }

    }

    /**
     * --------------------------------------------------------------------------------------------------------
     * Pie Chart that breaks down the percentage of orders from each location
     * --------------------------------------------------------------------------------------------------------
     */


    public static class PieChartFragment extends Fragment {

        Firebase ref = new Firebase("https://barq.firebaseio.com/");
        private AuthData authData;
        private Firebase user;
        PieData d;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_location_graph, container, false);

            authData = ref.getAuth();
            String authUid = authData.getUid();
            user = ref.child(authUid);
            Log.i("user", authUid);

            pieChart = (PieChart) rootView.findViewById(R.id.locationPieChart);

            pieChart.setDescription("");

            Typeface gothamMedium = Typeface.createFromAsset(getContext().getAssets(), "fonts/gothamMedium.TTF");
            Typeface gothamRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/gothamRegular.TTF");

            pieChart.setCenterText("Orders\n\nper\n\nLocation");//generateCenterText());
            pieChart.setCenterTextSize(30f);
            pieChart.setCenterTextTypeface(gothamMedium);
            pieChart.setCenterTextColor(getContext().getResources().getColor(R.color.darkgray));

            // radius of the center hole in percent of maximum radius
            pieChart.setHoleRadius(55f);
            pieChart.setTransparentCircleRadius(60f);
            pieChart.getLegend().setEnabled(false);

            pieChart.setRotationEnabled(true);

            pieChart.animateY(1400);

            generatePieData();

            return rootView;
        }


        public int[] getBarChartColors(Context context) {
            int[] barChartColors = {Color.rgb(Color.red(context.getResources().getColor(R.color.redorange)),
                    Color.green(context.getResources().getColor(R.color.redorange)),
                    Color.blue(context.getResources().getColor(R.color.redorange))),
                    Color.rgb(Color.red(context.getResources().getColor(R.color.softyellow)),
                            Color.green(context.getResources().getColor(R.color.softyellow)),
                            Color.blue(context.getResources().getColor(R.color.softyellow))),
                    Color.rgb(Color.red(context.getResources().getColor(R.color.bluegreen)),
                            Color.green(context.getResources().getColor(R.color.bluegreen)),
                            Color.blue(context.getResources().getColor(R.color.bluegreen)))};
            return barChartColors;
        }


        private void generatePieData() {


            final ArrayList<Entry> entries1 = new ArrayList<Entry>();
            final ArrayList<String> xVals = new ArrayList<String>();

            user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("inside", "of function");
                    int idx = 0;
                    for (DataSnapshot itemSnapshot : dataSnapshot.child("Devices").getChildren()) {
                        Device device1 = itemSnapshot.getValue(Device.class);
                        xVals.add(device1.Location);
                        entries1.add(new Entry(device1.numOfOrders, idx));
                        idx++;
                    }


                    Typeface gothamMedium = Typeface.createFromAsset(getContext().getAssets(), "fonts/gothamMedium.TTF");
                    Typeface gothamRegular = Typeface.createFromAsset(getContext().getAssets(), "fonts/gothamRegular.TTF");

                    PieDataSet ds1 = new PieDataSet(entries1, "Location Data");
                    ds1.setColors(getBarChartColors(getContext()));
                    ds1.setSliceSpace(2f);
                    ds1.setValueTextColor(getContext().getResources().getColor(R.color.defaultwhite));
                    ds1.setValueTypeface(gothamMedium);
                    ds1.setValueTextSize(20f);
                    ds1.setValueFormatter(new IntForPieChartFormatter());
                    d = new PieData(xVals, ds1);
                    pieChart.setData(d);

                }


                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }
}

