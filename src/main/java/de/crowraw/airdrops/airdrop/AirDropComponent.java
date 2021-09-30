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
import de.crowraw.airdrops.v1_17.entitiy.AirDrop;
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
    private int timeElapsed = 0;
    private boolean antiLag;
    private boolean start = false;
    private AirDrops plugin;

    public AirDropComponent(AirDrops plugin) {
        this.plugin = plugin;
    }

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

        int i = 0;

        while (size != locations.size()) {
            if (plugin.getConfigUtil().getYamlConfiguration().get("location." + i) != null) {
                locations.add(plugin.getConfigUtil().getLocationFromId(i));
            }
            i++;
        }
        Collections.shuffle(locations);
        if (locations.get(0) == null) {
            plugin.getLogger().info("The Location is not valid! ID:" + i);
        }
        return locations.get(0);
    }

    public List<ItemStack> prepareItems(AirDrops plugin) {

        List<ItemStack> itemStacks = new ArrayList<>();
        int size = plugin.getConfigUtil().getYamlConfiguration().getConfigurationSection("items").getKeys(false).size();

        int i = 0;

        while (size != itemStacks.size()) {
            if (plugin.getConfigUtil().getYamlConfiguration().get("items." + i) != null) {
                itemStacks.add(plugin.getConfigUtil().getYamlConfiguration().getItemStack("items." + i));
            }
            i++;
        }
        Collections.shuffle(itemStacks);
        itemStacks = itemStacks.stream().limit(Integer.parseInt(plugin.getConfigUtil().getStringMessage("5", "items_amount_in_chest"))).collect(Collectors.toList());
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

    public void sendMessage(AirDrops plugin) {

        String message = plugin.getConfigUtil().getStringMessage(
                "§4§lWarning: AirDrop coming at: $x$ X and $z$ Z.",
                "message.send").replace("$x$", "" + getLocation().getX()).replace("$z$", "" + getLocation().getZ());

        Bukkit.getOnlinePlayers().forEach(player ->
                player.sendMessage(message));
    }

    public Location getLocation() {
        return location;
    }

    public void airDropStartChecker() {
        if (!start) {
            if (Bukkit.getOnlinePlayers().size() <
                    Integer.parseInt(plugin.getConfigUtil().getStringMessage("40", "playersrequired"))) {
                return;
            }
        }

        timeElapsed++;
        if (timeElapsed == Integer.parseInt(plugin.getConfigUtil().getStringMessage(String.valueOf((60 * 9 + 30)), "time_till_prepare"))) {
            sendMessage(plugin);
        }
        if (this.timeElapsed >= Integer.parseInt(plugin.getConfigUtil().getStringMessage(String.valueOf((60 * 10)), "time_till_airdrop"))) {
            start = false;
            if (Bukkit.getVersion().contains("1.8")) {
                new de.crowraw.airdrops.v1_8.entitiy.AirDrop(getLocation(), prepareItems(plugin)
                        , plugin).spawnAirDrop(antiLag);
            }
            if (Bukkit.getVersion().contains("1.17")) {
                new de.crowraw.airdrops.v1_17.entitiy.AirDrop(getLocation(), prepareItems(plugin)
                        , plugin).spawnAirDrop(antiLag);
            }
            if (Bukkit.getVersion().contains("1.16")) {
                new de.crowraw.airdrops.v1_16.entitiy.AirDrop(getLocation(), prepareItems(plugin)
                        , plugin).spawnAirDrop(antiLag);
            }


            this.timeElapsed = 0;
            setLocation(getRandomLocation(plugin));
        }
    }


    public int getTimeElapsed() {
        return timeElapsed;
    }

    public void timeElapsed(int timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void antiLag(boolean antiLag) {
        this.antiLag = antiLag;
    }

    public void start(boolean start) {
        this.start = start;
    }

    public boolean start() {
        return start;
    }

    public boolean getAntiLag() {
        return antiLag;
    }
}
