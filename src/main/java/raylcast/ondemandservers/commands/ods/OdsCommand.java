package raylcast.ondemandservers.commands.ods;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import raylcast.ondemandservers.commands.GroupedCommand;
import raylcast.ondemandservers.commands.SubCommand;
import raylcast.ondemandservers.commands.ods.sub.StartCommand;
import raylcast.ondemandservers.commands.ods.sub.StatusCommand;
import raylcast.ondemandservers.commands.ods.sub.StopCommand;
import raylcast.ondemandservers.services.SystemServiceConnector;

import java.util.Map;

public class OdsCommand extends GroupedCommand {
    public static final String CommandName = "Ods";
    private Map<String, SubCommand> SubCommands;

    private final Plugin Plugin;
    private final SystemServiceConnector SystemServiceConnector;

    public OdsCommand(Plugin plugin, SystemServiceConnector systemServiceConnector){
        Plugin = plugin;
        SystemServiceConnector = systemServiceConnector;

        init();
    }

    @Override
    public SubCommand[] loadSubcommands() {
        return new SubCommand[] {
            new StartCommand(Plugin, SystemServiceConnector),
            new StopCommand(Plugin, SystemServiceConnector),
            new StatusCommand(Plugin, SystemServiceConnector)
        };
    }

    @Override
    public Permission getPermission() {
        return new Permission("raylcast.ods.command.ods");
    }

    @Override
    public String getName() {
        return CommandName;
    }
}
