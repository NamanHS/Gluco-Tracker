package tech.hans.glucotracker;

import java.util.Date;

public class Articles {

    String title, url;
    Date date;

    public Articles(){}

    public Articles(String title,String url, Date date){
        this.title = title;
        this.url = url;
    }

    public String getTitle(){ return title; }

    public String getUrl(){ return url;}

    public Date getDate(){ return date;}



}
