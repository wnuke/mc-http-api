package dev.wnuke.mchttpapi.utils;

import com.google.gson.annotations.SerializedName;

public class APIPlayerStats {
    @SerializedName("Username")
    public String name;
    @SerializedName("UUID")
    public String uuid;
    @SerializedName("Player")
    public PlayerInfo player;
    @SerializedName("Coordinates")
    public Position coordinates;

    public static class PlayerInfo {
        @SerializedName("Health")
        public float health;
        @SerializedName("Hunger")
        public float hunger;
        @SerializedName("Saturation")
        public float saturation;
    }

    public static class Position {
        @SerializedName("X")
        public double x;
        @SerializedName("Y")
        public double y;
        @SerializedName("Z")
        public double z;

        public Position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
