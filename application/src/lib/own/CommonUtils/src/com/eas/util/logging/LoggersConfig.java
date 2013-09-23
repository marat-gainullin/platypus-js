/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util.logging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.LogManager;

/**
 *
 * @author mg
 */
public class LoggersConfig {

    public LoggersConfig() throws IOException {
        super();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            System.getProperties().store(out, null);
            try (ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray())) {
                LogManager.getLogManager().readConfiguration(in);
            }
        }
    }
}
