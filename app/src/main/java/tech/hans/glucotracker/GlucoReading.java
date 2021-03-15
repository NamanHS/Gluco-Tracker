package tech.hans.glucotracker;

import java.io.Serializable;
import java.util.Date;

public class GlucoReading implements Serializable{
    String regNo,interval,optionalNotes;
    int glucoReadingEntered;
    Date date;

    public GlucoReading(){}

    public GlucoReading(String regNo, String interval, String optionalNotes, int glucoReadingEntered, Date date){
        this.regNo = regNo;
        this.interval = interval;
        this.optionalNotes = optionalNotes;
        this.glucoReadingEntered = glucoReadingEntered;
        this.date = date;
    }

    public String getRegNo(){return regNo;}
    public String getInterval(){return interval;}
    public String getOptionalNotes(){return optionalNotes;}
    public int getGlucoReadingEntered(){return glucoReadingEntered;}
    public Date getDate(){return date;};
}
