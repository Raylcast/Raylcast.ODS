package raylcast.ondemandservers.services;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import raylcast.ondemandservers.models.ServiceDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ConfigService {
    private static final String ServicesListKey = "services";
    private static final String SlotItemKey = "slot";
    private static final String IdentifierItemKey = "identifier";
    private static final String MaterialItemKey = "material";
    private static final String DisplayNameItemKey = "displayName";

    private static final String HotbarItemSectionKey = "hotbaritem";
    private static final String HotbarItemEnabledItemKey = "enabled";
    private static final String HotbarItemSlotItemKey = "slot";
    private static final String HotbarItemMaterialItemKey = "material";
    
    private final Plugin Plugin;
    private final Logger Logger;

    private List<ServiceDefinition> ServiceDefinitions;
    private boolean HotbarItemEnabled;
    private int HotbarItemSlot;
    private Material HotbarItemMaterial;
    
    public ConfigService(Plugin plugin) {
        Plugin = plugin;
        Logger = plugin.getLogger();

        ServiceDefinitions = new ArrayList<>();
        HotbarItemEnabled = false;
    }

    public void Reload() {
        var config = Plugin.getConfig();

        ReloadServiceDefinitionsConfig(config);
        ReloadHotbarItemConfig(config);
    }

    private void ReloadServiceDefinitionsConfig(FileConfiguration config) {
        var items = config.getMapList(ServicesListKey);
        var serviceDefinitions = new ArrayList<ServiceDefinition>();

        for(var rawService : items) {
            var _slot = rawService.get(SlotItemKey);
            var _identifier = rawService.get(IdentifierItemKey);
            var _material = rawService.get(MaterialItemKey);
            var _displayName = rawService.get(DisplayNameItemKey);

            if (!(_slot instanceof Integer slot)) {
                Logger.severe("Invalid value for key slot: " + _slot.toString());
                continue;
            }
            if (!(_identifier instanceof String identifier)){
                Logger.severe("Invalid value for key identifier: " + _identifier.toString());
                continue;
            }
            if (!(_material instanceof String material) || !isValidMaterial(material)){
                Logger.severe("Invalid value for key material: " + _material.toString());
                continue;
            }
            if (!(_displayName instanceof String displayName)){
                Logger.severe("Invalid value for key displayName: " + _displayName.toString());
                continue;
            }

            serviceDefinitions.add(new ServiceDefinition(
                    slot,
                    identifier,
                    Material.valueOf(material),
                    displayName
            ));
        }

        ServiceDefinitions = serviceDefinitions;
    }

    private void ReloadHotbarItemConfig(FileConfiguration config) {
        var section = config.getConfigurationSection(HotbarItemSectionKey);

        if (section == null) {
            HotbarItemEnabled = false;
            return;
        }

        HotbarItemEnabled = section.getBoolean(HotbarItemEnabledItemKey);
        HotbarItemSlot = section.getInt(HotbarItemSlotItemKey);
        
        var rawMaterial = section.getString(HotbarItemMaterialItemKey);
        HotbarItemMaterial = isValidMaterial(rawMaterial) 
            ? Material.valueOf(rawMaterial)
            : Material.CLOCK;
    }

    public List<ServiceDefinition> getServiceDefinitions() {
        return ServiceDefinitions;
    }
    public boolean isHotbarItemEnabled() {
        return HotbarItemEnabled;
    }
    public int getHotbarItemSlot() {
        return HotbarItemSlot;
    }
    public Material getHotbarItemMaterial() {
        return HotbarItemMaterial;
    }

    public boolean isValidMaterial(String material) {
        try {
            Material.valueOf(material);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
