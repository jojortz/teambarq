package com.teambarq.barq;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Order {
    String MACid;
    long TimeIn;
    //String TimeIn;
    int QueuePosition;
    public long Duration;
    List<String> Servers;

    public Order() {}

    public Order(String MACid,int QueuePosition){
        this.MACid = MACid;
//        this.Servers = Servers;
        this.QueuePosition = QueuePosition;
        //Calendar c = Calendar.getInstance();
        //SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        //this.TimeIn = "test";
        this.TimeIn = System.currentTimeMillis();
        //this.TimeIn = df.format(c.getTime());;
    }
}
