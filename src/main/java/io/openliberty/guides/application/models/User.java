package io.openliberty.guides.application.models;

import io.openliberty.guides.application.util.UserManager;

public class User {
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return new String(UserManager.DECODER.decode(password));
    }

    public void setUserName(String user) {
        userName = user;
    }
    
    public void setPassword(String pass) {
        password = pass;
    }
}