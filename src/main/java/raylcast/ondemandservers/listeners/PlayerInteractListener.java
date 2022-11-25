package raylcast.ondemandservers.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import raylcast.ondemandservers.OnDemandServers;

public class PlayerInteractListener implements Listener {

    private final OnDemandServers plugin;

    public PlayerInteractListener(OnDemandServers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) return;
        if (plugin.ItemService.isItemName(event.getItem().displayName())) {
            event.setCancelled(true);
            if (!event.getPlayer().hasPermission(plugin.ItemService.getItemUsePerm())) return;
            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            plugin.ItemService.onRightClick(event.getPlayer());
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!event.getPlayer().hasPermission(plugin.ItemService.getItemUsePerm())) return;
        if (plugin.ItemService.isItemName(event.getItemDrop().getItemStack().displayName()))
            event.setCancelled(true);
    }
}
