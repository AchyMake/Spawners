package org.achymake.spawners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.achymake.spawners.commands.SpawnerCommand;
import org.achymake.spawners.commands.SpawnersCommand;
import org.achymake.spawners.listeners.BlockBreak;
import org.achymake.spawners.listeners.BlockPlace;
import org.achymake.spawners.listeners.PlayerInteract;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;

public final class Spawners extends JavaPlugin {
    private static Spawners instance;
    private static PluginManager manager;
    @Override
    public void onEnable() {
        instance = this;
        manager = getServer().getPluginManager();
        getCommand("spawner").setExecutor(new SpawnerCommand(this));
        getCommand("spawners").setExecutor(new SpawnersCommand(this));
        getManager().registerEvents(new BlockBreak(this), this);
        getManager().registerEvents(new BlockPlace(this), this);
        getManager().registerEvents(new PlayerInteract(this), this);
        reload();
        sendLog(Level.INFO, "Enabled " + getDescription().getName() + " " + getDescription().getVersion());
    }
    @Override
    public void onDisable() {
        sendLog(Level.INFO, "Disabled " + getDescription().getName() + " " + getDescription().getVersion());
    }
    public void getUpdate(Player player) {
        if (notifyUpdate()) {
            getLatest((latest) -> {
                if (!getDescription().getVersion().equals(latest)) {
                    send(player,"&6" + getDescription().getName() + " Update:&f " + latest);
                    send(player,"&6Current Version: &f" + getDescription().getVersion());
                }
            });
        }
    }
    public void getUpdate() {
        if (notifyUpdate()) {
            getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                @Override
                public void run() {
                    getLatest((latest) -> {
                        sendLog(Level.INFO, "Checking latest release");
                        if (getDescription().getVersion().equals(latest)) {
                            sendLog(Level.INFO, "You are using the latest version");
                        } else {
                            sendLog(Level.INFO, "New Update: " + latest);
                            sendLog(Level.INFO, "Current Version: " + getDescription().getVersion());
                        }
                    });
                }
            });
        }
    }
    public void getLatest(Consumer<String> consumer) {
        try {
            InputStream inputStream = (new URL("https://api.spigotmc.org/legacy/update.php?resource=" + 103803)).openStream();
            Scanner scanner = new Scanner(inputStream);
            if (scanner.hasNext()) {
                consumer.accept(scanner.next());
                scanner.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            sendLog(Level.WARNING, e.getMessage());
        }
    }
    private boolean notifyUpdate() {
        return getConfig().getBoolean("notify-update");
    }
    public void reload() {
        File file = new File(getDataFolder(), "config.yml");
        if (file.exists()) {
            try {
                getConfig().load(file);
                getConfig().save(file);
            } catch (IOException | InvalidConfigurationException e) {
                sendLog(Level.WARNING, e.getMessage());
            }
        } else {
            getConfig().options().copyDefaults(true);
            try {
                getConfig().save(file);
            } catch (IOException e) {
                sendLog(Level.WARNING, e.getMessage());
            }
        }
    }
    public void send(ConsoleCommandSender sender, String message) {
        sender.sendMessage(message);
    }
    public void send(Player player, String message) {
        player.sendMessage(addColor(message));
    }
    public void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(addColor(message)));
    }
    public String addColor(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    public void sendLog(Level level, String message) {
        getLogger().log(level, message);
    }
    public PluginManager getManager() {
        return manager;
    }
    public static Spawners getInstance() {
        return instance;
    }
}
