package dev.wnuke.mchttpapi.utils;

import com.google.gson.annotations.SerializedName;


public class Login {
    @SerializedName("username")
    public String username;
    @SerializedName("password")
    public String password;

    @Override
    public String toString() {
        return "Login{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

