package com.example.netflix_app4.model;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String name;
    private String avatar;
    private String userId;
    private String token;
    private boolean isAdmin;

    public UserInfo(String name, String avatar, String userId, String token, boolean isAdmin) {
        this.name = name;
        this.avatar = avatar;
        this.userId = userId;
        this.token = token;
        this.isAdmin = isAdmin;
    }

    // Getters
    public String getName() { return name; }
    public String getAvatar() { return avatar; }
    public String getUserId() { return userId; }
    public String getToken() { return token; }
    public boolean isAdmin() { return isAdmin; }
}