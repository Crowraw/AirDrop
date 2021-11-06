package de.crowraw.airdrops.v1_8.mechanic;/*
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
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class AirDropMechanic extends AirDropComponent implements de.crowraw.airdrops.airdrop.AirDropInterface {
    private final AirDrops plugin;

    public AirDropMechanic(AirDrops plugin) {
        super(plugin);
        this.plugin = plugin;

        if (plugin.getConfigUtil().getYamlConfiguration().getConfigurationSection("location") == null) {
            return;
        }

        if (plugin.getConfigUtil().getYamlConfiguration().get("items.0") == null) {
            plugin.getConfigUtil().getYamlConfiguration().set("items.0", new ItemStack(Material.EMERALD));
            plugin.getConfigUtil().saveConfig();
        }
        startScheduler();
    }

    private void startScheduler() {
        setLocation(getRandomLocation(plugin));

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            airDropStartChecker();

            if (getTimeElapsed() >= Integer.parseInt(plugin.getConfigUtil().getStringMessage(String.valueOf((60 * 9 + 30)), "time_till_prepare"))) {

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    ((CraftPlayer) onlinePlayer).getHandle().playerConnection.
                            sendPacket(new PacketPlayOutSpawnEntityWeather(new EntityLightning((((CraftWorld) getLocation().getWorld()).getHandle()),
                                    getLocation().getX(), getLocation().getY(), getLocation().getZ(),
                                    false, false)));

                    ((CraftPlayer) onlinePlayer).getHandle().playerConnection.
                            sendPacket(new PacketPlayOutExplosion(getLocation().getX(),
                                    getLocation().getY(), getLocation().getZ(), 10,
                                    Collections.emptyList(), new Vec3D(0, 0, 0)));

                    onlinePlayer.playSound(getLocation(), "random.explode", 1f, 1f);


                }

            }


        }, 20, 20);
    }

    public void setAntiLag(boolean antiLag) {
        antiLag(antiLag);
    }

    public void setTimeElapsed(int timeElapsed) {
        timeElapsed(timeElapsed);
    }

    public void setStart(boolean start) {
        start(start);
    }

    public boolean isAntiLag() {
        return getAntiLag();
    }
}
