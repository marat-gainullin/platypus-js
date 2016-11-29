/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.logging;

import com.eas.util.logging.PlatypusFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class FormatterTest {

    public static final String TEST_LOGGER_NAME = "testLogger";
    private HandlerMock handler;
    private Logger logger;

    private static class HandlerMock extends ConsoleHandler {

        private final Formatter formatter = new PlatypusFormatter();
        private String lastPublished;
        
        @Override
        public void publish(LogRecord record) {
            lastPublished = formatter.format(record);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }
    }
    
    @Before
    public void setup() {
        handler = new HandlerMock();
        logger = Logger.getLogger(TEST_LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
    }

    @Test
    public void messageTest() {
        Logger.getLogger(TEST_LOGGER_NAME).log(Level.WARNING, "test message 1");
    }

    @Test
    public void exceptionTest() {
        try {
            throw new IllegalStateException("test exception");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
            assertTrue(handler.lastPublished.substring(handler.lastPublished.indexOf("\t")).startsWith("\tSEVERE\tnull\tjava.lang.IllegalStateException"));
        }
    }
}
