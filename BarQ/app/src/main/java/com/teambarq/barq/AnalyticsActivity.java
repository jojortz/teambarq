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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AnalyticsActivity extends FragmentActivity implements ActionBar.TabListener  {
    //Fragment
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;
    Context context = this;

    //Navigation drawer
    private DrawerLayout mDrawerLayout;
    private RecyclerView navRecyclerView;
    private RecyclerView.Adapter navAdapter;                        // Declaring Adapter For Recycler View
    private RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager

    private String drawerTitles[] = { "Analytics", "Shift Creator", "Serve","Feedback", "Help" };
    private int drawerIcons[] = {R.drawable.ic_analytics_icon,R.drawable.ic_add_person,R.drawable.ic_bar_icon, R.drawable.ic_feedback_icon, R.drawable.ic_help_icon};

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

                case 1:
                    Fragment pieChartFrag = new PieChartFragment();
                    return pieChartFrag;

                case 2:
                    Fragment lineChartFrag = new LineChartFragment();
                    return lineChartFrag;

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


    /** --------------------------------------------------------------------------------------------------------
     * Bar chart that displays average time it takes each bartender to serve customers
       -------------------------------------------------------------------------------------------------------- */


    public static class BarChartFragment extends Fragment {
        private ArrayList<BarEntry> yAxisBarData = new ArrayList<>();
        private ArrayList<String> xAxisBarLabel = new ArrayList<>();
        private BarDataSet barDataSet;

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

            //set yaxis label
            TextView yAxis = (TextView) rootView.findViewById(R.id.barchartYLabel);
            Typeface gothamRegular =Typeface.createFromAsset(getContext().getAssets(),"fonts/gothamRegular.TTF");
            Typeface gothamMedium =Typeface.createFromAsset(getContext().getAssets(),"fonts/gothamMedium.TTF");

            yAxis.setTextColor(getResources().getColor(R.color.darkgray));
            yAxis.setTypeface(gothamMedium);

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

                    int idx = 0;
                    for (DataSnapshot bartenderSnapshot : dataSnapshot.child("BartenderList").getChildren()) {
                        Bartender bartender = bartenderSnapshot.getValue(Bartender.class);

                        //get ave time for each bartender
                        if (bartender.totalOrdersServed != 0) {
                            float duration = new Float((float)bartender.totalDuration).longValue();
                            float aveMillis = duration / bartender.totalOrdersServed;
                            float aveSecs = aveMillis/1000;


                            BarEntry barEntry = new BarEntry(aveSecs, idx);
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
                        line.setLineWidth(4);
                        line.enableDashedLine(20f, 10f, 0f);
                        line.setLabel(getResources().getString(R.string.bar_ave_wait));
                        line.setTextSize(15);
                        line.setTextColor(getResources().getColor(R.color.darkgray));
                    }

                    barDataSet = new BarDataSet(yAxisBarData, "");

                    barDataSet.setColors(getBarChartColors(getContext()));

                    BarData data = new BarData(xAxisBarLabel, barDataSet);
                    chart.setData(data);

                    //set fonts
                    Typeface gothamMedium =Typeface.createFromAsset(getContext().getAssets(),"fonts/gothamMedium.TTF");
                    Typeface gothamRegular =Typeface.createFromAsset(getContext().getAssets(),"fonts/gothamRegular.TTF");

                    //add average value limitLine
                    YAxis yAxis = chart.getAxisLeft();
                    yAxis.addLimitLine(line);
                    yAxis.setTypeface(gothamMedium);
                    yAxis.setTextColor(getResources().getColor(R.color.darkgray));
                    yAxis.setTextSize(20);
                    yAxis.setDrawGridLines(false);
                    yAxis.setDrawLabels(true);

                    XAxis xAxis = chart.getXAxis();
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setTypeface(gothamMedium);
                    xAxis.setTextColor(getResources().getColor(R.color.darkgray));
                    xAxis.setTextSize(20);
                    xAxis.setDrawGridLines(false);

                    //format chart
                    chart.setDescription("");
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
                            Color.blue(context.getResources().getColor(R.color.bluegreen))),

                    Color.rgb(Color.red(context.getResources().getColor(R.color.brightpurple)),
                            Color.green(context.getResources().getColor(R.color.brightpurple)),
                            Color.blue(context.getResources().getColor(R.color.brightpurple))),

                    Color.rgb(Color.red(context.getResources().getColor(R.color.forestgreen)),
                            Color.green(context.getResources().getColor(R.color.forestgreen)),
                            Color.blue(context.getResources().getColor(R.color.forestgreen)))};

            return barChartColors;
        }

    }

    /** --------------------------------------------------------------------------------------------------------
     * Pie Chart that breaks down the percentage of orders from each location
     -------------------------------------------------------------------------------------------------------- */


    public static class PieChartFragment extends Fragment {
        private PieChart pieChart;
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

            Typeface gothamMedium =Typeface.createFromAsset(getContext().getAssets(),"fonts/gothamMedium.TTF");
            Typeface gothamRegular =Typeface.createFromAsset(getContext().getAssets(),"fonts/gothamRegular.TTF");

            pieChart.setCenterText("Orders\n\nper\n\nLocation");//generateCenterText());
            pieChart.setCenterTextSize(30f);
            pieChart.setCenterTextTypeface(gothamMedium);
            pieChart.setCenterTextColor(getContext().getResources().getColor(R.color.darkgray));

            // radius of the center hole in percent of maximum radius
            pieChart.setHoleRadius(55f);
            pieChart.setTransparentCircleRadius(60f);
            pieChart.getLegend().setEnabled(false);

            pieChart.setRotationEnabled(true);
            pieChart.setHighlightEnabled(false);

//            pieChart.animateY(1400, AnimationEasing.EasingOption.EaseInOutQuad);
            pieChart.animateY(1400);

//            pieChart.setUsePercentValues(true);

            generatePieData();

            return rootView;
        }


        public int[] getBarChartColors(Context context){
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


                    Typeface gothamMedium =Typeface.createFromAsset(getContext().getAssets(),"fonts/gothamMedium.TTF");
                    Typeface gothamRegular =Typeface.createFromAsset(getContext().getAssets(),"fonts/gothamRegular.TTF");

                    PieDataSet ds1 = new PieDataSet(entries1, "Location Data");
                    ds1.setColors(getBarChartColors(getContext()));
                    ds1.setSliceSpace(2f);
                    ds1.setValueTextColor(Color.WHITE);
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

    /** --------------------------------------------------------------------------------------------------------
     * Line Chart of average wait times for a given date.
     -------------------------------------------------------------------------------------------------------- */


    public static class LineChartFragment extends Fragment {
        private PieChart pieChart;
        Firebase ref = new Firebase("https://barq.firebaseio.com/");
        private AuthData authData;
        private Firebase user;
        private DatePicker dpResult;
        private Button selectDate;
        static final long DAY_MILLIS = 86400000;
        static final long FOUR_HRS_MILLIS = 14400000;
        private ArrayList<Entry> yAxisLineData = new ArrayList<>();
        private ArrayList<String> xAxisLineData = new ArrayList<>();
        private LineDataSet lineDataSet;
        private LineChart lineChart;

        private DataPoint[] graphDataPoints;
        private LineGraphSeries<DataPoint> line_series = new LineGraphSeries<>();
        private DataPoint[] lineGraphData;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);

            authData = ref.getAuth();
            String authUid = authData.getUid();
            user = ref.child(authUid);
            Log.i("user", authUid);

            selectDate = (Button) rootView.findViewById(R.id.selectDateButton);
            dpResult = (DatePicker) rootView.findViewById(R.id.datePicker);

            lineChart = (LineChart) rootView.findViewById(R.id.lineChart);

            selectDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int day = dpResult.getDayOfMonth();
                    int month = dpResult.getMonth() + 1;
                    int year = dpResult.getYear();

//                    Log.i("day", String.valueOf(day));
//                    Log.i("month", String.valueOf(month));
//                    Log.i("year", String.valueOf(year));

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(dpResult.getYear(), dpResult.getMonth(), dpResult.getDayOfMonth(), 0, 0, 0);
//                    long dateStartMillis = calendar.getTimeInMillis() - FOUR_HRS_MILLIS;
//                    long dateEndMillis = dateStartMillis + FOUR_HRS_MILLIS + FOUR_HRS_MILLIS;
                    long dateStartMillis = 1463975177763L;
                    long dateEndMillis = 1463984295601L;

                    Log.i("dateStartMillis", String.valueOf(dateStartMillis));
                    Log.i("dateEndMillis", String.valueOf(dateEndMillis));


//                    Log.i("dateMillis", String.valueOf(dateStartMillis));

                    Query query2 = user.child("AllOrders").orderByChild("TimeIn").startAt(dateStartMillis).
                            endAt(dateEndMillis);
                    query2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i("Count " ,""+ dataSnapshot.getChildrenCount());

                            int idx = 0;
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                                Order order = postSnapshot.getValue(Order.class);
                                Log.i("TimeIn", String.valueOf(order.TimeIn));
                                Log.i("Duration", String.valueOf(order.Duration));

//                                //format label
//                                long timeInMillis = order.Duration;
//                                long hours = TimeUnit.MILLISECONDS.toHours(timeInMillis);
//                                timeInMillis -= TimeUnit.HOURS.toMillis(hours);
//                                long minutes = TimeUnit.MILLISECONDS.toMinutes(timeInMillis);
//                                StringBuilder sb = new StringBuilder();
//                                sb.append(hours);
//                                sb.append(":");
//                                sb.append(minutes);
//                                String formattedTimeIn = String.format("%02d:%02d", hours, minutes);


                                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd hh:mm:ss");

                                // Create a calendar object that will convert the date and time value in milliseconds to date.
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeInMillis(order.TimeIn);
                                String formattedTimeIn = formatter.format(calendar.getTime());

                                xAxisLineData.add(formattedTimeIn);
                                yAxisLineData.add(new Entry(order.Duration/1000, idx));


                                //line_series = new LineGraphSeries<DataPoint>(new DataPoint[] {new DataPoint(0,2)});
                                line_series.appendData(new DataPoint(order.TimeIn, order.Duration/1000),true,100);



                                idx++;
                            }

                            //DataSnapshot dinosaur = dataSnapshot.getChildren().iterator().next();
                            //Order newOrder = dinosaur.getValue(Order.class);
                            //create array from data
                            //Order order = dataSnapshot.getValue(Order.class);
                            //float orderTimeIn = new Float((float) order.TimeIn).longValue();
                            //String orderTimeIn = (String) dataSnapshot.getValue();
                            //Log.i("orderTimeIn", String.valueOf(newOrder.TimeIn) );
                            //Log.i("orderTimeIn", String.valueOf(dataSnapshot.getKey()));

//                            lineDataSet = new LineDataSet(yAxisLineData, "");
//                            LineData data = new LineData(xAxisLineData,lineDataSet);
//                            lineChart.setData(data);
//                            lineChart.invalidate();
                            //line_series = new LineGraphSeries<DataPoint>(new DataPoint[]{graphDataPoints});
                            GraphView line_graph = (GraphView) rootView.findViewById(R.id.lineGraph);
                            line_graph.addSeries(line_series);
                            line_graph.invalidate();
                        }


                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                    }
                });



            return rootView;
        }
    }


}
