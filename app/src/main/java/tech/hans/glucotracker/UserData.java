package tech.hans.glucotracker;

public class UserData {
    private String name,email,doc_email,fam_email;

    public UserData(){}

    public UserData(String name, String email, String doc_email, String fam_email){
        this.name = name;
        this.email = email;
        this.doc_email = doc_email;
        this.fam_email = fam_email;
    }

    public String getName(){
        return name;
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
