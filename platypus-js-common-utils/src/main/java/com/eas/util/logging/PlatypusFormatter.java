package com.eas.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 * @author mg
 */
public class PlatypusFormatter extends Formatter {

    // format string for printing the log record
    private static final String FORMAT = "%1$td:%1$tm:%1$tY %1$tH:%1$tM:%1$tS\t%2$s\t%3$s\t%4$s%n";

    public PlatypusFormatter() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String format(LogRecord record) {
        Date dat = new Date(record.getMillis());
        String message = formatMessage(record);
        String throwable = "";
        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter();
            try (PrintWriter pw = new PrintWriter(sw)) {
                record.getThrown().printStackTrace(pw);
            }
            throwable = sw.toString();
        }
        return String.format(FORMAT,
                dat,
                record.getLevel().getLocalizedName(),
                message,
                throwable);
    }
}
