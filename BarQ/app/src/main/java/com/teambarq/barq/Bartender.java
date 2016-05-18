package com.teambarq.barq;

/**
 * Created by jojortz on 5/8/2016.
 */
public class Bartender {
    String name;
    String profilePic;
    String id;

    public Bartender(){
    }

    public Bartender(String name,String profilePic){
        this.name = name;
        this.profilePic = profilePic;
    }
    public String getName(){
        return this.name;
    }

    public String profilePic(){
        return this.profilePic;
    }

    public String getId(){
        return this.id;
    }
}
