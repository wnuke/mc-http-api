package dev.wnuke.mchttpapi.utils;

import com.google.gson.annotations.SerializedName;

public class ServerConnect {
    @SerializedName("address")
    public String address;
    @SerializedName("port")
    public Integer port;

    public void fixUP() {
        if (port == null) port = 25565;
        if (address == null) address = "localhost";
    }

    @Override
    public String toString() {
        return "ServerConnect{" +
                "address='" + address + '\'' +
                ", port=" + port +
                '}';
    }
}
