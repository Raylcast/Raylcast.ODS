package raylcast.ondemandservers.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import raylcast.ondemandservers.OnDemandServers;

public class InventoryClickListener implements Listener {

    private final OnDemandServers plugin;

    public InventoryClickListener(OnDemandServers plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!player.hasPermission(plugin.ItemService.getItemUsePerm())) return;
        if (event.getCurrentItem() != null && plugin.ItemService.isItemName(event.getCurrentItem().displayName())) {
            event.setCancelled(true);
            return;
        }
        if ((plugin.ItemService.getPlayersInUi().contains(player))) {
            event.setCancelled(true);
            plugin.ItemService.handleItemClick(event.getSlot(), player);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        plugin.ItemService.getPlayersInUi().remove(player);
    }
}
