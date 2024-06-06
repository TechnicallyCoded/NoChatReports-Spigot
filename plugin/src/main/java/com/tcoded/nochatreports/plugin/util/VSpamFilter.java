package com.tcoded.nochatreports.plugin.util;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class VSpamFilter implements Filter {

    private final Logger antiPluginPolice;
    private final boolean hideMessages;
    private boolean isScarySection = false;
    private boolean isTargetedHarassment = false;


    public VSpamFilter(Logger antiPluginPolice, boolean hideMessages) {
        this.antiPluginPolice = antiPluginPolice;
        this.hideMessages = hideMessages;
    }

    @Override
    public boolean isLoggable(LogRecord record) {
//        System.out.println("Checking if loggable: " + record.getMessage());

        boolean isScaryMessageBar = record.getMessage().contains("************************************************");
        boolean isHarassment = record.getMessage().contains("NoChatReports");

        if (isScaryMessageBar) {
            if (isScarySection) {
                isScarySection = false;

                if (hideMessages) return false;

                if (isTargetedHarassment) {
                    antiPluginPolice.warning("↑↑↑↑↑ Oh no! Scary message! (You can safely ignore this...) ↑↑↑↑↑");
                    antiPluginPolice.warning(" ");
                    antiPluginPolice.warning("If you want more context about this message, please read the NoChatReports");
                    antiPluginPolice.warning("description page on SpigotMC.");
                    antiPluginPolice.warning(" ");
                    antiPluginPolice.warning("Please note that ViaVersion is known to purposefully fabricate non-existent");
                    antiPluginPolice.warning("issues when a chat reporting plugin is installed. Please ignore any fake");
                    antiPluginPolice.warning("scary messages..");
                    antiPluginPolice.warning(" ");
                    antiPluginPolice.warning("Once you have verified that there are no real issues, feel free to disable");
                    antiPluginPolice.warning("this spam in NoChatReports' config.yml file.");
                    antiPluginPolice.warning("(This message will automatically disappear when ViaVersion fixes their plugin.)");
                }
            } else {
                isScarySection = true;
            }
        }

        if (isHarassment) isTargetedHarassment = true;

        // User choice is great, you know...
        if (hideMessages && isScarySection) return false;

        return true;
    }

}
