package dev.wnuke.mchttpapi.utils;

import com.google.gson.annotations.SerializedName;

public class APIPlayerStats {
    @SerializedName("Username")
    String name;
    @SerializedName("UUID")
    String uuid;
    @SerializedName("Player")
    PlayerInfo player;
    @SerializedName("Coordinates")
    Position coordinates;

    public static class PlayerInfo {
        @SerializedName("Health")
        float health;
        @SerializedName("Hunger")
        float hunger;
        @SerializedName("Saturation")
        float saturation;
    }

    public static class Position {
        @SerializedName("X")
        double x;
        @SerializedName("Y")
        double y;
        @SerializedName("Z")
        double z;

        public Position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
