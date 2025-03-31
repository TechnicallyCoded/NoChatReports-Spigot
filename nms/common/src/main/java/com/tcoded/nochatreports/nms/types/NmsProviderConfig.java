package com.tcoded.nochatreports.nms.types;

import java.util.function.Supplier;

public record NmsProviderConfig(boolean isPaper, Supplier<Boolean> debugStateSupplier) {
}
