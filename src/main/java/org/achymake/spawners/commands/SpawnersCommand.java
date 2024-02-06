package org.achymake.spawners.commands;

import org.achymake.spawners.Spawners;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpawnersCommand implements CommandExecutor, TabCompleter {
    private final Spawners plugin;
    public SpawnersCommand(Spawners plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("check")) {
                    if (player.hasPermission("spawners.command.spawners.check")) {
                        Block block = player.getTargetBlockExact(3);
                        if (block != null) {
                            if (block.getType().equals(Material.SPAWNER)) {
                                if (block.getState() instanceof CreatureSpawner creatureSpawner) {
                                    plugin.send(player, "&7Spawner Information:");
                                    plugin.send(player, "&7Type:&f " + creatureSpawner.getSpawnedType());
                                    plugin.send(player, "&7Level:&f " + creatureSpawner.getSpawnCount());
                                }
                            }
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    if (player.hasPermission("spawners.command.spawners.reload")) {
                        plugin.reload();
                        plugin.send(player, "&6Spawners:&f reloaded");
                    }
                }
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (player.hasPermission("spawners.command.spawners.check")) {
                    commands.add("check");
                }
                if (player.hasPermission("spawners.command.spawners.reload")) {
                    commands.add("reload");
                }
            }
        }
        return commands;
    }
}
