package com.tcoded.nochatreports.nms.types;

public record PacketWriteResult<T>(boolean keep, T packet) {
}
