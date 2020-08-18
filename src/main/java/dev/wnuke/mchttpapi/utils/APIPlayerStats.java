package dev.wnuke.mchttpapi.utils;

public class APIPlayerStats {
    public String name;
    public String uuid;
    public PlayerInfo player;
    public Position coordinates;

    @Override
    public String toString() {
        return "APIPlayerStats{" +
                "name='" + name + '\'' +
                ", uuid='" + uuid + '\'' +
                ", player=" + player +
                ", coordinates=" + coordinates +
                '}';
    }

    public static class PlayerInfo {
        public float health;
        public float hunger;
        public float saturation;

        @Override
        public String toString() {
            return "PlayerInfo{" +
                    "health=" + health +
                    ", hunger=" + hunger +
                    ", saturation=" + saturation +
                    '}';
        }
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

        @Override
        public String toString() {
            return "Position{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }
}
