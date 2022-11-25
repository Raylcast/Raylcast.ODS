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
import raylcast.ondemandservers.models.ServiceStatus;

import java.util.ArrayList;
import java.util.List;

public class ItemService {

    private final JavaPlugin Plugin;
    private final SystemServiceConnector SystemServiceConnector;
    private final List<Player> playersInUi = new ArrayList<>();


    public ItemService(JavaPlugin plugin, raylcast.ondemandservers.services.SystemServiceConnector systemServiceConnector) {
        Plugin = plugin;
        SystemServiceConnector = systemServiceConnector;
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
        String service = getServiceFromSlot(slot);
        ServiceStatus status = SystemServiceConnector.getStatus(service) == null ? ServiceStatus.Unknown : SystemServiceConnector.getStatus(service);

        switch (status) {
            case Dead -> {
                player.closeInventory();
                SystemServiceConnector.startService(service);
                player.sendMessage(ChatColor.GREEN + "Started Server " + service + "!");
            }
            case Running -> {
                player.closeInventory();
                SystemServiceConnector.stopService(service);
                player.sendMessage(ChatColor.RED + "Stopped Server " + service + "!");
            }
        }
    }

    public void addItemToPlayer(Player player) {
        if (Plugin.getConfig().getBoolean("item.enabled") && player.hasPermission(getItemUsePerm())) {
            player.getInventory().setItem(getItemSlot(), getItem());
        }
    }

    public int getItemSlot() {
        int slot = Plugin.getConfig().getInt("item.hotbarslot", 0);
        return slot < 9 && slot >= 0 ? slot : 0;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.RECOVERY_COMPASS);
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
        inv.setItem(0, getItemFromConfig(0));
        inv.setItem(1, getItemFromConfig(1));
        inv.setItem(2, getItemFromConfig(2));
        inv.setItem(3, getItemFromConfig(3));
        inv.setItem(4, getItemFromConfig(4));
        inv.setItem(5, getItemFromConfig(5));
        inv.setItem(6, getItemFromConfig(6));
        inv.setItem(7, getItemFromConfig(7));
        inv.setItem(8, getItemFromConfig(8));
        return inv;
    }


    private ItemStack getItemFromConfig(int i) {
        ConfigurationSection section = Plugin.getConfig().getConfigurationSection("item." + i);
        if (section == null) return new ItemStack(Material.AIR);
        Material material = Material.SCULK_SHRIEKER;
        String displayName = section.getString("displayName");
        if (displayName == null) displayName = ChatColor.RESET + "Server " + i;
        String serverServiceName = section.getString("serviceName");
        try {
            material = section.getString("material") == null ? Material.SCULK_SHRIEKER : Material.valueOf(section.getString("material"));
        } catch (IllegalArgumentException ignored) {}

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(displayName));
        List<Component> lore = meta.lore();
        if (lore == null) lore = new ArrayList<>();

        switch (SystemServiceConnector.getStatus(serverServiceName) == null ? ServiceStatus.Unknown : SystemServiceConnector.getStatus(serverServiceName)) {
            case Dead -> {
                lore.add(Component.text(ChatColor.RED + ChatColor.ITALIC.toString() + "Offline"));
                lore.add(Component.newline());
                lore.add(Component.text(ChatColor.GRAY + "Click to start the server!"));
            }
            case Failed -> {
                lore.add(Component.text(ChatColor.DARK_RED + ChatColor.ITALIC.toString() + "FAILED!"));
                lore.add(Component.newline());
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

    private String getServiceFromSlot(int slot) {
        ConfigurationSection section = Plugin.getConfig().getConfigurationSection("item." + slot);
        if (section == null) section = Plugin.getConfig().createSection("item");
        return section.getString("serviceName");
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
