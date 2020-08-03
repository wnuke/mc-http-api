package dev.wnuke.mchttpapi.utils;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {
    @SerializedName("message")
    public String message;

    @Override
    public String toString() {
        return "ChatMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
