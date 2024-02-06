package org.achymake.spawners.listeners;

import org.achymake.spawners.Spawners;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public record BlockPlace(Spawners plugin) implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (!block.getType().equals(Material.SPAWNER))return;
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = heldItem.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (!container.has(NamespacedKey.minecraft("entity"), PersistentDataType.STRING))return;
        String entityTypeString = container.get(NamespacedKey.minecraft("entity"), PersistentDataType.STRING);
        int level = container.get(NamespacedKey.minecraft("count"), PersistentDataType.INTEGER);
        CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
        creatureSpawner.setSpawnedType(EntityType.valueOf(entityTypeString));
        creatureSpawner.setSpawnCount(Math.max(level, 1));
        creatureSpawner.update();
    }
}
