package raylcast.ondemandservers.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

import java.util.List;

public abstract class SubCommand extends CommandBase {
    public abstract String getName();
    public abstract String getDescription();
    public String getUsage(){
        return getName() + " : " + getDescription();
    }

    public abstract Permission getPermission();

    public abstract boolean onCommand(CommandSender commandSender, List<String> args);
}
