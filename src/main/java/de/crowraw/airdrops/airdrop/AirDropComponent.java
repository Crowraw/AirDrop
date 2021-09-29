package de.crowraw.airdrops.airdrop;/*
   _____                                      
 / ____|                                     
| |     _ __ _____      ___ __ __ ___      __
| |    | '__/ _ \ \ /\ / / '__/ _` \ \ /\ / /
| |____| | | (_) \ V  V /| | | (_| |\ V  V / 
 \_____|_|  \___/ \_/\_/ |_|  \__,_| \_/\_/  
    
    
    Crowraw#9875 for any questions
    Date: 29.09.2021
    
    
    
 */

import de.crowraw.airdrops.AirDrops;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AirDropComponent {
    private Location location;

    public void createFireWork(AirDrops plugin, FallingBlock fallingBlock) {
        Firework firework = (Firework) fallingBlock.getWorld().spawnEntity(fallingBlock.getLocation().add(0, 1, 0), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder()
                .withColor(Color.AQUA, Color.RED, Color.ORANGE, Color.BLACK, Color.GREEN, Color.BLACK, Color.LIME)
                .flicker(true)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build());
        firework.setFireworkMeta(fireworkMeta);
        fireworkMeta.setPower(20);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, firework::detonate, 4);
    }

    public Location getRandomLocation(AirDrops plugin) {
        List<Location> locations = new ArrayList<>();
        int size = plugin.getConfigUtil().getYamlConfiguration().getConfigurationSection("location").getKeys(false).size();
        for (int i = 0; i < size; i++) {
            locations.add(plugin.getConfigUtil().getLocationFromId(i));

        }
        Collections.shuffle(locations);
        return locations.get(0);
    }

    public List<ItemStack> prepareItems(AirDrops plugin) {

        List<ItemStack> itemStacks = new ArrayList<>();

        for (int i = 0; i < plugin.getConfigUtil().getYamlConfiguration().getConfigurationSection("items").getKeys(false).size(); i++) {

            if (plugin.getConfigUtil().getYamlConfiguration().get("items." + i) == null) {
                continue;
            }
            itemStacks.add(plugin.getConfigUtil().getYamlConfiguration().getItemStack("items." + i));
        }

        Collections.shuffle(itemStacks);
        itemStacks = itemStacks.stream().limit(5).collect(Collectors.toList());
     return itemStacks;
    }

    public void groundTouch(AirDrops plugin, FallingBlock fallingBlock, List<ItemStack> itemStacks) {
        Location location = fallingBlock.getLocation();

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> location.getBlock().setType(Material.CHEST), 20);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (location.getBlock().getState() instanceof Chest) {
                Chest chest = (Chest) location.getBlock().getState();
                itemStacks.forEach(itemStack -> {
                    if (itemStack != null)
                        chest.getInventory().addItem(itemStack);
                });
            }
            plugin.getLocations().add(location);
        }, 40);


        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> location.getBlock().setType(Material.AIR), 20 * 60 * 4);

    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
