package raylcast.ondemandservers.services;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import raylcast.ondemandservers.models.ServiceDefinition;
import raylcast.ondemandservers.models.ServiceStatus;

import java.util.ArrayList;
import java.util.List;

public class ItemService {

    private final JavaPlugin Plugin;
    private final SystemServiceConnector SystemServiceConnector;
    private final ConfigService ConfigService;

    private final List<Player> playersInUi = new ArrayList<>();

    public ItemService(JavaPlugin plugin, raylcast.ondemandservers.services.SystemServiceConnector systemServiceConnector, ConfigService configService) {
        Plugin = plugin;
        SystemServiceConnector = systemServiceConnector;
        ConfigService = configService;
    }


    public void enable() {
        Bukkit.getOnlinePlayers().forEach(this::addItemToPlayer);
    }

    public void disable() {
        playersInUi.forEach(HumanEntity::closeInventory);
    }


    public void onRightClick(Player player) {
        playersInUi.add(player);
        player.openInventory(getUi());
    }


    public void handleItemClick(int slot, Player player) {
        var serviceDefinitionResult = ConfigService.getServiceDefinitions().stream()
                .filter(x -> x.Slot == slot)
                .findFirst();

        if (serviceDefinitionResult.isEmpty()){
            return;
        }

        var serviceDefinition = serviceDefinitionResult.get();
        ServiceStatus status = SystemServiceConnector.getStatus(serviceDefinition.Identifier);

        player.closeInventory();

        if (status == ServiceStatus.Unknown || status == ServiceStatus.Failed){
            player.sendMessage("This server is in an invalid state, Contact an administrator!");
            return;
        }



        Bukkit.getScheduler().runTaskAsynchronously(Plugin, () -> {
            switch (status) {
                case Dead -> {
                    player.sendMessage(ChatColor.BLUE + "Starting...");
                    SystemServiceConnector.startService(serviceDefinition.Identifier);
                    player.sendMessage(ChatColor.GREEN + "Started Server " + serviceDefinition.DisplayName + "!");
                }
                case Running -> {
                    player.sendMessage(ChatColor.GOLD + "Stopping...");
                    SystemServiceConnector.stopService(serviceDefinition.Identifier);
                    player.sendMessage(ChatColor.RED + "Stopped Server " + serviceDefinition.DisplayName + "!");
                }
            }
        });
    }

    public void addItemToPlayer(Player player) {
        if (!ConfigService.isHotbarItemEnabled()){
            return;
        }
        if (!player.hasPermission(getItemUsePerm())){
            return;
        }

        player.getInventory().setItem(ConfigService.getHotbarItemSlot(), getItem());
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(ConfigService.getHotbarItemMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(getItemName());
        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.GRAY + "Right click to manage your ODS-Servers"));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public Inventory getUi() {
        Inventory inv = Bukkit.createInventory(null, 9, Component.text(ChatColor.DARK_AQUA + "Server Manager"));

        for(var serviceDefinition : ConfigService.getServiceDefinitions()){
            inv.setItem(serviceDefinition.Slot, getItemFromServiceDefinition(serviceDefinition));
        }

        return  inv;
    }

    private ItemStack getItemFromServiceDefinition(ServiceDefinition serviceDefinition) {
        var item = new ItemStack(serviceDefinition.Material);
        var meta = item.getItemMeta();
        meta.displayName(Component.text(serviceDefinition.DisplayName));

        var lore = new ArrayList<Component>();

        switch (SystemServiceConnector.getStatus(serviceDefinition.Identifier)) {
            case Dead -> {
                lore.add(Component.text(ChatColor.RED + ChatColor.ITALIC.toString() + "Offline"));
                lore.add(Component.empty());
                lore.add(Component.text(ChatColor.GRAY + "Click to start the server!"));
            }
            case Failed -> {
                lore.add(Component.text(ChatColor.DARK_RED + ChatColor.ITALIC.toString() + "FAILED!"));
                lore.add(Component.empty());
                lore.add(Component.text(ChatColor.GRAY + "Please contact the server owner!"));
            }
            case Running -> {
                lore.add(Component.text(ChatColor.GREEN + ChatColor.ITALIC.toString() + "Online"));
                lore.add(Component.empty());
                lore.add(Component.text(ChatColor.GRAY + "Click to stop the server!"));
                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            case Unknown -> lore.add(Component.text(ChatColor.YELLOW + ChatColor.ITALIC.toString() + "Status Unknown"));
        }

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public Component getItemName() {
        return Component.text(ChatColor.AQUA + "Server Manager");
    }

    public boolean isItemName(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component).contains(PlainTextComponentSerializer.plainText().serialize(getItemName()));
    }

    public Permission getItemUsePerm() {
        return new Permission("raylcast.ods.item.use");
    }

    public List<Player> getPlayersInUi() {
        return playersInUi;
    }
}
