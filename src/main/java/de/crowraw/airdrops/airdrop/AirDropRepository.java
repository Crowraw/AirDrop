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

import de.crowraw.airdrops.AirDrops;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AirDropRepository {
    private static AirDropRepository instance;
    private final List<AirDropSpecialities> airDropSpecialities = new ArrayList<>();

    public AirDropRepository(AirDrops plugin) {
        startCheckingScheduler(plugin);
    }

    private void startCheckingScheduler(AirDrops plugin) {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            final List<AirDropSpecialities> specs = new ArrayList<>();
            airDropSpecialities.forEach(airDropSpecialities -> {
                if (airDropSpecialities==null||airDropSpecialities.check()) {
                    specs.add(airDropSpecialities);
                }
            });

            airDropSpecialities.removeAll(specs);
        }, 20, 20);
    }


    public static AirDropRepository getInstance(AirDrops airDrops) {
        if (instance == null) {
            instance = new AirDropRepository(airDrops);
        }
        return instance;
    }

    public Optional<AirDropSpecialities> getAirDrop(Location location) {
        return airDropSpecialities.stream().filter(airDropSpec -> airDropSpec.getLocation().equals(location)).findAny();
    }
public void addChestLocation(Location location,AirDrops plugin){
        airDropSpecialities.add(new AirDropSpecialities(location,plugin,Integer.parseInt(plugin.getConfigUtil().
                getStringMessage("600# this is in seconds. Set it to 1 if you dont want to make the timer work",
                        "time_till_remove_chest").split("#")[0])));
}

}
