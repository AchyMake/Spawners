package org.achymake.spawners.listeners;

import org.achymake.spawners.Spawners;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public record PlayerInteract(Spawners plugin) implements Listener {
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))return;
        if (event.getHand() != EquipmentSlot.HAND)return;
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        Block block = event.getClickedBlock();
        if (block == null)return;
        if (!block.getType().equals(Material.SPAWNER))return;
        if (!player.isSneaking())return;
        if (!heldItem.getType().equals(Material.SPAWNER))return;
        if (!(block.getState() instanceof CreatureSpawner creatureSpawner))return;
        ItemMeta itemMeta = heldItem.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        if (container.has(NamespacedKey.minecraft("count"), PersistentDataType.INTEGER)) {
            int level = container.get(NamespacedKey.minecraft("count"), PersistentDataType.INTEGER);
            if (plugin.getConfig().getInt("max-level") > creatureSpawner.getSpawnCount()) {
                int newLevel = creatureSpawner.getSpawnCount() + level;
                creatureSpawner.setSpawnCount(newLevel);
                creatureSpawner.update();
                heldItem.setAmount(heldItem.getAmount() - 1);
                event.setCancelled(true);
            }
        }
    }
}
