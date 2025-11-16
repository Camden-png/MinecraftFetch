package com.camden.skriptutils;

import java.time.Instant;

public class Utils {
    public static long getEpoch() {
        return Instant.now().getEpochSecond();
    }
}
