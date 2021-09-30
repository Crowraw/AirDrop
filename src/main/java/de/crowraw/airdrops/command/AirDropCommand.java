package de.crowraw.airdrops.command;/*
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AirDropCommand implements CommandExecutor {

    private final AirDrops plugin;


    public AirDropCommand(AirDrops plugin) {
        this.plugin = plugin;
        this.plugin.getCommand("airdrop").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (strings.length != 1) {
                syntax(player);
                return true;
            }
            if (player.hasPermission("airdrop.setup")) {


                switch (strings[0].toLowerCase()) {
                    case "addlocation":

                        plugin.getConfigUtil().
                                getLocationFromId(plugin.getConfigUtil().getYamlConfiguration().
                                        getConfigurationSection("location").getKeys(false).size() + 1, player.getLocation());
                        success(player);
                        break;
                    case "additem":
                        plugin.getConfigUtil().getYamlConfiguration().set("items." + (plugin.getConfigUtil().getYamlConfiguration().
                                getConfigurationSection("items").getKeys(false).size()), player.getItemInHand());
                        success(player);
                        break;
                    case "start":
                        plugin.getAirDropMechanic().setTimeElapsed(60*9+28);
                        plugin.getAirDropMechanic().setStart(true);
                        success(player);
                        break;
                    case "antilag":
                        plugin.getAirDropMechanic().setAntiLag(!plugin.getAirDropMechanic().isAntiLag());
                        player.sendMessage("§aAntiLag is now" + (plugin.getAirDropMechanic().isAntiLag() ? "§2activ" : "§4not active"));
                        break;
                    default:
                        syntax(player);
                }


            }


        }


        return false;
    }

    private void syntax(Player player) {
        player.sendMessage("§cSorry false syntax!");
        player.sendMessage("§c- /airdrop addLocation -> Location you are standing. Make sure there is no roof!");
        player.sendMessage("§c- /airdrop addItem -> Item in hand");
        player.sendMessage("§c- /airdrop start -> Starts");
        player.sendMessage("§c- /airdrop antilag -> No fireworks");
    }

    private void success(Player player) {
        player.sendMessage("§2Sucess");
        plugin.getConfigUtil().saveConfig();
    }
}
