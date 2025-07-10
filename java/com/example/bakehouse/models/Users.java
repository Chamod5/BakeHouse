package com.example.bakehouse.models;

public class Users {

    private String name;
    private String username;
    private String email;
    private String password;
    private String re_password;
    private String profilepic;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRe_password() {
        return re_password;
    }

    public void setRe_password(String re_password) {
        this.re_password = re_password;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public Users(String name, String username, String email, String password, String re_password, String profilepic) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.re_password = re_password;
        this.profilepic = profilepic;
    }

    public Users(){

    }


}


