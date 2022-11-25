package raylcast.ondemandservers.commands.ods.sub;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import raylcast.ondemandservers.commands.SubCommand;
import raylcast.ondemandservers.models.ServiceStatus;
import raylcast.ondemandservers.services.SystemServiceConnector;

import java.util.ArrayList;
import java.util.List;

public class StatusCommand extends SubCommand {
    private final Plugin Plugin;
    private final SystemServiceConnector SystemServiceConnector;

    public StatusCommand(Plugin plugin, SystemServiceConnector systemServiceConnector) {
        super();
        Plugin = plugin;
        SystemServiceConnector = systemServiceConnector;
    }

    @Override
    public String getName() {
        return "Status";
    }
    @Override
    public String getDescription() {
        return "Get a status overview over all available servers";
    }

    @Override
    public String getUsage() {
        return getName() + " : " + getDescription();
    }

    @Override
    public Permission getPermission() {
        return new Permission("raylcast.ods.command.ods.status");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, List<String> args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("This command is only for players!");
            return true;
        }

        var sb = new StringBuilder();

        for (String service : SystemServiceConnector.getServices()) {
            sb.append(service)
              .append(" - ")
              .append(SystemServiceConnector.getStatus(service).name());
        }

        commandSender.sendMessage(sb.toString());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        return null;
    }
}
