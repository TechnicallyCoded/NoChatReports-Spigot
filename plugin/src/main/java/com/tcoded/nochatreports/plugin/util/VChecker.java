package com.tcoded.nochatreports.plugin.util;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class VChecker implements Filter {

    private final Logger logger;
    private final boolean skip;
    private boolean pBar = false;
    private boolean ncr = false;


    public VChecker(Logger antiPluginPolice, boolean skip) {
        this.logger = antiPluginPolice;
        this.skip = skip;
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        boolean bar = record.getMessage().contains("****");
        boolean ncr = record.getMessage().contains("NoChatReports");

        if (bar) {
            if (pBar) {
                pBar = false;
                if (skip) return false;
                if (this.ncr) {
                    logger.warning("↑↑↑↑↑ Oh no! Scary message! (You can safely ignore this...) ↑↑↑↑↑");
                    logger.warning(" ");
                    logger.warning("If you want more context about this message, please read the NoChatReports");
                    logger.warning("description page on SpigotMC.");
                    logger.warning(" ");
                    logger.warning("Please note that ViaVersion is known to purposefully fabricate non-existent");
                    logger.warning("issues when a chat reporting plugin is installed. Please ignore any fake");
                    logger.warning("scary messages..");
                    logger.warning(" ");
                    logger.warning("Once you have verified that there are no real issues, feel free to disable");
                    logger.warning("this spam in NoChatReports' config.yml file.");
                    logger.warning("(This message will automatically disappear when ViaVersion fixes their plugin.)");
                }
            } else {
                pBar = true;
            }
        }

        if (ncr) this.ncr = true;

        // User choice is great, you know...
        if (skip && pBar) return false;

        return true;
    }

}
