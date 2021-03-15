package tech.hans.glucotracker;

public class Articles {

    String title, url;

    public Articles(){}

    public Articles(String title,String url){
        this.title = title;
        this.url = url;
    }

    public String getTitle(){ return title; }

    public String getUrl(){ return url;}

}
