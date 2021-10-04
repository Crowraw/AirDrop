package de.crowraw.airdrops;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import de.crowraw.airdrops.airdrop.AirDropFactory;
import de.crowraw.airdrops.airdrop.AirDropInterface;
import de.crowraw.airdrops.airdrop.AirDropRepository;
import de.crowraw.airdrops.airdrop.AirDropSpecialities;
import de.crowraw.airdrops.listener.ChestListener;
import de.crowraw.lib.data.ConfigHelper;
import de.crowraw.airdrops.command.AirDropCommand;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class AirDrops extends JavaPlugin {
    private final ConfigHelper configUtil = new ConfigHelper("plugins//AirDrop//config.yml");
    private AirDropInterface airDropMechanic;
/*
Next update:
cannot take any items out of chest, added config option for 1.8
 */

    @Override
    public void onEnable() {
        // Plugin startup logic
        configUtil.saveConfig();
        new ChestListener(this);
        new AirDropCommand(this);
        airDropMechanic = AirDropFactory.getAirDropMechanic(Bukkit.getVersion(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        AirDropRepository.getInstance(this).getAirDropSpecialities().forEach(AirDropSpecialities::deleteChest);
    }

    public ConfigHelper getConfigUtil() {
        return configUtil;
    }


    public AirDropInterface getAirDropMechanic() {
        return airDropMechanic;
    }
}
