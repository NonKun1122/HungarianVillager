package com.nonkungch.HungarianVillager;

import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class VillagerListener implements Listener {
    private final HungarianVillager plugin;

    public VillagerListener(HungarianVillager plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            // เช็คว่ามีชื่ออยู่แล้วหรือไม่
            if (villager.getCustomName() != null) return;

            String name = NameGenerator.generate(plugin);
            villager.setCustomName(name);
            villager.setCustomNameVisible(true);
            
            plugin.logToFile("SPAWN: " + name + " (Location: " + villager.getLocation().getBlockX() + ", " + villager.getLocation().getBlockZ() + ")");
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Villager villager) {
            if (villager.getCustomName() == null) return;
            
            String cause = (villager.getLastDamageCause() != null) ? villager.getLastDamageCause().getCause().name() : "UNKNOWN";
            plugin.logToFile("DEATH: " + villager.getCustomName() + " | Cause: " + cause);
        }
    }
}
