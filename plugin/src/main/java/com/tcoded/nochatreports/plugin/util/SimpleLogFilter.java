package com.tcoded.nochatreports.plugin.util;

import java.util.function.Predicate;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class SimpleLogFilter implements Filter {

    private final Logger logger;
    private final Predicate<LogRecord> predicate;


    public SimpleLogFilter(Logger logger, Predicate<LogRecord> predicate) {
        this.logger = logger;
        this.predicate = predicate;

        logger.setFilter(this);
    }

    @Override
    public boolean isLoggable(LogRecord record) {
        return predicate.test(record);
    }

}
