package org.achymake.spawners.listeners;

import org.achymake.spawners.Spawners;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public record BlockBreak(Spawners plugin) implements Listener {
    private FileConfiguration getConfig() {
        return plugin.getConfig();
    }
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.getGameMode().equals(GameMode.SURVIVAL))return;
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (!Tag.ITEMS_PICKAXES.isTagged(heldItem.getType()))return;
        Block block = event.getBlock();
        if (block.getType().equals(Material.SPAWNER)) {
            CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
            String entityTypeString = creatureSpawner.getSpawnedType().toString();
            int level = creatureSpawner.getSpawnCount();
            if (player.hasPermission("spawners.event.block-break.spawner")) {
                if (!heldItem.containsEnchantment(Enchantment.SILK_TOUCH))return;
                if (player.isSneaking()) {
                    if (level >= 2) {
                        int newLevel = level - 1;
                        creatureSpawner.setSpawnCount(newLevel);
                        creatureSpawner.update();
                        player.getWorld().dropItem(block.getLocation().add(0.5,0.5,0.5), getSpawner(entityTypeString, 1));
                        event.setCancelled(true);
                    } else {
                        player.getWorld().dropItem(block.getLocation().add(0.5,0.5,0.5), getSpawner(entityTypeString, 1));
                        event.setExpToDrop(0);
                    }
                } else {
                    player.getWorld().dropItem(block.getLocation().add(0.5,0.5,0.5), getSpawner(entityTypeString, level));
                    event.setExpToDrop(0);
                }
            } else if (player.hasPermission("spawners.event.block-break.spawner.no-silk")) {
                if (player.isSneaking()) {
                    if (level >= 2) {
                        int newLevel = level - 1;
                        creatureSpawner.setSpawnCount(newLevel);
                        creatureSpawner.update();
                        player.getWorld().dropItem(block.getLocation().add(0.5,0.5,0.5), getSpawner(entityTypeString, 1));
                        event.setCancelled(true);
                    } else {
                        player.getWorld().dropItem(block.getLocation().add(0.5,0.5,0.5), getSpawner(entityTypeString, 1));
                        event.setExpToDrop(0);
                    }
                } else {
                    player.getWorld().dropItem(block.getLocation().add(0.5,0.5,0.5), getSpawner(entityTypeString, level));
                    event.setExpToDrop(0);
                }
            }
        } else if (block.getType().equals(Material.BUDDING_AMETHYST)) {
            if (player.hasPermission("spawners.event.block-break.budding-amethyst")) {
                if (!heldItem.containsEnchantment(Enchantment.SILK_TOUCH))return;
                ItemStack amethyst = new ItemStack(block.getType(), 1);
                player.getWorld().dropItem(block.getLocation().add(0.5,0.5,0.5), amethyst);
            } else if (player.hasPermission("spawners.event.block-break.budding-amethyst.no-silk")) {
                ItemStack amethyst = new ItemStack(block.getType(), 1);
                player.getWorld().dropItem(block.getLocation().add(0.5,0.5,0.5), amethyst);
            }
        }
    }
    private ItemStack getSpawner(String entityType, int level) {
        ItemStack spawner = new ItemStack(Material.SPAWNER, 1);
        ItemMeta itemMeta = spawner.getItemMeta();
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        persistentDataContainer.set(NamespacedKey.minecraft("entity"), PersistentDataType.STRING, entityType);
        persistentDataContainer.set(NamespacedKey.minecraft("count"), PersistentDataType.INTEGER, Math.max(level, 1));
        if (getConfig().isString("spawners." + entityType + ".name")) {
            itemMeta.setDisplayName(plugin.addColor(getConfig().getString("spawners." + entityType + ".name")));
        }
        if (getConfig().isList("spawners." + entityType + ".lore")) {
            List<String> lore = new ArrayList<>();
            for (String list : getConfig().getStringList("spawners." + entityType + ".lore")) {
                lore.add(plugin.addColor(MessageFormat.format(list, level)));
            }
            itemMeta.setLore(lore);
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        spawner.setItemMeta(itemMeta);
        return spawner;
    }
}
