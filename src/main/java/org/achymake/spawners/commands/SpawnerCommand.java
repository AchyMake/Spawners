package org.achymake.spawners.commands;

import org.achymake.spawners.Spawners;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SpawnerCommand implements CommandExecutor, TabCompleter {
    private final Spawners plugin;
    public SpawnerCommand(Spawners plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 1) {
                giveSpawner(player, getSpawner(args[0].toUpperCase(), 1, 1));
            }
            if (args.length == 2) {
                giveSpawner(player, getSpawner(args[0].toUpperCase(), Integer.parseInt(args[1]), 1));
            }
            if (args.length == 3) {
                giveSpawner(player, getSpawner(args[0].toUpperCase(), Integer.parseInt(args[1]), Integer.parseInt(args[2])));
            }
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> commands = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 1) {
                for (String listed : plugin.getConfig().getConfigurationSection("spawners").getKeys(false)) {
                    commands.add(listed.toLowerCase());
                }
            }
            if (args.length == 2) {
                commands.add("1");
                commands.add("2");
                commands.add("5");
            }
            if (args.length == 3) {
                commands.add("1");
                commands.add("2");
                commands.add("3");
            }
        }
        return commands;
    }
    private void giveSpawner(Player player, ItemStack itemStack) {
        if (Arrays.asList(player.getInventory().getStorageContents()).contains(null)) {
            player.getInventory().addItem(itemStack);
        } else {
            player.getWorld().dropItem(player.getLocation(), itemStack);
        }
    }
    private ItemStack getSpawner(String entityTypeUpperCase, int amount, int level) {
        ItemStack spawner = new ItemStack(Material.SPAWNER, amount);
        ItemMeta itemMeta = spawner.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(NamespacedKey.minecraft("entity"), PersistentDataType.STRING, entityTypeUpperCase);
        persistentDataContainer.set(NamespacedKey.minecraft("count"), PersistentDataType.INTEGER, Math.max(level, 1));
        if (plugin.getConfig().isString("spawners." + entityTypeUpperCase + ".name")) {
            itemMeta.setDisplayName(plugin.addColor(plugin.getConfig().getString("spawners." + entityTypeUpperCase + ".name")));
        }
        if (plugin.getConfig().isList("spawners." + entityTypeUpperCase + ".lore")) {
            List<String> lore = new ArrayList<>();
            for (String list : plugin.getConfig().getStringList("spawners." + entityTypeUpperCase + ".lore")) {
                lore.add(plugin.addColor(MessageFormat.format(list, level)));
            }
            itemMeta.setLore(lore);
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        spawner.setItemMeta(itemMeta);
        return spawner;
    }
}
