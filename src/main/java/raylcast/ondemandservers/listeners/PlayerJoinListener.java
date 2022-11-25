package raylcast.ondemandservers.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import raylcast.ondemandservers.OnDemandServers;

public class PlayerJoinListener implements Listener {

    private final OnDemandServers plugin;

    public PlayerJoinListener(OnDemandServers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        plugin.ItemService.addItemToPlayer(event.getPlayer());
    }

}
