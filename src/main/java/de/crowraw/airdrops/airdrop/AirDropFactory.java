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
import org.bukkit.Bukkit;

public class AirDropFactory {


    public static AirDropMechanic getAirDropMechanic(String version, AirDrops plugin) {
        if (version.contains("1.8")) {
            return new de.crowraw.airdrops.v1_8.mechanic.AirDropMechanic(plugin);
        }
        if (version.contains("1.17")) {
            return new de.crowraw.airdrops.v1_17.mechanic.AirDropMechanic(plugin);
        }
        System.out.println("Not supported version!");
        return null;
    }
}