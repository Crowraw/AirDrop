package de.crowraw.airdrops.v1_12.mechanic;/*
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
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
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
                    onlinePlayer.playSound(getLocation(), "entity.generic.explode", 1f, 1f);


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

    public static void playMusicByKey(String keyAsString, Location location) {
        net.minecraft.server.v1_12_R1.MinecraftKey key = new net.minecraft.server.v1_12_R1.MinecraftKey(keyAsString);
        net.minecraft.server.v1_12_R1.SoundEffect effect = new net.minecraft.server.v1_12_R1.SoundEffect(key);

        net.minecraft.server.v1_12_R1.PacketPlayOutNamedSoundEffect packet;
        packet = new net.minecraft.server.v1_12_R1.PacketPlayOutNamedSoundEffect(effect, net.minecraft.server.v1_12_R1.SoundCategory.PLAYERS,
                location.getX(),
                location.getY(),
                location.getZ(), 1f, 1f);
        Bukkit.getOnlinePlayers().forEach(player -> ((org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer) player).getHandle().playerConnection.sendPacket(packet));
    }
}
