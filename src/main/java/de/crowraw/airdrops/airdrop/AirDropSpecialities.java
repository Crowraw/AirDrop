package de.crowraw.airdrops.airdrop;/*
   _____                                      
 / ____|                                     
| |     _ __ _____      ___ __ __ ___      __
| |    | '__/ _ \ \ /\ / / '__/ _` \ \ /\ / /
| |____| | | (_) \ V  V /| | | (_| |\ V  V / 
 \_____|_|  \___/ \_/\_/ |_|  \__,_| \_/\_/  
    
    
    Crowraw#9875 for any questions
    Date: 30.09.2021
    
    
    
 */

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.HologramLine;
import de.crowraw.airdrops.AirDrops;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class AirDropSpecialities {
    private final Location location;
    private final AirDrops plugin;
    private int secondsTillRemove;
    private boolean runTimer = true;
    private boolean opened = false;
    private Hologram hologram;
    private boolean spawnedHolos = false;

    public AirDropSpecialities(Location location, AirDrops plugin, int secondsTillRemove) {
        this.location = location;
        this.plugin = plugin;
        this.secondsTillRemove = secondsTillRemove;
        if (secondsTillRemove == 1) {
            runTimer = false;
        }


        setUpArmorStand();
        spawnMobs();
    }

    private void setUpArmorStand() {
        Location preparedLoc = location.clone();
        preparedLoc.add(0, 2, 0);

    }

    public void spawnMobs() {
        if (plugin.getConfigUtil().getYamlConfiguration().get("mobs") == null) {
            List<EntityType> list = new ArrayList<>();
            list.add(EntityType.PIG);
            list.add(EntityType.ZOMBIE);

            list.forEach(entityType -> plugin.getConfigUtil().getYamlConfiguration().set("mobs." + entityType, 1));
            plugin.getConfigUtil().saveConfig();
        }
        if (Boolean.parseBoolean(plugin.getConfigUtil().getStringMessage("false", "mobs_as_guards"))) {
            plugin.getConfigUtil().getYamlConfiguration().getConfigurationSection("mobs").getKeys(false).forEach(string -> {
                Location preparedLoc = location.clone();
                preparedLoc.add(0, 2, 0);
                for (int i = 0; i < plugin.getConfigUtil().getYamlConfiguration().getInt("mobs." + string); i++) {
                    location.getWorld().spawnEntity(location, EntityType.valueOf(string));
                }

            });
        }
    }

    public boolean check() {


        if (runTimer)
            secondsTillRemove--;
        if (secondsTillRemove == 0) {
            deleteChest();
        }
        if (getLocation().getBlock().getState() instanceof Chest) {
            Chest chest = (Chest) getLocation().getBlock().getState();
            if (isEmpty(chest.getInventory())) {
                deleteChest();
                return true;
            }
        } else {
            deleteChest();
            return true;
        }
        if (!spawnedHolos) {
            hologram = HologramsAPI.createHologram(plugin, getCenter(getLocation()));
            spawnedHolos = true;
            loadHoloGramLines();
        }

        if (Boolean.parseBoolean(plugin.getConfigUtil().getStringMessage("true", "refresh_holo"))) {
            loadHoloGramLines();
        }
        return false;
    }

    public Location getLocation() {
        return location;
    }

    public void openedChest() {
        if (!opened && Boolean.parseBoolean(plugin.getConfigUtil().getStringMessage("false", "command.active"))) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    plugin.getConfigUtil().getStringMessage("/CommandHere $player$ " +
                            "# this will execute the command in console. Example: /eco add $player$ 10", "command.execution"));
            opened = true;
        }
    }

    private boolean isEmpty(Inventory inventory) {
        return Arrays.stream(inventory.getContents()).filter(Objects::nonNull).noneMatch(itemStack -> itemStack.getType() != Material.AIR);
    }

    public void deleteChest() {
        getLocation().getBlock().setType(Material.AIR);
        hologram.delete();
    }

    private void loadHoloGramLines() {
        hologram.delete();
        hologram = HologramsAPI.createHologram(plugin, getCenter(getLocation()));
        String[] lines = plugin.getConfigUtil().getStringMessage("§2Cool hologram!\n" +
                "§adoggos\n§c$timer$ §atill remove", "hologram.message").split("\n");
        for (String line : lines) {
            hologram.appendTextLine(line.replace("$timer$", "" + secondsTillRemove));
        }

    }


    private Location getCenter(Location loc) {
        return new Location(loc.getWorld(),
                getRelativeCoord(loc.getBlockX()),
                loc.getY() + 2.0d,
                getRelativeCoord(loc.getBlockZ()));
    }

    private double getRelativeCoord(int i) {
        return i + 0.5d;
    }

}
