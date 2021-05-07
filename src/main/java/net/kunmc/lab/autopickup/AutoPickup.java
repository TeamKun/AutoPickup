package net.kunmc.lab.autopickup;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class AutoPickup extends JavaPlugin implements Listener {
    private static class PlayerData {
        public Block lastLookingBlock;
    }

    private Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("autopickup")) {
            UUID uuid = player.getUniqueId();
            PlayerData data = playerDataMap.computeIfAbsent(uuid, id -> new PlayerData());
            Block lookingBlock = player.getTargetBlock(4);
            if (lookingBlock != null && !Objects.equals(data.lastLookingBlock, lookingBlock)) {
                data.lastLookingBlock = lookingBlock;
                PlayerInventory inventory = player.getInventory();
                inventory.addItem(new ItemStack(lookingBlock.getType()));
            }
        }
    }
}
