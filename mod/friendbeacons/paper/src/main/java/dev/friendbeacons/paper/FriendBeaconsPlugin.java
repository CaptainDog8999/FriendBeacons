package dev.friendbeacons.paper;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.plugin.java.JavaPlugin;

import dev.friendbeacons.paper.share.FriendPackets;
import dev.friendbeacons.paper.share.FwpCommand;
import dev.friendbeacons.paper.share.PinBroadcaster;
import dev.friendbeacons.paper.share.ShareStore;

public final class FriendBeaconsPlugin extends JavaPlugin implements Listener {
    private PinBroadcaster pins;
    private ShareStore shares;

    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        shares = new ShareStore(this);
        shares.load();
        getServer().getMessenger().registerOutgoingPluginChannel(this, FriendPackets.UPDATE);
        getServer().getMessenger().registerOutgoingPluginChannel(this, FriendPackets.REMOVE);
        pins = new PinBroadcaster(this, shares);
        FwpCommand command = new FwpCommand(this, shares);
        var exec = getCommand("friendbeacons");
        if (exec != null) {
            exec.setExecutor(command);
            exec.setTabCompleter(command);
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Friend Beacons ready. Fabric and NeoForge clients on 26.2 will see pins over friendbeacons:update.");
    }

    @Override
    public void onDisable() {
        if (pins != null) pins.cancel();
        if (shares != null) shares.save();
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
    }

    public PinBroadcaster pins() {
        return pins;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        getServer().getScheduler().runTaskLater(this, () -> pins.syncIncoming(event.getPlayer()), 20L);
    }

    @EventHandler
    public void onChannel(PlayerRegisterChannelEvent event) {
        if (FriendPackets.UPDATE.equals(event.getChannel())) {
            pins.syncIncoming(event.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        pins.playerLeft(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onWorld(PlayerChangedWorldEvent event) {
        pins.pushNow(event.getPlayer());
    }
}
