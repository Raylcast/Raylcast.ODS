package raylcast.ondemandservers.commands.ods.sub;

import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import raylcast.ondemandservers.commands.SubCommand;
import raylcast.ondemandservers.models.ServiceStatus;
import raylcast.ondemandservers.services.SystemServiceConnector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StopCommand extends SubCommand {
    private final Plugin Plugin;
    private final SystemServiceConnector SystemServiceConnector;

    public StopCommand(Plugin plugin, SystemServiceConnector systemServiceConnector) {
        super();
        Plugin = plugin;
        SystemServiceConnector = systemServiceConnector;
    }

    @Override
    public String getName() {
        return "Stop";
    }
    @Override
    public String getDescription() {
        return "Stop an on-demand server";
    }

    @Override
    public String getUsage() {
        return getName() + " [Identifier] : " + getDescription();
    }

    @Override
    public Permission getPermission() {
        return new Permission("raylcast.ods.command.ods.stop");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, List<String> args) {
        if (args.size() != 1){
            return false;
        }
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("This command is only for players!");
            return true;
        }

        var serviceIdentifier = args.get(0).toLowerCase();

        if (SystemServiceConnector.getStatus(serviceIdentifier) == ServiceStatus.Dead) {
            commandSender.sendMessage("Server already stopped!");
            return true;
        }
        if (SystemServiceConnector.getStatus(serviceIdentifier) != ServiceStatus.Running) {
            commandSender.sendMessage("This server is in an invalid state! Please notify the owner!");
            return true;
        }

        commandSender.sendMessage("Stopping server...");

        Bukkit.getScheduler().runTaskAsynchronously(Plugin, () -> {
            try {
                SystemServiceConnector.stopService(serviceIdentifier);
                commandSender.sendMessage("Successfully stopped server!");
            } catch (Exception e) {
                commandSender.sendMessage("There was an error trying to stop the server!");
                e.printStackTrace();
            }
        });

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            return SystemServiceConnector.getRunningServices();
        }

        return new ArrayList<>();
    }
}
