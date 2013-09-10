/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author mg
 */
public class PlatypusFormatter extends SimpleFormatter {

    // format string for printing the log record
    private static final String format = "%1$td:%1$tm:%1$tY %1$tH:%1$tM:%1$tS\t%2$s\t%3$s\t%4$s%n";
    private final Date dat = new Date();
    private String loggerName;
    private Formatter delegate;

    public PlatypusFormatter(String aLoggerName, Formatter aDelegate) {
        super();
        assert aLoggerName != null && !aLoggerName.isEmpty() : "Logger name must be specified";
        loggerName = aLoggerName;
        delegate = aDelegate;
    }

    /**
     * @inherit
     */
    @Override
    public synchronized String format(LogRecord record) {
        if (loggerName.equalsIgnoreCase(record.getLoggerName())) {
            dat.setTime(record.getMillis());
            String message = formatMessage(record);
            String throwable = "";
            if (record.getThrown() != null) {
                StringWriter sw = new StringWriter();
                try (PrintWriter pw = new PrintWriter(sw)) {
                    record.getThrown().printStackTrace(pw);
                }
                throwable = sw.toString();
            }
            return String.format(format,
                    dat,
                    record.getLevel().getLocalizedName(),
                    message,
                    throwable);
        } else if (delegate != null) {
            return delegate.format(record);
        } else {
            return super.format(record);
        }
    }
}
