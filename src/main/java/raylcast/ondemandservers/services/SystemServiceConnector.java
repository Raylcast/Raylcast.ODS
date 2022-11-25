package raylcast.ondemandservers.services;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import raylcast.ondemandservers.models.ServiceStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemServiceConnector {
    private final JavaPlugin Plugin;
    private final Runtime Runtime;

    private final Map<String, ServiceStatus> Services;

    public SystemServiceConnector(JavaPlugin plugin, String[] availableServices) {
        Plugin = plugin;
        Runtime = java.lang.Runtime.getRuntime();
        Services = new HashMap<>();

        for (String service : availableServices) {
            Services.put(service, ServiceStatus.Unknown);
        }
    }

    public void enable() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Plugin, () -> {
            try {
                updateServices();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }, 0, 20 * 60);
    }

    private void updateServices() throws IOException, InterruptedException {
        for (String service : Services.keySet()) {
            var process = Runtime.exec(new String[] { "sh", "-c", "systemctl status " + service });

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            process.waitFor();

            while(true)
            {
                var line = stdInput.readLine();

                if (line == null) {
                    throw new RuntimeException("Missing service status in systemctl result");
                }

                if (!line.contains("Active: ")) {
                    continue;
                }

                if (line.contains("(running)")){
                    Services.replace(service, ServiceStatus.Running);
                    break;
                } else if (line.contains("(dead)")) {
                    Services.replace(service, ServiceStatus.Dead);
                    break;
                } else if (line.contains("(Result: exit-code")) {
                    Services.replace(service, ServiceStatus.Dead);
                    break;
                } else {
                    Services.replace(service, ServiceStatus.Unknown);
                }
            }
        }
    }

    public long TotalRunningServiceCount() {
        return Services.values().stream()
            .filter(x -> x == ServiceStatus.Running)
            .count();
    }

    public ServiceStatus getStatus(String serviceIdentifier) {
        return Services.get(serviceIdentifier);
    }

    public void startService(String serviceIdentifier) {
        if (Services.get(serviceIdentifier) != ServiceStatus.Dead){
            throw new RuntimeException("Invalid state transition!");
        }

        try {
            Runtime.exec(new String[] { "sh", "-c", "systemctl start " + serviceIdentifier })
                   .waitFor();

            Services.replace(serviceIdentifier, ServiceStatus.Running);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            //Shouldn't be an issue
        }
    }

    public void stopService(String serviceIdentifier) {
        if (Services.get(serviceIdentifier) != ServiceStatus.Running){
            throw new RuntimeException("Invalid state transition!");
        }

        try {
            Runtime.exec(new String[] { "sh", "-c", "systemctl stop " + serviceIdentifier })
                   .waitFor();

            Services.replace(serviceIdentifier, ServiceStatus.Dead);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            //Shouldn't be an issue
        }
    }

    public List<String> getServices() {
        return Services.keySet().stream().toList();
    }
    public List<String> getRunningServices() {
        return Services.keySet().stream().filter(x -> Services.get(x) == ServiceStatus.Running).toList();
    }
    public List<String> getDeadServices() {
        return Services.keySet().stream().filter(x -> Services.get(x) == ServiceStatus.Dead).toList();
    }
}
