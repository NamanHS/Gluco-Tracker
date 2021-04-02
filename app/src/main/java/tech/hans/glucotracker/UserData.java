package tech.hans.glucotracker;

import java.util.Date;

public class UserData {
    private String name,email,doc_email,fam_email;
    private Long dob;

    public UserData(){}

    public UserData(String name, Long dob, String email, String doc_email, String fam_email){
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.doc_email = doc_email;
        this.fam_email = fam_email;
    }

    public String getName(){
        return name;
    }

    public Long getDob(){
        return dob;
    }

    public String getEmail(){
        return email;
    }

    public String getDoc_email(){
        return doc_email;
    }

    public String getFam_email(){
        return fam_email;
    }


}
