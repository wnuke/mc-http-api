package dev.wnuke.mchttpapi.utils

import java.util.function.BooleanSupplier

class RunBooleanSupplier : BooleanSupplier {
    override fun getAsBoolean(): Boolean {
        return false
    }
}