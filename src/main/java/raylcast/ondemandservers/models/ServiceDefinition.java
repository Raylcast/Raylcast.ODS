package raylcast.ondemandservers.models;

import org.bukkit.Material;

public class ServiceDefinition {
    public int Slot;
    public String Identifier;
    public Material Material;
    public String DisplayName;

    public ServiceDefinition(int slot, String identifier, Material material, String displayName) {
        Slot = slot;
        Identifier = identifier;
        Material = material;
        DisplayName = displayName;
    }
}
