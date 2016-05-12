package com.teambarq.barq;

public class Device {
    String MACid;
    String Color;
    String Location;

    public Device() {}

    public Device(String MACid,String Color, String Location){
        this.MACid = MACid;
        this.Color = Color;
        this.Location = Location;
    }
}
