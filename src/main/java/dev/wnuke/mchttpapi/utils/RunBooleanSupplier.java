package dev.wnuke.mchttpapi.utils;

import java.util.function.BooleanSupplier;

public class RunBooleanSupplier implements BooleanSupplier {
    @Override
    public boolean getAsBoolean() {
        return false;
    }
}
