package com.teambarq.barq;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ServeActivity extends AppCompatActivity {

    private long topQueueTime;
    Button servedButton;
    TextView upNextTimer;
    Context context = this;
    Firebase ref = new Firebase("https://barq.firebaseio.com/");
    ArrayList<Device> devices;
    int pos = 0;
    ArrayList<Device> orderHistory;
    ListView l1;
    OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serve);

        //ensure screen doesn't go to sleep during activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //set imageView of bar layout
        ImageView barLayoutView = (ImageView) findViewById(R.id.barLayoutImage);
        Picasso.with(context).load(R.drawable.barlayout1).into(barLayoutView);


        //set current time
        topQueueTime = System.currentTimeMillis();

        servedButton = (Button) findViewById(R.id.servedButton);

        //imitate item getting removed from queue
        servedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                topQueueTime = System.currentTimeMillis();
            }
        });

        final Handler timerHandler = new Handler();
        upNextTimer = (TextView) findViewById(R.id.upNextTimerView);


        Runnable timerRunnable = new Runnable() {
            @Override
            public void run() {
                //action
                long deltaTime = System.currentTimeMillis() - topQueueTime;
                String formattedTime = getFormattedTime(deltaTime);
                upNextTimer.setText(formattedTime);
                //sets handler to run every 100 ms
                timerHandler.postDelayed(this, 100);
            }
        };

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

        String formattedTime = String.format("%d:%02d",
                minutes, seconds);

        //return(sb.toString());

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
//                    Log.e("FB", "Loading previous data");
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
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String MACid = dataSnapshot.getKey();
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
                            int newPosition = order2.QueuePosition - 1;
                            ref.child("Bar1").child("RunningQueue").child(itemSnapshot.getKey()).child("QueuePosition").setValue(newPosition);
//                            Log.e("FB", "Updating queue positions in FB");
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

        adapter = new OrderAdapter(this, R.layout.listview_item_row, orderHistory);

        l1 = (ListView) findViewById(R.id.OrderList);

        if (l1 != null) {
            l1.setAdapter(adapter);
        }

    }
}