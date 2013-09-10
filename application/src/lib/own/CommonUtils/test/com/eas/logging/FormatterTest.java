/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.logging;

import com.eas.util.logging.PlatypusFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author mg
 */
public class FormatterTest {

    public static final String TEST_LOGGER_NAME = "testLogger";

    @Before
    public void setup() {
        PlatypusFormatter formatter = new PlatypusFormatter(TEST_LOGGER_NAME, null);
        Handler h = new ConsoleHandler();
        h.setFormatter(formatter);
        Logger l = Logger.getLogger(TEST_LOGGER_NAME);
        l.setUseParentHandlers(false);
        l.addHandler(h);
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
            Logger.getLogger(TEST_LOGGER_NAME).log(Level.SEVERE, "test message 2", ex);
        }
    }
}
