package de.crowraw.airdrops.v1_8.entitiy;/*
   _____                                      
 / ____|                                     
| |     _ __ _____      ___ __ __ ___      __
| |    | '__/ _ \ \ /\ / / '__/ _` \ \ /\ / /
| |____| | | (_) \ V  V /| | | (_| |\ V  V / 
 \_____|_|  \___/ \_/\_/ |_|  \__,_| \_/\_/  
    
    
    Crowraw#9875 for any questions
    Date: 12.09.2021
    
    
    
 */


import de.crowraw.airdrops.AirDrops;
import de.crowraw.airdrops.airdrop.AirDropComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutExplosion;
import net.minecraft.server.v1_8_R3.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class AirDrop extends AirDropComponent {

    private final Location location;
    private final List<ItemStack> itemStacks;
    private AirDrops plugin;

    public AirDrop(Location location, List<ItemStack> itemStacks, AirDrops plugin) {
        this.location = location;
        this.itemStacks = itemStacks;
        this.plugin = plugin;
    }

    public void spawnAirDrop(boolean antiLag) {
        if (this.location == null) {
            plugin.getLogger().info("Need to setup correctly!");
            return;
        }
        location.setY(120);


        FallingBlock fallingBlock = this.location.getWorld().spawnFallingBlock(this.location, Material.CHEST, (byte) 0);
        fallingBlock.setDropItem(false);

        new BukkitRunnable() {
            @Override
            public void run() {


                if (fallingBlock.isOnGround()) {

                    groundTouch(plugin, fallingBlock, itemStacks);
                    location.getWorld().playSound(location, Sound.EXPLODE, 1f, 1f);
                    Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().playerConnection.
                            sendPacket(new PacketPlayOutExplosion(location.getX(),
                                    location.getY(), location.getZ(), 10,
                                    Collections.emptyList(), new Vec3D(0, 0, 0))));


                    this.cancel();
                    fallingBlock.remove();
                    return;
                }
                if (!antiLag) {
                    createFireWork(plugin, fallingBlock);
                    fallingBlock.getWorld().playSound(fallingBlock.getLocation(), Sound.LEVEL_UP, 0.5f, 1f);
                }

            }
        }.runTaskTimer(this.plugin, 0, 0);


    }


}