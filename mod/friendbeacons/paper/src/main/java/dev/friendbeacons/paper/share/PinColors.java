package dev.friendbeacons.paper.share;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public final class PinColors {
    public static final String[] NAMES = {
            "black", "dark_blue", "dark_green", "dark_aqua",
            "dark_red", "dark_purple", "gold", "gray",
            "dark_gray", "blue", "green", "aqua",
            "red", "purple", "yellow", "white"
    };

    private PinColors() {}

    public static String name(int index) {
        return NAMES[index & 15];
    }

    public static byte parse(String raw) {
        if (raw == null) return -1;
        String key = raw.trim().toLowerCase(Locale.ROOT).replace(' ', '_').replace('-', '_');
        for (int i = 0; i < NAMES.length; i++) {
            if (NAMES[i].equals(key)) return (byte) i;
        }
        return -1;
    }

    public static boolean isValid(String raw) {
        return parse(raw) >= 0;
    }

    public static String normalize(String raw) {
        byte parsed = parse(raw);
        return parsed < 0 ? "green" : name(parsed);
    }

    public static byte stored(String raw) {
        byte parsed = parse(raw);
        return parsed < 0 ? 10 : parsed;
    }

    public static byte colorFor(UUID id, String stored) {
        byte parsed = parse(stored);
        if (parsed >= 0) return parsed;
        int[] preferred = {12, 9, 10, 11, 14, 13, 6, 15};
        return (byte) preferred[Math.floorMod(id.hashCode(), preferred.length)];
    }

    public static Set<String> names() {
        return Set.of(NAMES);
    }
}
