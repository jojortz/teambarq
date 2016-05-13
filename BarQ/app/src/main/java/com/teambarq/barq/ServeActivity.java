package com.teambarq.barq;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Handler;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.skyfishjy.library.RippleBackground;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServeActivity extends AppCompatActivity {

    private long topQueueTime = 0; //initialize
    Button servedButton;
    TextView upNextTimer;
    Context context = this;
    Firebase ref = new Firebase("https://barq.firebaseio.com/");
    ArrayList<Device> devices;
    int pos = 0;
    ArrayList<Device> orderHistory;
    ListView l1;
    OrderAdapter adapter;

    private static String DEVICEMAC1 = "5ccf7f0fd6e4"; //center, green
    private static String DEVICEMAC2 = "5ccf7f006c6c"; //left, red
    private static String DEVICEMAC3 = "18fe34d45db8"; //right, blue


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serve);

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
                    ref.child("Bar1").child("RunningQueue").child(DEVICEMAC1).removeValue();
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
                    ref.child("Bar1").child("RunningQueue").child(DEVICEMAC2).removeValue();
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
                    ref.child("Bar1").child("RunningQueue").child(DEVICEMAC3).removeValue();
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

        //update clock
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {

                if (topQueueTime == 0){
                    upNextTimer.setText("Waiting for Order");
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



        devices = new ArrayList<Device>();
        orderHistory = new ArrayList<Device>();
//        Firebase newPostRef = ref.child("Bar1").child("Devices");
//        newPostRef.push().setValue(new Device("D1","Blue","Center"));
//        newPostRef.push().setValue(new Device("D2","Red","Left"));
//        newPostRef.push().setValue(new Device("D3","Green","Right"));

        ref.child("Bar1").child("Devices").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Device device1 = postSnapshot.getValue(Device.class);
                    devices.add(device1);
//                    Log.e("FB", "Adding device data now");
                }
                loadedDevices();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
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

    protected void loadedDevices()
    {
        Log.e("FB","All devices added");
        Firebase ref2 = ref.child("Bar1").child("RunningQueue");
        Query queryRef2 = ref2.orderByChild("QueuePosition");
        queryRef2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {
                String MACid = snapshot.getKey();
                Device currentDevice;
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
                    orderHistory.add(currentDevice);
                    adapter.notifyDataSetChanged();

                    // update top of queue time for top of the list
                    if (order1.QueuePosition == 1){
                        topQueueTime = order1.TimeIn;
                    }

                }
                else
                {
//                    Log.e("FB", "Adding new device found");
//                    Log.e("FB", MACid);
                    //Get current Servers
                    List<String> Servers = new ArrayList<String>();
                    Servers.add("Server1");
                    Servers.add("Server2");
                    pos = pos + 1;
                    Log.e("FB", Integer.toString(pos));
                    Order newOrder = new Order(MACid, Servers, pos);
                    Firebase newPostRef = ref.child("Bar1").child("RunningQueue").child(MACid);
                    newPostRef.setValue(newOrder);
                    currentDevice = devices.get(0);
                    for (int i = 0; i < devices.size(); i++) {
                        if (devices.get(i).MACid.equals(MACid)) {
                            currentDevice = devices.get(i);
                            break;
                        }
                    }
                    orderHistory.add(currentDevice);
                    adapter.notifyDataSetChanged();

                    // update top of queue time
                    if (newOrder.QueuePosition == 1){
                        topQueueTime =  newOrder.TimeIn;
                    }

                    //update view by adding location circle
                    addLocationCircle(MACid);




                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String MACid = dataSnapshot.getKey();

                //update view by removing location circle
                removeLocationCircle(MACid);

                Device currentDevice;
                long currentPosition = (long) dataSnapshot.child("QueuePosition").getValue();
//                Log.e("FB", "Deleting");
//                Log.e("FB", Long.toString(currentPosition));
                currentDevice = devices.get(0);
                for (int i = 0; i < devices.size(); i++) {
                    if (devices.get(i).MACid.equals(MACid)) {
                        currentDevice = devices.get(i);
                        break;
                    }
                }


                Query queryRef2 = ref.child("Bar1").child("RunningQueue").orderByChild("QueuePosition").startAt(currentPosition);
                queryRef2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                            Order order2 = itemSnapshot.getValue(Order.class);

                            // update top of queue time (do here to avoid latancy issues checking for new pos 1)
                            if (order2.QueuePosition == 2){
                                topQueueTime = order2.TimeIn;
                                Log.i("FB", String.valueOf(topQueueTime));
                            }

                            int newPosition = order2.QueuePosition - 1;
                            ref.child("Bar1").child("RunningQueue").child(itemSnapshot.getKey()).child("QueuePosition").setValue(newPosition);
                            Log.i("FB", "Updating queue positions in FB");


                        }
                        pos = pos - 1;


                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
                adapter.remove(currentDevice);


                adapter.notifyDataSetChanged();
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

        //Check to see if any items in running queue and set topQueueTime to null if not
        ref.child("Bar1").child("RunningQueue").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //set time to null becuase no items in queue
                topQueueTime = 0;
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

                adapter = new OrderAdapter(this, R.layout.listview_item_row, orderHistory);

        l1 = (ListView) findViewById(R.id.OrderList);

        if (l1 != null) {
            l1.setAdapter(adapter);
        }

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
}