package de.crowraw.airdrops;

import de.crowraw.api.data.ConfigUtil;
import de.crowraw.airdrops.command.AirDropCommand;
import de.crowraw.airdrops.mechanic.AirDropMechanic;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class AirDrops extends JavaPlugin {
    private final ConfigUtil configUtil = new ConfigUtil("plugins//AirDrop//config.yml");
    private AirDropMechanic airDropMechanic;

    private final List<Location> locations = new ArrayList<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        configUtil.saveConfig();

        airDropMechanic = new AirDropMechanic(this);
        new AirDropCommand(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ConfigUtil getConfigUtil() {
        return configUtil;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public AirDropMechanic getAirDropMechanic() {
        return airDropMechanic;
    }
}
