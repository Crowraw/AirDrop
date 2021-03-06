package de.crowraw.airdrops.v1_17.mechanic;/*
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
import de.crowraw.airdrops.v1_17.entitiy.AirDrop;
import net.minecraft.network.protocol.game.PacketPlayOutExplosion;
import net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
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
                    String keyAsString = "entity.lightning_bolt.impact";
                    playMusicByKey(keyAsString, getLocation());
                    EntityLightning lightning = new EntityLightning(EntityTypes.U, ((CraftWorld) getLocation().getWorld()).getHandle());

                    Vec3D vec = new Vec3D(0, 0, 0);

                    PacketPlayOutSpawnEntity lightningPacket = new PacketPlayOutSpawnEntity(lightning.getId(), lightning.getUniqueID(),
                            getLocation().getX(),
                            getLocation().getY(),
                            getLocation().getZ(), 0f, 0f, EntityTypes.U, 0, vec);


                    ((CraftPlayer) onlinePlayer).getHandle().b.sendPacket(lightningPacket);

                    ((CraftPlayer) onlinePlayer).getHandle().b.
                            sendPacket(new PacketPlayOutExplosion(getLocation().getX(),
                                    getLocation().getY(), getLocation().getZ(), 10,
                                    Collections.emptyList(), new Vec3D(0, 0, 0)));

                    ((CraftPlayer) onlinePlayer).getHandle().b.
                            sendPacket(new PacketPlayOutNamedSoundEffect(new SoundEffect(MinecraftKey.a("ambient.weather.thunder")), SoundCategory.d, getLocation().getX(),
                                    getLocation().getY(), getLocation().getZ(), 0.00001f, 1f));

                }

            }


        }, 20, 20);
    }

    public static void playMusicByKey(String keyAsString, Location location) {
        MinecraftKey key = new MinecraftKey(keyAsString);
        SoundEffect effect = new SoundEffect(key);

        PacketPlayOutNamedSoundEffect packet;
        packet = new PacketPlayOutNamedSoundEffect(effect, SoundCategory.h,
                location.getX(),
                location.getY(),
                location.getZ(), 1f, 1f);
        Bukkit.getOnlinePlayers().forEach(player -> ((CraftPlayer) player).getHandle().b.sendPacket(packet));
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
