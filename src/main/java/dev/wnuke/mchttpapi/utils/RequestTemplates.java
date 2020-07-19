package dev.wnuke.mchttpapi.utils;

import com.google.gson.annotations.SerializedName;

public class RequestTemplates {
    public static class ServerConnect {
        @SerializedName("address")
        public String address;
        @SerializedName("port")
        public Integer port;

        public void fixUP() {
            if (port == null) port = 25565;
            if (address == null) address = "localhost";
        }
    }

    public static class ChatMessage {
        @SerializedName("message")
        public String message;
    }

    public static class Login {
        @SerializedName("username")
        public String username;
        @SerializedName("password")
        public String password;
    }
}
