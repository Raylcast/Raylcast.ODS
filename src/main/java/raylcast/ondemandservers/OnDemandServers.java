package raylcast.ondemandservers;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import raylcast.ondemandservers.listeners.InventoryClickListener;
import raylcast.ondemandservers.listeners.PlayerInteractListener;
import raylcast.ondemandservers.listeners.PlayerJoinListener;
import raylcast.ondemandservers.services.CommandHandlerService;
import raylcast.ondemandservers.services.ConfigService;
import raylcast.ondemandservers.services.ItemService;
import raylcast.ondemandservers.services.SystemServiceConnector;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public final class OnDemandServers extends JavaPlugin {
    private final Logger Logger;

    private final SystemServiceConnector SystemServiceConnector;
    private final CommandHandlerService CommandHandlerService;
    private final ConfigService ConfigService;

    public final ItemService ItemService;

    private LuckPerms LuckPerms;

    public OnDemandServers() {
        Logger = getLogger();

        ConfigService = new ConfigService(this);
        SystemServiceConnector = new SystemServiceConnector(this, ConfigService);
        CommandHandlerService = new CommandHandlerService(this, SystemServiceConnector);

        ItemService = new ItemService(this, SystemServiceConnector, ConfigService);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        saveConfig();
    }

    @Override
    public void onEnable() {
        var registration = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (registration == null){
            throw new IllegalStateException("Can't enable the plugin without Luckperms installed!");
        }

        super.onEnable();

        ConfigService.Reload();
        SystemServiceConnector.Enable();

        registerListeners();

        LuckPerms = registration.getProvider();
        CommandHandlerService.onEnable();
        ItemService.enable();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        CommandHandlerService.onDisable();
        ItemService.disable();
    }

    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return CommandHandlerService.onTabComplete(sender, command.getName(), Arrays.stream(args).toList());
    }
}
