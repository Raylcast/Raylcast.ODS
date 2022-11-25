package raylcast.ondemandservers.services;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import raylcast.ondemandservers.commands.CommandBase;
import raylcast.ondemandservers.commands.GroupedCommand;
import raylcast.ondemandservers.commands.ods.OdsCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandHandlerService {
    private final JavaPlugin Plugin;
    private final SystemServiceConnector SystemServiceConnector;
    private final Map<String, CommandBase> Commands;

    public CommandHandlerService(JavaPlugin plugin, SystemServiceConnector systemServiceConnector){
        Plugin = plugin;
        SystemServiceConnector = systemServiceConnector;
        Commands = new HashMap<>();

        for (var command : GetCommands()){
            Commands.put(command.getName().toUpperCase(), command);
        }
    }

    private CommandBase[] GetCommands(){
        return new CommandBase[] {
          new OdsCommand(Plugin, SystemServiceConnector),
        };
    }

    public void onEnable(){
        for(var command : Commands.values()){
            var cmd = Plugin.getCommand(command.getName());

            if (cmd == null){
                throw new NullPointerException("cmd is null");
            }

            cmd.setExecutor(command);
            cmd.setTabCompleter(Plugin);
        }
    }

    public void onDisable(){
    }

    public List<String> onTabComplete(CommandSender sender, String label, List<String> args){
        if (args.isEmpty()){
            return Commands.values().stream()
                .filter(command -> !(command instanceof GroupedCommand groupedCommand) || sender.hasPermission(groupedCommand.getPermission()))
                .map(CommandBase::getName)
                .filter(name -> name.toUpperCase().startsWith(label.toUpperCase()))
                .toList();
        }

        var command = Commands.get(label.toUpperCase());

        if (command == null){
            return null;
        }

        if (command instanceof GroupedCommand groupedCommand && !sender.hasPermission(groupedCommand.getPermission())){
            return new ArrayList<>();
        }

        return command.onTabComplete(sender, args.stream().toList());
    }
}
