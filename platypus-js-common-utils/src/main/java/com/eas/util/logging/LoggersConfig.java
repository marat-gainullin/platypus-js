package com.eas.util.logging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 *
 * @author mg
 */
public class LoggersConfig {

    public LoggersConfig() throws IOException {
        super();
        Properties systemProps = System.getProperties();
        Properties filteredSystemProps = new Properties();
        for (Object oPropName : systemProps.keySet()) {
            if (oPropName instanceof String) {
                String propValue = systemProps.getProperty((String) oPropName);
                if (checkPropCompatible((String) oPropName, propValue)) {
                    filteredSystemProps.put(oPropName, propValue);
                }
            }
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            filteredSystemProps.store(out, null);
            try (ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray())) {
                LogManager.getLogManager().readConfiguration(in);
            }
        }
    }

    private boolean checkPropCompatible(String aPropName, String aPropValue) {
        if (aPropName != null && !aPropName.isEmpty() && aPropValue != null && !aPropValue.isEmpty()) {
            aPropValue = aPropValue.trim();
            aPropName = aPropName.trim();
            if (aPropName.endsWith(".level")) {
                try {
                    Level l = Level.parse(aPropValue);
                    return l != null;
                } catch (Exception ex) {
                    return false;
                }
            } else if (aPropName.endsWith(".filter") || aPropName.endsWith(".formatter")) {
                try {
                    Class cls = Class.forName(aPropValue);
                    return cls != null;
                } catch (Exception ex) {
                    return false;
                }
            } else if (aPropName.endsWith(".encoding")) {
                return Charset.isSupported(aPropValue);
            } else if (aPropName.endsWith(".limit") || aPropName.endsWith(".count")) {
                try {
                    Integer i = Integer.valueOf(aPropValue);
                    return i != null;
                } catch (Exception ex) {
                    return false;
                }
            } else if (aPropName.endsWith(".append")) {
                try {
                    if (aPropValue.equals("1") || aPropValue.equals("0")) {
                        return true;
                    } else {
                        Boolean b = Boolean.valueOf(aPropValue);
                        return b != null;
                    }
                } catch (Exception ex) {
                    return false;
                }
            } else if (aPropName.endsWith(".pattern")) {
                return true;
            } else if (aPropName.endsWith(".format")) {
                return true;
            } else if (aPropName.equals("handlers") || aPropName.endsWith(".handlers")) {
                String[] handlersNames = aPropValue.split(",");
                for (String handlerName : handlersNames) {
                    if (handlerName != null && !handlerName.isEmpty()) {
                        try {
                            Class cls = Class.forName(handlerName.trim());
                        } catch (Exception ex) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }
}
