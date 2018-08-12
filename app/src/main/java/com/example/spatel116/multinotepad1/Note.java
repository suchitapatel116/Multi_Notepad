package com.example.spatel116.multinotepad1;

import java.io.Serializable;

/**
 * Created by spatel116 on 2/7/2018.
 */

public class Note implements Serializable {

    private String title;
    private String description;
    private String dateTime;

    private static int counter = 1;

    public Note()
    {
        //this.title = "Note : " + counter;
        //this.description = "Description";
        //this.dateTime = "" + System.currentTimeMillis();
        counter++;
    }

    public String getTitle() {
        return title;
    }

    public static int getCounter() {
        return counter;
    }
    public String getDescription() {
        return description;
    }
    public String getDateTime() {
        return dateTime;
    }

    public void setTitle(String t) {
        this.title = t;
    }
    public void setDescription(String descr) {
        this.description = descr;
    }
    public void setDateTime(String dt) {
        this.dateTime = dt;
    }

    public static void decrementCounter() {
        counter--;
    }

    @Override
    public String toString() {
        return "Note{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }
}
