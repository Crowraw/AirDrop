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
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AirDropMechanic implements de.crowraw.airdrops.airdrop.AirDropMechanic {
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
        this.location = getRandomLocation();

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
                sendAirDrop();
                this.timeElapsed = 0;
                this.location = getRandomLocation();
            }
            if (timeElapsed >= Integer.parseInt(plugin.getConfigUtil().getStringMessage(String.valueOf((60 * 9 + 30)), "time_till_prepare"))) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    String keyAsString = "entity.lightning_bolt.impact";
                    playMusicByKey(keyAsString, location);
                    EntityLightning lightning = new EntityLightning(EntityTypes.U, ((CraftWorld) location.getWorld()).getHandle());

                    Vec3D vec = new Vec3D(0, 0, 0);

                    PacketPlayOutSpawnEntity lightningPacket = new PacketPlayOutSpawnEntity(lightning.getId(), lightning.getUniqueID(),
                            location.getX(),
                            location.getY(),
                            location.getZ(), 0f, 0f, EntityTypes.U, 0, vec);


                    ((CraftPlayer) onlinePlayer).getHandle().b.sendPacket(lightningPacket);

                    ((CraftPlayer) onlinePlayer).getHandle().b.
                            sendPacket(new PacketPlayOutExplosion(location.getX(),
                                    location.getY(), location.getZ(), 10,
                                    Collections.emptyList(), new Vec3D(0, 0, 0)));

                    ((CraftPlayer) onlinePlayer).getHandle().b.
                            sendPacket(new PacketPlayOutNamedSoundEffect(new SoundEffect(MinecraftKey.a("ambient.weather.thunder")), SoundCategory.d, location.getX(),
                                    location.getY(), location.getZ(), 0.00001f, 1f));

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

    public void sendAirDrop() {


        List<ItemStack> itemStacks = new ArrayList<>();


        for (int i = 0; i < plugin.getConfigUtil().getYamlConfiguration().getConfigurationSection("items").getKeys(false).size(); i++) {

            if (plugin.getConfigUtil().getYamlConfiguration().get("items." + i) == null) {
                continue;
            }
            itemStacks.add(plugin.getConfigUtil().getYamlConfiguration().getItemStack("items." + i));
        }

        Collections.shuffle(itemStacks);
        itemStacks = itemStacks.stream().limit(5).collect(Collectors.toList());
        new AirDrop(location, itemStacks
                , plugin).spawnAirDrop(antiLag);
    }

    private Location getRandomLocation() {
        List<Location> locations = new ArrayList<>();
        int size = plugin.getConfigUtil().getYamlConfiguration().getConfigurationSection("location").getKeys(false).size();
        for (int i = 0; i < size; i++) {
            locations.add(plugin.getConfigUtil().getLocationFromId(i));

        }
        Collections.shuffle(locations);
        return locations.get(0);
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
