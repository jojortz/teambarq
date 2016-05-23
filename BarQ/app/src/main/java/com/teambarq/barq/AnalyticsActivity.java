package com.teambarq.barq;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import android.view.Window;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;

public class AnalyticsActivity extends FragmentActivity implements ActionBar.TabListener  {
    //Fragment
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    Context context = this;

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
        mViewPager.setAdapter( mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
          //      actionBar.setSelectedNavigationItem(position);
            }
        });

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
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
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

                default:
                    // The other sections of the app are dummy placeholders.
                    Fragment fragment = new PieChartFragment();
                    return fragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Section " + (position + 1);
        }
    }

    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class BarChartFragment extends Fragment {
        private ArrayList<BarEntry> yAxisBarData = new ArrayList<>();
        private ArrayList<String> xAxisBarLabel = new ArrayList<>();
        private BarDataSet barDataSet;
        //color template for charts


        Firebase ref = new Firebase("https://barq.firebaseio.com/");
        private AuthData authData;
        private Firebase user;
        private BarChart chart;
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

                    int idx = 0;
                    for (DataSnapshot bartenderSnapshot : dataSnapshot.child("BartenderList").getChildren()) {
                        Bartender bartender = bartenderSnapshot.getValue(Bartender.class);

                        //get ave time for each bartender
                        if (bartender.totalOrdersServed != 0) {
                            float duration = new Float((float)bartender.totalDuration).longValue();
                            float aveMillis = duration / bartender.totalOrdersServed;
                            float aveSecs = aveMillis/1000;


                            BarEntry barEntry = new BarEntry(aveSecs, idx); // Jan
                            yAxisBarData.add(barEntry);

                            //add bartender to x axis array
                            xAxisBarLabel.add(bartender.getName());

                            idx++;
                        }

                        int allOrdersCount = 0;
                        float allOrdersDur = 0;


                        for (DataSnapshot orderSnapshot : dataSnapshot.child("AllOrders").getChildren()){
                            Order order = orderSnapshot.getValue(Order.class);

                            float orderDur = new Float((float) order.Duration).longValue();
                            allOrdersDur = allOrdersDur + orderDur;
                            allOrdersCount ++;

                        }

                        float aveOrderDur = (allOrdersDur/allOrdersCount)/1000;

                        Log.i("aveLine", String.valueOf(aveOrderDur));
                        line = new LimitLine(aveOrderDur);
                        line.setLineColor(getResources().getColor(R.color.darkgray));
                        line.setLineWidth(5);
                        line.enableDashedLine(20f, 10f, 0f);
                        line.setLabel(getResources().getString(R.string.bar_ave_wait));
                    }

                    barDataSet = new BarDataSet(yAxisBarData, "");


                    barDataSet.setColors(getBarChartColors(getContext()));



                    BarData data = new BarData(xAxisBarLabel, barDataSet);
                    chart.setData(data);

                    //set fonts
                    Typeface gothamExtraLight =Typeface.createFromAsset(getContext().getAssets(),"fonts/gothamExtraLight.TTF");
                    Typeface gothamRegular =Typeface.createFromAsset(getContext().getAssets(),"fonts/gothamRegular.TTF");
                    Typeface gothamMedium =Typeface.createFromAsset(getContext().getAssets(),"fonts/gothamMedium.TTF");

                    //add average value limitLine
                    YAxis yAxis = chart.getAxisLeft();
                    yAxis.addLimitLine(line);
                    yAxis.setTypeface(gothamRegular);
                    yAxis.setTextSize(20);
                    yAxis.setDrawGridLines(false);
                    yAxis.setDrawLabels(true);
                    //set yaxis labels
                    


                    XAxis xAxis = chart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setTypeface(gothamRegular);
                    xAxis.setTextSize(20);
                    xAxis.setDrawGridLines(false);


                    //format chart
                    chart.setDescription(getResources().getString(R.string.barchart_title));
                    chart.setDescriptionTypeface(gothamMedium);


                    chart.setHighlightEnabled(false);
                    chart.getAxisRight().setEnabled(false); //turn off right axis
                    chart.getLegend().setEnabled(false);
                    chart.getData().setDrawValues(false);
                    chart.setDrawGridBackground(false);
                    chart.animateY(3000);
                    chart.invalidate();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

        }

        public int[] getBarChartColors(Context context){

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
                            Color.blue(context.getResources().getColor(R.color.bluegreen)))};

            return barChartColors;
        }

    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class PieChartFragment extends Fragment {
        private PieChart pieChart;
        Firebase ref = new Firebase("https://barq.firebaseio.com/");
        private AuthData authData;
        private Firebase user;

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

          //  Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

            //pieChart.setCenterTextTypeface(tf);
            pieChart.setCenterText("TEST");//generateCenterText());
            pieChart.setCenterTextSize(10f);
            //pieChart.setCenterTextTypeface(tf);

            // radius of the center hole in percent of maximum radius
            pieChart.setHoleRadius(45f);
            pieChart.setTransparentCircleRadius(50f);

            Legend l = pieChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);

            pieChart.setData(generatePieData());

            return rootView;
        }

        private SpannableString generateCenterText() {
            SpannableString s = new SpannableString("Revenues\nQuarters 2015");
            s.setSpan(new RelativeSizeSpan(2f), 0, 8, 0);
            s.setSpan(new ForegroundColorSpan(Color.GRAY), 8, s.length(), 0);
            return s;
        }

        protected PieData generatePieData() {

            int count = 4;

            ArrayList<Entry> entries1 = new ArrayList<Entry>();
            ArrayList<String> xVals = new ArrayList<String>();

            xVals.add("Quarter 1");
            xVals.add("Quarter 2");
            xVals.add("Quarter 3");
            xVals.add("Quarter 4");

            for(int i = 0; i < count; i++) {
                xVals.add("entry" + (i+1));

                entries1.add(new Entry((float) (Math.random() * 60) + 40, i));
            }

            PieDataSet ds1 = new PieDataSet(entries1, "Quarterly Revenues 2015");
            ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            ds1.setSliceSpace(2f);
            ds1.setValueTextColor(Color.WHITE);
            ds1.setValueTextSize(12f);

            PieData d = new PieData(xVals, ds1);

            return d;
        }
    }



}
