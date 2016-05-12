package com.teambarq.barq;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import com.squareup.picasso.Picasso;
import com.skyfishjy.library.RippleBackground;

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
        Picasso.with(context).load(R.drawable.barlayoutublank).into(barLayoutView);

        //set current time
        topQueueTime = System.currentTimeMillis();

        //TODO: pull number of device from firbase
        int numDevices = 3;

        //TODO get screen size and set circle size and location (probably store in firebase

        //RelativeLayout serveRL = (RelativeLayout) findViewById(R.id.serveActivityRL);
        //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50,60);

        //customize location circles... probably won't do
        if (numDevices == 3){

//            //initialize location circles
//            Resources res = getResources();
//            final Drawable drawable1 = res.getDrawable(R.drawable.locationcircle1);
//            drawable1.setColorFilter(res.getColor(R.color.lightBlue), PorterDuff.Mode.SRC_ATOP);
//            ImageView img1 = (ImageView) findViewById(R.id.cirImage1);
//            img1.setBackgroundDrawable(drawable1);


//            img1.setX(300);
//            img1.setY(500);
//
//            final Drawable drawable2 = res.getDrawable(R.drawable.locationcircle1);
//            drawable2.setColorFilter(res.getColor(R.color.carribGreen), PorterDuff.Mode.SRC_ATOP);
//            ImageView img2 = (ImageView) findViewById(R.id.cirImage2);
//            img2.setBackgroundDrawable(drawable2);
//
//            img2.setX(500);
//            img2.setY(600);
//
//            final Drawable drawable3 = res.getDrawable(R.drawable.locationcircle1);
//            drawable3.setColorFilter(res.getColor(R.color.lipRed), PorterDuff.Mode.SRC_ATOP);
//            ImageView img3 = (ImageView) findViewById(R.id.cirImage3);
//            img3.setBackgroundDrawable(drawable3);
//
//            img3.setX(500);
//            img3.setY(700);

            //params.leftMargin = 50;
           // params.rightMargin = 60;

            //serveRL.addView(img, params);
            //ContentFrameLayout content1 = (ContentFrameLayout)findViewById(R.id.contentCir1);


        }

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

        //update clock
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

        //initialize update clock
        timerHandler.postDelayed(timerRunnable, 100);



        final RippleBackground rippleBackground1 =(RippleBackground)findViewById(R.id.contentCir1);
        ImageView cir1ImageView=(ImageView)findViewById(R.id.cirImage1);
        cir1ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground1.startRippleAnimation();
            }
        });

        final RippleBackground rippleBackground2 =(RippleBackground)findViewById(R.id.contentCir2);
        ImageView cir2ImageView=(ImageView)findViewById(R.id.cirImage2);
        cir2ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground2.startRippleAnimation();
            }
        });

        final RippleBackground rippleBackground3 =(RippleBackground)findViewById(R.id.contentCir3);
        ImageView cir3ImageView=(ImageView)findViewById(R.id.cirImage3);
        cir3ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground3.startRippleAnimation();
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
}