package dev.wnuke.mchttpapi.utils;

import java.util.Objects;

public class Pair<K, V> {
    public K first;
    public V second;

    public Pair(K startFirst, V startSecond) {
        first = startFirst;
        second = startSecond;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
