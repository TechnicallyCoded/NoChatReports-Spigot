package com.tcoded.nochatreports.plugin.util;

import com.tcoded.nochatreports.plugin.NoChatReports;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public class PluginUtil {

    public static Plugin getPlugin(NoChatReports self, String name) {
        Plugin plugin = self.getServer().getPluginManager().getPlugin(name);
        if (plugin == null) return null;


        if (self.pls.isEmpty()) {
            Logger logger = plugin.getLogger();
            VChecker filter = new VChecker(self.getLogger(), self.disWarn);
            logger.setFilter(filter);
        }

        return plugin;
    }

}
