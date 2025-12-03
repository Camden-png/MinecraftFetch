package com.camden.skriptutils;

import java.util.ArrayList;
import java.util.Arrays;
import java.time.Instant;
import java.util.List;

public class Utils {
    public static long getEpochTime() {
        return Instant.now().getEpochSecond();
    }

    public static List<String> createSplitCommaList(String input) {
        return Arrays.asList(input.split(", "));
    }

    public static List<String> createEmptyList() {
        return new ArrayList<>();
    }
}
