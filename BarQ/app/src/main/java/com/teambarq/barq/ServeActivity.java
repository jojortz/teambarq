package com.teambarq.barq;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ServeActivity extends AppCompatActivity {
    private long topQueueTime = 0; //initialize
    private TextView upNextTimer, upNextHeader;
    Context context = this;
    Firebase ref = new Firebase("https://barq.firebaseio.com/");
    private AuthData authData;
    private Firebase user;
    private ArrayList<Device> devices;
    private ArrayList<Device> orderHistory;
    private ArrayList<String> currentServers;
    int pos = 0;
    SwipeAdapter mAdapter;
    private static String DEVICEMAC2 = "5ccf7f0fd6e4"; //center, yellow
    private static String DEVICEMAC1 = "5ccf7f006c6c"; //left, red
    private static String DEVICEMAC3 = "18fe34d460aa"; //right, blue

    private Button endShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serve);

        //Initializing Firebase database for HistoryActivity
        authData = ref.getAuth();
        String authUid = authData.getUid();
        //13d48831-3ce1-45bd-bd99-d4fb13a9106c

        //TODO Add end shift button
        Log.d("AuthUID", authUid);
        user = ref.child(authUid);

        //ensure screen doesn't go to sleep during activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //set imageView of bar layout
        ImageView barLayoutView = (ImageView) findViewById(R.id.barLayoutImage);
        Picasso.with(context).load(R.drawable.barlayoutublank).into(barLayoutView);

        //initialize location circles
        Resources res = getResources();
        final Drawable drawable1 = res.getDrawable(R.drawable.locationcircle1);
        ImageView cir1ImageView = (ImageView) findViewById(R.id.cirImage1);
        cir1ImageView.setBackgroundDrawable(drawable1);
        cir1ImageView.setVisibility(View.INVISIBLE);
        final Drawable drawable2 = res.getDrawable(R.drawable.locationcircle2);
        ImageView cir2ImageView = (ImageView) findViewById(R.id.cirImage2);
        cir2ImageView.setBackgroundDrawable(drawable2);
        cir2ImageView.setVisibility(View.INVISIBLE);
        final Drawable drawable3 = res.getDrawable(R.drawable.locationcircle3);
        ImageView cir3ImageView = (ImageView) findViewById(R.id.cirImage3);
        cir3ImageView.setBackgroundDrawable(drawable3);
        cir3ImageView.setVisibility(View.INVISIBLE);
        //Set listeners for double tap to delete
        cir1ImageView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.d("TEST", "onDoubleTap");
                    removeLocationCircle(DEVICEMAC1);
//                    user.child("RunningQueue").child(DEVICEMAC1).removeValue();
                    addToAllOrdersAndDelete(DEVICEMAC1);
                    return super.onDoubleTap(e);
                }
            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        cir2ImageView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.d("TEST", "onDoubleTap");
                    removeLocationCircle(DEVICEMAC2);
//                    user.child("RunningQueue").child(DEVICEMAC2).removeValue();
                    addToAllOrdersAndDelete(DEVICEMAC2);
                    return super.onDoubleTap(e);
                }
            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        cir3ImageView.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    Log.d("TEST", "onDoubleTap");
                    removeLocationCircle(DEVICEMAC3);
//                    user.child("RunningQueue").child(DEVICEMAC3).removeValue();
                    addToAllOrdersAndDelete(DEVICEMAC3);
                    return super.onDoubleTap(e);
                }
            });
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TEST", "Raw event: " + event.getAction() + ", (" + event.getRawX() + ", " + event.getRawY() + ")");
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
        //create handler for up next time
        final Handler timerHandler = new Handler();
        upNextTimer = (TextView) findViewById(R.id.upNextTimerView);
        upNextHeader = (TextView) findViewById(R.id.upNextTextView);
        //set fonts
        Typeface gothamMedium =Typeface.createFromAsset(getAssets(),"fonts/gothamMedium.TTF");
        Typeface gothamBold =Typeface.createFromAsset(getAssets(),"fonts/gothamBold.TTF");
        Typeface gothamRegular =Typeface.createFromAsset(getAssets(),"fonts/gothamRegular.TTF");
        upNextTimer.setTypeface(gothamMedium);
        upNextHeader.setTypeface(gothamRegular);
        //update clock
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (topQueueTime == 0){
                    upNextTimer.setText(R.string.waitingTimer);
                } else {
                    long deltaTime = System.currentTimeMillis() - topQueueTime;
                    String formattedTime = getFormattedTime(deltaTime);
                    upNextTimer.setText(formattedTime);
                }
                //sets handler to run every 100 ms
                timerHandler.postDelayed(this, 100);
            }
        };
        //initialize update clock
        timerHandler.postDelayed(timerRunnable, 100);
        //initialize arrays for holding devices and list
        devices = new ArrayList<Device>();
        orderHistory = new ArrayList<Device>();
        currentServers = new ArrayList<String>();
        //Adding a new device in firebase
//        Firebase newPostRef = user.child("Devices");
//        newPostRef.push().setValue(new Device("D1","Blue","Center"));
//        newPostRef.push().setValue(new Device("D2","Red","Left"));
//        newPostRef.push().setValue(new Device("D3","Green","Right"));
        //Loading the devices for the bar from firebase
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.child("Devices").getChildren()) {
                    Device device1 = postSnapshot.getValue(Device.class);
                    devices.add(device1);
                    Log.e("FB", "Adding device data now");
                }

                for (DataSnapshot postSnapshot : snapshot.child("ActiveBartenderList").getChildren()) {
                    String s1 = (String) postSnapshot.getValue();
                    currentServers.add(s1);
//                    Log.e("FB", "Adding device data now");
                }
                //Implement RunningQueue functionality once the devices are loaded
                loadedDevices();
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        endShift = (Button) findViewById(R.id.endShift_Button);

        endShift.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Remove data from firebase
                user.child("RunningQueue").removeValue();
                user.child("ActiveBartenderList").removeValue();

                //Launch Shift Activity
                Intent intent = new Intent(ServeActivity.this, ShiftActivity.class);
                startActivity(intent);
                finish();
            }

        });


    }
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    //format millis into minutes and seconds
    public static String getFormattedTime(long millis)
    {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        StringBuilder sb = new StringBuilder();
        sb.append(minutes);
        sb.append(":");
        sb.append(seconds);
        String formattedTime = String.format("%d:%02d", minutes, seconds);
        return (formattedTime);
    }
    //Implement RunningQueue fucntionality once the devices are loaded
    protected void loadedDevices()
    {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.swipe_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new SwipeAdapter(orderHistory);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
//        Log.e("FB","All devices added");
        Query query1 = user.child("RunningQueue").orderByChild("QueuePosition");
        query1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
                String MACid = snapshot.getKey();
                Device currentDevice;
                //Load data initially if anything in RunningQueue
                if (snapshot.child("TimeIn").getValue()!= null) {
//                  Log.e("FB", "Loading previous data");
                    Order order1 = snapshot.getValue(Order.class);
                    if (pos < order1.QueuePosition) {
                        pos = order1.QueuePosition;
                    }
                    currentDevice = devices.get(0);
                    for (int i = 0; i < devices.size(); i++) {
                        if (devices.get(i).MACid.equals(MACid)) {
                            currentDevice = devices.get(i);
                            break;
                        }
                    }
                    mAdapter.addItem(currentDevice);

                    // update top of queue time for top of the list
                    if (order1.QueuePosition == 1){
                        topQueueTime = order1.TimeIn;
                    }
                }
                //A new embedded device has been added; add it to the list
                else
                {
//                    Log.e("FB", "Adding new device found");
//                    Log.e("FB", MACid);
                    pos = pos + 1;
//                    Log.e("FB", Integer.toString(pos));
                    Order newOrder = new Order(MACid, pos);
                    user.child("RunningQueue").child(MACid).setValue(newOrder);
                    currentDevice = devices.get(0);
                    for (int i = 0; i < devices.size(); i++) {
                        if (devices.get(i).MACid.equals(MACid)) {
                            currentDevice = devices.get(i);
                            break;
                        }
                    }
                    mAdapter.addItem(currentDevice);

                    // update top of queue time
                    if (newOrder.QueuePosition == 1){
                        topQueueTime =  newOrder.TimeIn;
                    }
                    //update view by adding location circle
                    addLocationCircle(MACid);
                }
            }
            //Handle the case when either order was deleted by the embedded device or deleted/server using the app
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String MACid = dataSnapshot.getKey();

                //update view by removing location circle
                removeLocationCircle(MACid);
                long currentPosition = (long) dataSnapshot.child("QueuePosition").getValue();
//                Log.e("FB", "Deleting");
//                Log.e("FB", Long.toString(currentPosition));
                Query query2 = user.child("RunningQueue").orderByChild("QueuePosition").startAt(currentPosition);
                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                            Order order2 = itemSnapshot.getValue(Order.class);

                            // update top of queue time (do here to avoid latancy issues checking for new pos 1)
                            if (order2.QueuePosition == 2){
                                topQueueTime = order2.TimeIn;
//                                Log.i("FB", String.valueOf(topQueueTime));
                            }
                            int newPosition = order2.QueuePosition - 1;
                            user.child("RunningQueue").child(itemSnapshot.getKey()).child("QueuePosition").setValue(newPosition);
//                            Log.e("FB", "Updating queue positions in FB");
                        }
                        pos = pos - 1;
                    }
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
                mAdapter.deleteItem((int)(currentPosition-1));
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildKey) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        //dataevent listener
        //Check to see if any items in running queue and set topQueueTime to null if not
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("RunningQueue").getValue()==null){
                    //set time to null because no items in queue
                    topQueueTime = 0;
//                    Log.e("FB", "RunningQueue is empty");
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        mAdapter.setOnItemClickListener(new SwipeAdapter.OnItemClickListener() {
            @Override
            public void onItemServed(int position) {
                final Device d1 = mAdapter.getItem(position);
                addToAllOrdersAndDelete(d1.MACid);
                position=position+1;
                //Toast.makeText(ServeActivity.this, "Item served at position :" + position, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onItemDeleteAction(int position) {
                Device device1 = mAdapter.getItem(position);
                user.child("RunningQueue").child(device1.MACid).setValue(null);
                position=position+1;
                Toast.makeText(ServeActivity.this, "Item deleted at position" + position, Toast.LENGTH_LONG).show();
//                Log.e("FB", "Deleted from FB");
            }
        });


    }
    //function to add location circles base on MAC id
    private void addLocationCircle(String MACid){
        ImageView img1 = (ImageView) findViewById(R.id.cirImage1); //center, green
        ImageView img2 = (ImageView) findViewById(R.id.cirImage2); //left, red
        ImageView img3 = (ImageView) findViewById(R.id.cirImage3); //right, blue
        if (MACid.equals(DEVICEMAC1)) {
            img1.setVisibility(View.VISIBLE);
            final RippleBackground rippleBackground1 = (RippleBackground) findViewById(R.id.contentCir1);
            rippleBackground1.startRippleAnimation();
        }
        else if (MACid.equals(DEVICEMAC2)) {
            img2.setVisibility(View.VISIBLE);
            final RippleBackground rippleBackground2 = (RippleBackground) findViewById(R.id.contentCir2);
            rippleBackground2.startRippleAnimation();
        }
        else if (MACid.equals(DEVICEMAC3)) {
            img3.setVisibility(View.VISIBLE);
            final RippleBackground rippleBackground3 = (RippleBackground) findViewById(R.id.contentCir3);
            rippleBackground3.startRippleAnimation();
        }
    }
    //function to remove location circles base on MAC id
    private void removeLocationCircle(String MACid){
        ImageView img1 = (ImageView) findViewById(R.id.cirImage1); //center, green
        ImageView img2 = (ImageView) findViewById(R.id.cirImage2); //left, red
        ImageView img3 = (ImageView) findViewById(R.id.cirImage3); //right, blue
        if (MACid.equals(DEVICEMAC1)) {
            img1.setVisibility(View.INVISIBLE);
            final RippleBackground rippleBackground1 = (RippleBackground) findViewById(R.id.contentCir1);
            rippleBackground1.stopRippleAnimation();
        }
        else if (MACid.equals(DEVICEMAC2)) {
            img2.setVisibility(View.INVISIBLE);
            final RippleBackground rippleBackground2 = (RippleBackground) findViewById(R.id.contentCir2);
            rippleBackground2.stopRippleAnimation();
        }
        else if (MACid.equals(DEVICEMAC3)) {
            img3.setVisibility(View.INVISIBLE);
            final RippleBackground rippleBackground3 = (RippleBackground) findViewById(R.id.contentCir3);
            rippleBackground3.stopRippleAnimation();
        }
    }

    private void addToAllOrdersAndDelete(final String MACid){
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Order order1 = snapshot.child("RunningQueue").child(MACid).getValue(Order.class);
                long deltaTime = System.currentTimeMillis() - topQueueTime;
                order1.Duration = deltaTime;
                order1.Servers = currentServers;
                user.child("AllOrders").push().setValue(order1);
                for (int i=0;i<currentServers.size();i++){
                    long totalOrders = (long) snapshot.child("BartenderList").child(currentServers.get(i)).child("totalOrdersServed").getValue();
                    long totalTime = (long) snapshot.child("BartenderList").child(currentServers.get(i)).child("totalDuration").getValue();
                    totalOrders = totalOrders+1;
                    totalTime = totalTime + deltaTime;
                    user.child("BartenderList").child(currentServers.get(i)).child("totalOrdersServed").setValue(totalOrders);
                    user.child("BartenderList").child(currentServers.get(i)).child("totalDuration").setValue(totalTime);
                }
                user.child("RunningQueue").child(MACid).removeValue();

                String device = "";
                for (DataSnapshot itemSnapshot : snapshot.child("Devices").getChildren()) {
                    Device device1 = itemSnapshot.getValue(Device.class);
                    device = itemSnapshot.getKey();

                    if (device1.MACid.equals(MACid)) {
                        break;
                    }
                }
                long totalOrders = (long) snapshot.child("Devices").child(device).child("numOfOrders").getValue();
                totalOrders = totalOrders + 1 ;
                user.child("Devices").child(device).child("numOfOrders").setValue(totalOrders);
//                        Log.e("FB", "Added to AllOrders");
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

}