package dev.friendbeacons.paper.share;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import dev.friendbeacons.paper.FriendBeaconsPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public final class FwpCommand implements CommandExecutor, TabCompleter {
    private final FriendBeaconsPlugin plugin;
    private final ShareStore store;

    public FwpCommand(FriendBeaconsPlugin plugin, ShareStore store) {
        this.plugin = plugin;
        this.store = store;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }
        if (args.length == 0) return status(player);
        return switch (args[0].toLowerCase(Locale.ROOT)) {
            case "help" -> { help(player); yield true; }
            case "share" -> args.length < 2 ? missing(player, "share <player>") : share(player, args[1]);
            case "unshare" -> args.length < 2 ? missing(player, "unshare <player>") : unshare(player, args[1]);
            case "everyone" -> setMode(player, ShareMode.EVERYONE);
            case "nobody" -> setMode(player, ShareMode.NOBODY);
            case "list" -> list(player);
            case "incoming" -> incoming(player);
            case "color" -> args.length < 2 ? missing(player, "color <color>") : color(player, args[1]);
            default -> { help(player); yield true; }
        };
    }

    private boolean missing(Player player, String usage) {
        player.sendMessage(Component.text("Usage: /fwp " + usage, NamedTextColor.GRAY));
        return true;
    }

    private void help(Player player) {
        player.sendMessage(Component.text(
                "Friend Beacons — /fwp share <player> | unshare <player> | everyone | nobody | list | incoming | color <color>",
                NamedTextColor.GRAY));
    }

    private boolean status(Player player) {
        ShareStore.Entry entry = store.get(player.getUniqueId());
        if (entry.mode == ShareMode.NOBODY) {
            player.sendMessage(Component.text("Your beacon is off. Use /fwp share <player> or /fwp everyone.", NamedTextColor.GRAY));
        } else if (entry.mode == ShareMode.EVERYONE) {
            player.sendMessage(Component.text("You are sharing with everyone online.", NamedTextColor.GREEN));
        } else {
            list(player);
        }
        byte color = PinColors.colorFor(player.getUniqueId(), entry.color);
        player.sendMessage(Component.text("Color: " + PinColors.name(color), NamedTextColor.GRAY));
        return true;
    }

    private boolean share(Player player, String name) {
        Player target = findPlayer(name);
        if (target == null) {
            player.sendMessage(Component.text("No player named " + name + " is online.", NamedTextColor.RED));
            return true;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(Component.text("You cannot share a waypoint with yourself.", NamedTextColor.RED));
            return true;
        }
        ShareStore.Entry entry = store.get(player.getUniqueId());
        if (entry.mode == ShareMode.EVERYONE) {
            entry.allowed.clear();
        }
        boolean added = entry.allowed.add(target.getUniqueId().toString());
        entry.mode = ShareMode.LIST;
        store.save();
        plugin.pins().pushNow(player);
        player.sendMessage(Component.text(
                added
                    ? "Sharing your live waypoint with " + target.getName() + ". They will see it if they have this mod and Xaero's Minimap."
                    : "You are already sharing with " + target.getName() + ".",
                added ? NamedTextColor.GREEN : NamedTextColor.YELLOW));
        return true;
    }

    private boolean unshare(Player player, String name) {
        Player target = findPlayer(name);
        if (target == null) {
            player.sendMessage(Component.text("No player named " + name + " is online.", NamedTextColor.RED));
            return true;
        }
        ShareStore.Entry entry = store.get(player.getUniqueId());
        if (entry.mode == ShareMode.EVERYONE) {
            entry.mode = ShareMode.LIST;
            entry.allowed.clear();
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (!other.getUniqueId().equals(player.getUniqueId()) && !other.getUniqueId().equals(target.getUniqueId())) {
                    entry.allowed.add(other.getUniqueId().toString());
                }
            }
            store.save();
            plugin.pins().pushNow(player);
            player.sendMessage(Component.text("Stopped sharing with " + target.getName() + ".", NamedTextColor.GREEN));
            return true;
        }
        if (!entry.allowed.remove(target.getUniqueId().toString())) {
            player.sendMessage(Component.text("You were not sharing with " + target.getName() + ".", NamedTextColor.RED));
            return true;
        }
        if (entry.allowed.isEmpty()) entry.mode = ShareMode.NOBODY;
        store.save();
        plugin.pins().removeFor(target, player.getUniqueId());
        player.sendMessage(Component.text("Stopped sharing with " + target.getName() + ".", NamedTextColor.GREEN));
        return true;
    }

    private Player findPlayer(String name) {
        Player exact = Bukkit.getPlayerExact(name);
        return exact != null ? exact : Bukkit.getPlayer(name);
    }

    private boolean setMode(Player player, ShareMode mode) {
        ShareStore.Entry entry = store.get(player.getUniqueId());
        entry.mode = mode;
        if (mode == ShareMode.NOBODY) entry.allowed.clear();
        store.save();
        if (mode == ShareMode.NOBODY) {
            plugin.pins().playerLeft(player.getUniqueId());
            player.sendMessage(Component.text("Your live waypoint is hidden. Nobody can see it.", NamedTextColor.GRAY));
        } else {
            plugin.pins().pushNow(player);
            player.sendMessage(Component.text("Anyone online with Friend Beacons + Xaero's Minimap can now see your live waypoint.", NamedTextColor.GREEN));
        }
        return true;
    }

    private boolean list(Player player) {
        ShareStore.Entry entry = store.get(player.getUniqueId());
        if (entry.mode == ShareMode.NOBODY) {
            player.sendMessage(Component.text("You are not sharing your waypoint with anyone.", NamedTextColor.GRAY));
            return true;
        }
        if (entry.mode == ShareMode.EVERYONE) {
            player.sendMessage(Component.text("You are sharing with everyone online.", NamedTextColor.GREEN));
            return true;
        }
        player.sendMessage(Component.text("Players who can see your waypoint:", NamedTextColor.WHITE));
        for (String id : entry.allowed) {
            try {
                UUID uuid = UUID.fromString(id);
                Player online = Bukkit.getPlayer(uuid);
                player.sendMessage(Component.text("  " + (online != null ? online.getName() : id), NamedTextColor.GRAY));
            } catch (IllegalArgumentException e) {
                player.sendMessage(Component.text("  " + id, NamedTextColor.GRAY));
            }
        }
        return true;
    }

    private boolean incoming(Player player) {
        List<String> names = new ArrayList<>();
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (store.canSee(player.getUniqueId(), other.getUniqueId())) {
                names.add(other.getName());
            }
        }
        if (names.isEmpty()) {
            player.sendMessage(Component.text("Nobody is sharing a live waypoint with you.", NamedTextColor.GRAY));
            return true;
        }
        player.sendMessage(Component.text("Players sharing with you:", NamedTextColor.AQUA));
        for (String name : names) {
            player.sendMessage(Component.text("  " + name, NamedTextColor.GRAY));
        }
        return true;
    }

    private boolean color(Player player, String raw) {
        if (!PinColors.isValid(raw)) {
            player.sendMessage(Component.text("Unknown color '" + raw + "'. Try: red, blue, green, aqua, yellow, white, gold, purple.", NamedTextColor.RED));
            return true;
        }
        ShareStore.Entry entry = store.get(player.getUniqueId());
        entry.color = PinColors.normalize(raw);
        store.save();
        if (entry.mode != ShareMode.NOBODY) plugin.pins().pushNow(player);
        player.sendMessage(Component.text("Your waypoint color is now " + entry.color + ".", NamedTextColor.GREEN));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filter(args[0], List.of("share", "unshare", "everyone", "nobody", "list", "incoming", "color", "help"));
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("share") || args[0].equalsIgnoreCase("unshare"))) {
            return filter(args[1], Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("color")) {
            return filter(args[1], List.of(PinColors.NAMES));
        }
        return List.of();
    }

    private static List<String> filter(String prefix, List<String> options) {
        String p = prefix.toLowerCase(Locale.ROOT);
        return options.stream().filter(o -> o.toLowerCase(Locale.ROOT).startsWith(p)).toList();
    }
}
