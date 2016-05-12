package com.teambarq.barq;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Order {
    String MACid;
    String TimeIn;
    int QueuePosition;
    public float Duration;
    List<String> Servers;

    public Order() {}

    public Order(String MACid,List<String> Servers,int QueuePosition){
        this.MACid = MACid;
        this.Servers = Servers;
        this.QueuePosition = QueuePosition;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        this.TimeIn = df.format(c.getTime());;
    }
}
