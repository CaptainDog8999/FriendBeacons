package dev.friendbeacons.paper.share;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Locale;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class ShareStore {
    private final JavaPlugin plugin;
    private final File file;
    private final Map<UUID, Entry> entries = new HashMap<>();

    public ShareStore(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "shares.yml");
    }

    public void load() {
        entries.clear();
        if (!file.exists()) return;
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection root = yaml.getConfigurationSection("players");
        if (root == null) return;
        for (String key : root.getKeys(false)) {
            try {
                UUID id = UUID.fromString(key);
                ConfigurationSection sec = root.getConfigurationSection(key);
                if (sec == null) continue;
                Entry entry = new Entry();
                entry.mode = parseMode(sec.getString("mode", "NOBODY"));
                entry.color = sec.getString("color", "");
                entry.allowed.addAll(sec.getStringList("allowed"));
                entries.put(id, entry);
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void save() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Map.Entry<UUID, Entry> e : entries.entrySet()) {
            String path = "players." + e.getKey();
            yaml.set(path + ".mode", e.getValue().mode.name());
            yaml.set(path + ".color", e.getValue().color);
            yaml.set(path + ".allowed", e.getValue().allowed.stream().toList());
        }
        try {
            plugin.getDataFolder().mkdirs();
            yaml.save(file);
        } catch (IOException ex) {
            plugin.getLogger().warning("Could not save shares.yml: " + ex.getMessage());
        }
    }

    public Entry get(UUID owner) {
        return entries.computeIfAbsent(owner, id -> new Entry());
    }

    public boolean canSee(UUID viewer, UUID owner) {
        if (viewer.equals(owner)) return false;
        Entry entry = get(owner);
        return switch (entry.mode) {
            case NOBODY -> false;
            case EVERYONE -> true;
            case LIST -> entry.allowed.contains(viewer.toString());
        };
    }

    private static ShareMode parseMode(String raw) {
        try {
            return ShareMode.valueOf(raw.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return ShareMode.NOBODY;
        }
    }

    public static final class Entry {
        public ShareMode mode = ShareMode.NOBODY;
        public String color = "";
        public final Set<String> allowed = new HashSet<>();
    }
}
