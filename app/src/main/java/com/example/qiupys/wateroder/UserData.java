package com.example.qiupys.wateroder;

public class UserData {
    private String User_name;
    private String User_pwd;
    private String Phone;
    private String Address;
    private int User_id;

    public int getUser_id() {
        return User_id;
    }

    public String getUser_name() {
        return User_name;
    }

    public String getUser_pwd() {
        return User_pwd;
    }

    public void setUser_id(int user_id) {
        User_id = user_id;
    }

    public UserData(String user_name, String user_pwd){
        this.User_name=user_name;
        this.User_pwd=user_pwd;
    }
}
