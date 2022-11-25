package raylcast.ondemandservers;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import raylcast.ondemandservers.services.CommandHandlerService;
import raylcast.ondemandservers.services.SystemServiceConnector;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public final class OnDemandServers extends JavaPlugin {
    private final Logger Logger;

    private final SystemServiceConnector SystemServiceConnector;
    private final CommandHandlerService CommandHandlerService;

    private LuckPerms LuckPerms;
    private FileConfiguration Config;

    public OnDemandServers() {
        Logger = getLogger();

        var services = new String[] {
            "survival"
        };

        SystemServiceConnector = new SystemServiceConnector(this, services);
        CommandHandlerService = new CommandHandlerService(this, SystemServiceConnector);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        Config = getConfig();
        Config.options().copyDefaults(true);
        saveConfig();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        var registration = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (registration == null){
            throw new IllegalStateException("Can't enable the plugin without Luckperms installed!");
        }

        LuckPerms = registration.getProvider();
        CommandHandlerService.onEnable();
        Config = getConfig();

        SystemServiceConnector.enable();
    }

    @Override
    public void onDisable() {
        super.onDisable();

        CommandHandlerService.onDisable();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return CommandHandlerService.onTabComplete(sender, command.getName(), Arrays.stream(args).toList());
    }
}