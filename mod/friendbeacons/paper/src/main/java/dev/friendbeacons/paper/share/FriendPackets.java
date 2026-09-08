package dev.friendbeacons.paper.share;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/** Byte layout matches Fabric/NeoForge FriendlyByteBuf so mixed loaders work. */
public final class FriendPackets {
    public static final String UPDATE = "friendbeacons:update";
    public static final String REMOVE = "friendbeacons:remove";

    private FriendPackets() {}

    public static byte[] update(UUID playerId, String name, String dimension, int x, int y, int z, byte color) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeUuid(out, playerId);
        writeUtf(out, clip(name, 32));
        writeUtf(out, clip(dimension, 128));
        writeVarInt(out, x);
        writeVarInt(out, y);
        writeVarInt(out, z);
        out.write(color);
        return out.toByteArray();
    }

    public static byte[] remove(UUID playerId) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeUuid(out, playerId);
        return out.toByteArray();
    }

    private static String clip(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max);
    }

    private static void writeUuid(ByteArrayOutputStream out, UUID id) {
        writeLong(out, id.getMostSignificantBits());
        writeLong(out, id.getLeastSignificantBits());
    }

    private static void writeLong(ByteArrayOutputStream out, long value) {
        for (int shift = 56; shift >= 0; shift -= 8) {
            out.write((int) (value >>> shift));
        }
    }

    private static void writeUtf(ByteArrayOutputStream out, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarInt(out, bytes.length);
        out.writeBytes(bytes);
    }

    private static void writeVarInt(ByteArrayOutputStream out, int value) {
        int v = value;
        while ((v & 0xFFFFFF80) != 0) {
            out.write((v & 0x7F) | 0x80);
            v >>>= 7;
        }
        out.write(v);
    }
}
