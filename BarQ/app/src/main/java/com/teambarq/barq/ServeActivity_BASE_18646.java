package com.teambarq.barq;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

public class ServeActivity extends AppCompatActivity {

    private long topQueueTime;
    Button servedButton;
    TextView upNextTimer;
    Context context = this;

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
}