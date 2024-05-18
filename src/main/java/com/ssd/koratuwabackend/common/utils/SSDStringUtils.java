package com.ssd.koratuwabackend.common.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;

public class SSDStringUtils {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";


    public static boolean isNullOrEmpty(String arg) {
        return arg == null || arg.isEmpty();
    }

    public static String generateId(String prefix) {
        String id_1 = RandomStringUtils.randomAlphanumeric(8);
        String id_2 = RandomStringUtils.randomAlphanumeric(new Random().nextInt(5) + 1);
        if (prefix == null || StringUtils.isEmpty(prefix))
            prefix = RandomStringUtils.randomAlphanumeric(new Random().nextInt(4) + 1);
        return prefix.concat("-").concat(id_2).concat("-").concat(id_1);
    }

    public static String generateId(int prefix) {
        return generateId(String.valueOf(prefix));
    }
}
