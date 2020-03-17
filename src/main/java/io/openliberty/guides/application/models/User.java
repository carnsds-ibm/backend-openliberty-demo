package io.openliberty.guides.application.models;

public class User {
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setUserName(String user) {
        userName = user;
    }
    
    public void setPassword(String pass) {
        password = pass;
    }
}