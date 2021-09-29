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
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;

public class AirDropMechanic extends AirDropComponent implements de.crowraw.airdrops.airdrop.AirDropInterface {
    private final AirDrops plugin;

    private boolean antiLag;
    private int timeElapsed = 0;
    private Location location;
    private boolean start = false;

    public AirDropMechanic(AirDrops plugin) {
        this.plugin = plugin;

        if (plugin.getConfigUtil().getYamlConfiguration().getConfigurationSection("location") == null) {
            plugin.getConfigUtil().loadLocation(99, new Location(Bukkit.getWorld("world"), 1, 1, 1));
        }

        if (plugin.getConfigUtil().getYamlConfiguration().get("items.0") == null) {
            plugin.getConfigUtil().getYamlConfiguration().set("items.0", new ItemStack(Material.EMERALD));
            plugin.getConfigUtil().saveConfig();
        }
        startScheduler();
    }

    private void startScheduler() {
        this.location = getRandomLocation(plugin);

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            if (!start) {
                if (Bukkit.getOnlinePlayers().size() <
                        Integer.parseInt(plugin.getConfigUtil().getStringMessage("40", "playersrequired"))) {
                    return;
                }
            }

            timeElapsed++;
            if (timeElapsed == Integer.parseInt(plugin.getConfigUtil().getStringMessage(String.valueOf((60 * 9 + 30)), "time_till_prepare"))) {
                Bukkit.getOnlinePlayers().forEach(player ->
                        player.sendMessage("§4§lWarning: AirDrop coming at: " + location.getX() + "X and " + location.getZ() + " Z. "));
            }
            if (this.timeElapsed >= Integer.parseInt(plugin.getConfigUtil().getStringMessage(String.valueOf((60 * 10)), "time_till_airdrop"))) {
                start = false;
                sendAirDrop(plugin, location, antiLag);
                this.timeElapsed = 0;
                this.location = getRandomLocation(plugin);
            }
            if (timeElapsed >= Integer.parseInt(plugin.getConfigUtil().getStringMessage(String.valueOf((60 * 9 + 30)), "time_till_prepare"))) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.playSound(location, Sound.AMBIENCE_THUNDER, 1f, 1f);
                    ((CraftPlayer) onlinePlayer).getHandle().playerConnection.
                            sendPacket(new PacketPlayOutSpawnEntityWeather(new EntityLightning((((CraftWorld) location.getWorld()).getHandle()),
                                    location.getX(), location.getY(), location.getZ(),
                                    false, false)));

                    ((CraftPlayer) onlinePlayer).getHandle().playerConnection.
                            sendPacket(new PacketPlayOutExplosion(location.getX(),
                                    location.getY(), location.getZ(), 10,
                                    Collections.emptyList(), new Vec3D(0, 0, 0)));

                    ((CraftPlayer) onlinePlayer).getHandle().playerConnection.
                            sendPacket(new PacketPlayOutNamedSoundEffect("ambient.weather.thunder", location.getX(),
                                    location.getY(), location.getZ(), 0.00001f, 1f));

                }

            }


        }, 20, 20);
    }


    public void setAntiLag(boolean antiLag) {
        this.antiLag = antiLag;
    }

    public void setTimeElapsed(int timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isAntiLag() {
        return antiLag;
    }
}
