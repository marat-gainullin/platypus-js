/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.settings;

import com.eas.client.ConnectionSettingsVisitor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mg
 */
public class XmlDom2ConnectionSettings implements ConnectionSettingsVisitor {

    protected Document doc;

    XmlDom2ConnectionSettings(Document aDoc) {
        super();
        doc = aDoc;
    }

    public static EasSettings document2Settings(Document aDoc) throws Exception {
        if (aDoc != null) {
            NodeList nl = aDoc.getChildNodes();
            if (nl.getLength() == 1) {
                Node settingsNode = nl.item(0);
                String nodeName = settingsNode.getNodeName();
                EasSettings settings = null;
                if (nodeName.equalsIgnoreCase(ConnectionSettings2XmlDom.DB_SETTINGS_TAG_NAME)) {
                    settings = new DbConnectionSettings();
                } else if (nodeName.equalsIgnoreCase(ConnectionSettings2XmlDom.PLATYPUS_SERVER_SETTINGS_TAG_NAME)) {
                    settings = new PlatypusConnectionSettings();
                } else {
                    assert false;
                }
                XmlDom2ConnectionSettings dom2Settings = new XmlDom2ConnectionSettings(aDoc);
                settings.accept(dom2Settings);
                return settings;
            }
        }
        return null;
    }

    @Override
    public void visit(DbConnectionSettings aSettings) {
        NodeList nl = doc.getChildNodes();
        if (nl.getLength() == 1) {
            Element settingsNode = (Element) nl.item(0);
            aSettings.setName(settingsNode.getAttribute(ConnectionSettings2XmlDom.NAME_ATTR_NAME));
            aSettings.setUrl(settingsNode.getAttribute(ConnectionSettings2XmlDom.URL_ATTR_NAME));
            aSettings.setUser(settingsNode.getAttribute(ConnectionSettings2XmlDom.USER_ATTR_NAME));
            String schema = settingsNode.getAttribute(ConnectionSettings2XmlDom.SCHEMA_ATTR_NAME);
            if (schema != null && !schema.isEmpty()) {
                aSettings.setSchema(schema);
            }
            aSettings.setPassword(settingsNode.getAttribute(ConnectionSettings2XmlDom.PASSWORD_ATTR_NAME));
            aSettings.setInitSchema(Boolean.valueOf(settingsNode.getAttribute(ConnectionSettings2XmlDom.INIT_SCHEMA_ATTR_NAME)));
            aSettings.setDeferCache(Boolean.valueOf(settingsNode.getAttribute(ConnectionSettings2XmlDom.DEFER_CACHE_ATTR_NAME)));

        }
    }

    @Override
    public void visit(PlatypusConnectionSettings aSettings) {
        NodeList nl = doc.getChildNodes();
        if (nl.getLength() == 1) {
            Element settingsNode = (Element) nl.item(0);
            aSettings.setName(settingsNode.getAttribute(ConnectionSettings2XmlDom.NAME_ATTR_NAME));
            aSettings.setUrl(settingsNode.getAttribute(ConnectionSettings2XmlDom.URL_ATTR_NAME));
            aSettings.setUser(settingsNode.getAttribute(ConnectionSettings2XmlDom.USER_ATTR_NAME));
            aSettings.setPassword(settingsNode.getAttribute(ConnectionSettings2XmlDom.PASSWORD_ATTR_NAME));
        }
    }
}
