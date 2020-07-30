package dev.wnuke.mchttpapi.utils;

public class APIPlayerStats {
    public String name;
    public String uuid;
    public PlayerInfo player;
    public Position coordinates;

    public static class PlayerInfo {
        public float health;
        public float hunger;
        public float saturation;
    }

    public static class Position {
        public double x;
        public double y;
        public double z;

        public Position(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
