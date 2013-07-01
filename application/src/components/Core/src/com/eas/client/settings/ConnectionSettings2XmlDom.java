/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.settings;

import com.eas.client.ClientConstants;
import com.eas.client.ConnectionSettingsVisitor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mg
 */
public class ConnectionSettings2XmlDom implements ConnectionSettingsVisitor {

    public static final String DB_SETTINGS_TAG_NAME = "dbSettings";
    public static final String NAME_ATTR_NAME = "name";
    protected static final String URL_ATTR_NAME = "url";
    protected static final String SCHEMA_ATTR_NAME = "schema";
    protected static final String USER_ATTR_NAME = "user";
    protected static final String PASSWORD_ATTR_NAME = "password";
    protected static final String INIT_SCHEMA_ATTR_NAME = "initschema";
    protected static final String DEFER_CACHE_ATTR_NAME = "deferCache";
    protected static final String PLATYPUS_SERVER_SETTINGS_TAG_NAME = "platypusServerSettings";
    protected static final String HTTP_SERVER_SETTINGS_TAG_NAME = "httpServerSettings";
    protected static final String EE_SERVER_SETTINGS_TAG_NAME = "eeServerSettings";
    // setup documents framework
    protected static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    // declaring documents framework
    protected DocumentBuilder builder = null;
    protected Document doc = null;

    ConnectionSettings2XmlDom() {
        super();
        try {
            // setup documents framework
            builder = factory.newDocumentBuilder();
            doc = builder.newDocument();
            doc.setXmlStandalone(true);
        } catch (ParserConfigurationException ex) {
            builder = null;
            Logger.getLogger(ConnectionSettings2XmlDom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Document settingsToDocument(EasSettings aSettigns) {
        ConnectionSettings2XmlDom v = new ConnectionSettings2XmlDom();
        aSettigns.accept(v);
        return v.getDoc();
    }

    public Document getDoc() {
        return doc;
    }

    @Override
    public void visit(DbConnectionSettings aSettings) {
        Element settingsNode = doc.createElement(DB_SETTINGS_TAG_NAME);
        settingsNode.setAttribute(NAME_ATTR_NAME, aSettings.getName());
        settingsNode.setAttribute(URL_ATTR_NAME, aSettings.getUrl());
        String schema = (String) aSettings.getInfo().get(ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME);
        if (schema != null && !schema.isEmpty()) {
            settingsNode.setAttribute(SCHEMA_ATTR_NAME, schema);
        }
        settingsNode.setAttribute(USER_ATTR_NAME, (String) aSettings.getInfo().get(ClientConstants.DB_CONNECTION_USER_PROP_NAME));
        settingsNode.setAttribute(PASSWORD_ATTR_NAME, (String) aSettings.getInfo().get(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME));

        doc.appendChild(settingsNode);
    }

    @Override
    public void visit(PlatypusConnectionSettings aSettings) {
        Element settingsNode = doc.createElement(PLATYPUS_SERVER_SETTINGS_TAG_NAME);

        settingsNode.setAttribute(NAME_ATTR_NAME, aSettings.getName());
        settingsNode.setAttribute(URL_ATTR_NAME, aSettings.getUrl());
        settingsNode.setAttribute(USER_ATTR_NAME, (String) aSettings.getInfo().get(ClientConstants.DB_CONNECTION_USER_PROP_NAME));
        //settingsNode.setAttribute(PASSWORD_ATTR_NAME, (String)aSettings.getInfo().get(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME));

        doc.appendChild(settingsNode);
    }
}
