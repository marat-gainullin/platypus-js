/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.metadata;

import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.ErrorManager;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *
 * @author vy
 */
public class PrintWriterHandler extends Handler {

    private PrintWriter writer = null;

    public PrintWriterHandler(PrintWriter aWriter) {
        writer = aWriter;
    }

    public PrintWriterHandler(PrintWriter aWriter, Formatter aFormatter) {
        this(aWriter);
        setFormatter(aFormatter);
    }

    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        String msg;

        try {
            Formatter formatter = getFormatter();
            if (formatter != null) {
                msg = formatter.format(record);
            } else {
                //default
                String message = record.getMessage();
                Throwable thrown = record.getThrown();
                msg = String.format("%s%s: %s -> %s %s\n", (record.getLevel().intValue() >= Level.WARNING.intValue() ? "\u001B[31m" : ""), record.getLevel(), new Date(record.getMillis()), (message == null ? "" : message), (thrown == null ? "" : thrown.toString()));
            }
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
            return;
        }
        writer.print(msg);
    }

    @Override
    public void flush() {
        writer.flush();
    }

    @Override
    public void close() throws SecurityException {
        writer.close();
    }
}
