package dev.friendbeacons.paper.share;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.kyori.adventure.text.Component;

public final class PinBroadcaster extends BukkitRunnable {
    private final JavaPlugin plugin;
    private final ShareStore store;
    private final Map<UUID, Snapshot> last = new HashMap<>();

    public PinBroadcaster(JavaPlugin plugin, ShareStore store) {
        this.plugin = plugin;
        this.store = store;
        runTaskTimer(plugin, 10L, 10L);
    }

    @Override
    public void run() {
        for (Player owner : Bukkit.getOnlinePlayers()) {
            push(owner, false);
        }
    }

    public void pushNow(Player owner) {
        push(owner, true);
    }

    public void syncIncoming(Player viewer) {
        if (!hasClient(viewer)) return;
        for (Player owner : Bukkit.getOnlinePlayers()) {
            if (store.canSee(viewer.getUniqueId(), owner.getUniqueId())) {
                sendUpdate(viewer, owner);
            }
        }
    }

    public void playerLeft(UUID id) {
        last.remove(id);
        byte[] payload = FriendPackets.remove(id);
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (hasClient(viewer)) {
                viewer.sendPluginMessage(plugin, FriendPackets.REMOVE, payload);
            }
        }
    }

    public void removeFor(Player viewer, UUID owner) {
        if (hasClient(viewer)) {
            viewer.sendPluginMessage(plugin, FriendPackets.REMOVE, FriendPackets.remove(owner));
        }
    }

    private void push(Player owner, boolean force) {
        ShareStore.Entry entry = store.get(owner.getUniqueId());
        if (entry.mode == ShareMode.NOBODY) {
            if (force || last.remove(owner.getUniqueId()) != null) {
                playerLeft(owner.getUniqueId());
            }
            return;
        }
        String dimension = owner.getWorld().getKey().toString();
        int x = owner.getLocation().getBlockX();
        int y = owner.getLocation().getBlockY();
        int z = owner.getLocation().getBlockZ();
        Snapshot previous = last.get(owner.getUniqueId());
        boolean moved = previous == null
                || !previous.dimension.equals(dimension)
                || dist2(previous.x, previous.y, previous.z, x, y, z) >= 1;
        boolean heartbeat = previous == null || previous.age++ >= 4;
        if (!force && !moved && !heartbeat) return;
        last.put(owner.getUniqueId(), new Snapshot(dimension, x, y, z));
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            if (!store.canSee(viewer.getUniqueId(), owner.getUniqueId())) continue;
            sendUpdate(viewer, owner);
        }
    }

    private void sendUpdate(Player viewer, Player owner) {
        if (!hasClient(viewer)) return;
        ShareStore.Entry entry = store.get(owner.getUniqueId());
        byte color = PinColors.colorFor(owner.getUniqueId(), entry.color);
        byte[] payload = FriendPackets.update(
                owner.getUniqueId(),
                owner.getName(),
                owner.getWorld().getKey().toString(),
                owner.getLocation().getBlockX(),
                owner.getLocation().getBlockY(),
                owner.getLocation().getBlockZ(),
                color);
        viewer.sendPluginMessage(plugin, FriendPackets.UPDATE, payload);
    }

    public static boolean hasClient(Player player) {
        Set<String> channels = player.getListeningPluginChannels();
        return channels.contains(FriendPackets.UPDATE);
    }

    private static int dist2(int x1, int y1, int z1, int x2, int y2, int z2) {
        int dx = x1 - x2, dy = y1 - y2, dz = z1 - z2;
        return dx * dx + dy * dy + dz * dz;
    }

    private static final class Snapshot {
        final String dimension;
        final int x, y, z;
        int age;
        Snapshot(String dimension, int x, int y, int z) {
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
