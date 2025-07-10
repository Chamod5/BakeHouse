package com.example.bakehouse.models;

public class Sellers {


    private String sid;
    private String sellerName;
    private String sellerPhone;
    private String sellerEmail;
    private String sellerBusinessName;
    private String sellerAddress;
    private String sellerPassword;
    private String sellerRePassword;
    private String profilepic;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return sellerName;
    }

    public void setName(String name) {
        this.sellerName = name;
    }

    public String getPhone() {
        return sellerPhone;
    }

    public void setPhone(String phone) {
        this.sellerPhone = phone;
    }

    public String getEmail() {
        return sellerEmail;
    }

    public void setEmail(String email) {
        this.sellerEmail = email;
    }

    public String getBusiness_name() {
        return sellerBusinessName;
    }

    public void setBusiness_name(String business_name) {
        this.sellerBusinessName = business_name;
    }

    public String getAddress() {
        return sellerAddress;
    }

    public void setAddress(String address) {
        this.sellerAddress = address;
    }

    public String getPassword() {
        return sellerPassword;
    }

    public void setPassword(String password) {
        this.sellerPassword = password;
    }

    public String getRe_password() {
        return sellerRePassword;
    }

    public void setRe_password(String re_password) {
        this.sellerRePassword = re_password;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public Sellers(String sellerName, String sellerPhone, String sellerEmail, String sellerBusinessName, String sellerAddress, String sellerPassword, String sellerRePassword, String profilepic){
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
        this.sellerEmail = sellerEmail;
        this.sellerBusinessName = sellerBusinessName;
        this.sellerAddress = sellerAddress;
        this.sellerPassword = sellerPassword;
        this.sellerRePassword = sellerRePassword;
        this.profilepic = profilepic;
    }

    public Sellers(){

    }
}
