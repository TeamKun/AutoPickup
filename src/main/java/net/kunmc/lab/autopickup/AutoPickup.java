package net.kunmc.lab.autopickup;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
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
        public boolean jumping;
        public boolean sneaking;
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
    public void onSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("autopickup")) {
            UUID uuid = player.getUniqueId();
            PlayerData data = playerDataMap.computeIfAbsent(uuid, id -> new PlayerData());
            if (player.hasPermission("autopickup.sneak")) {
                boolean sneaking = event.isSneaking();
                if (data.sneaking != sneaking) {
                    data.sneaking = sneaking;
                    if (sneaking) {
                        Block standingBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
                        PlayerInventory inventory = player.getInventory();
                        inventory.addItem(new ItemStack(standingBlock.getType()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("autopickup")) {
            UUID uuid = player.getUniqueId();
            PlayerData data = playerDataMap.computeIfAbsent(uuid, id -> new PlayerData());
            if (player.hasPermission("autopickup.look")) {
                Block lookingBlock = player.getTargetBlock(4);
                if (lookingBlock != null && !Objects.equals(data.lastLookingBlock, lookingBlock)) {
                    data.lastLookingBlock = lookingBlock;
                    PlayerInventory inventory = player.getInventory();
                    inventory.addItem(new ItemStack(lookingBlock.getType()));
                }
            }
            if (player.hasPermission("autopickup.jump")) {
                boolean jumping = event.getFrom().getY() < event.getTo().getY() && !player.isSwimming() && !player.isFlying();
                if (data.jumping != jumping) {
                    data.jumping = jumping;
                    if (jumping) {
                        Block standingBlock = event.getTo().getBlock().getRelative(BlockFace.DOWN);
                        PlayerInventory inventory = player.getInventory();
                        inventory.addItem(new ItemStack(standingBlock.getType()));
                    }
                }
            }
        }
    }
}
