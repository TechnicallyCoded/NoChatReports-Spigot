package ml.tcoded.nochatreports.util;

import ml.tcoded.nochatreports.NoChatReportsSpigot;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class UpdateUtil {

    public static void checkUpdate(NoChatReportsSpigot plugin) {
        NoChatReportsSpigot.getScheduler().runTaskAsynchronously(() -> {
            ConsoleCommandSender consoleSender = plugin.getConsoleSender();
            String pluginName = plugin.getDescription().getName();
            String prefix = ChatColor.WHITE + "[" + ChatColor.GREEN + pluginName+ ChatColor.WHITE + "]" + ChatColor.GRAY;
            int resourceId = 102931;

            consoleSender.sendMessage(prefix + " Checking for updates...");

            final UpdateChecker.UpdateResult result = new UpdateChecker(plugin, resourceId).getResult();

            int prioLevel = 0;
            String prioColor = ChatColor.AQUA.toString();
            String prioLevelName = "null";

            switch (result.getType()) {
                case FAIL_SPIGOT:
                    consoleSender.sendMessage(prefix + ChatColor.GOLD + " Warning: Could not contact Spigot to check if an update is available.");
                    break;
                case UPDATE_LOW:
                    prioLevel = 1;
                    prioLevelName = "minor";
                    break;
                case UPDATE_MEDIUM:
                    prioLevel = 2;
                    prioLevelName = "feature";
                    prioColor = ChatColor.GOLD.toString();
                    break;
                case UPDATE_HIGH:
                    prioLevel = 3;
                    prioLevelName = "MAJOR";
                    prioColor = ChatColor.RED.toString();
                    break;
                case DEV_BUILD:
                    consoleSender.sendMessage(prefix + ChatColor.GOLD + " Warning: You are running an experimental/development build! Proceed with caution.");
                    break;
                case NO_UPDATE:
                    consoleSender.sendMessage(prefix + ChatColor.RESET + " You are running the latest version.");
                    break;
                default:
                    break;
            }

            if (prioLevel > 0) {
                consoleSender.sendMessage("");
                consoleSender.sendMessage(prioColor + "===============================================================================");
                consoleSender.sendMessage(prioColor + "A " + prioLevelName + " update to " + pluginName + " is available!");
                consoleSender.sendMessage(prioColor + "Download at https://www.spigotmc.org/resources/" + pluginName.toLowerCase() + "." + resourceId + "/");
                consoleSender.sendMessage(prioColor + "(current: " + result.getCurrentVer() + ", latest: " + result.getLatestVer() + ")");
                consoleSender.sendMessage(prioColor + "===============================================================================");
                consoleSender.sendMessage("");
            }

        });
    }

}
