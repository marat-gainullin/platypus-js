package com.eas.deploy.project;

import com.eas.client.settings.ConnectionSettings2XmlDom;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.EasSettings;
import com.eas.client.settings.XmlDom2ConnectionSettings;
import com.eas.server.PlatypusServer;
import com.eas.util.StringUtils;
import java.beans.PropertyChangeSupport;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

/**
 *
 * @author mg
 */
public class PlatypusSettings {

    public static final String CONTEXT_TAG_NAME = "context";
    public static final String DISPLAY_NAME_ATTR_NAME = "displayName";
    public static final String RUN_CONFIGURATION_TAG_NAME = "run";
    public static final String RUNNING_ELEMENT_ATTR_NAME = "appElement";
    // Running configuration
    protected String runElement;
    protected static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    protected DocumentBuilder builder;
    protected DbConnectionSettings dbSettings;
    protected PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public PlatypusSettings() throws Exception {
        super();
        builder = factory.newDocumentBuilder();
    }

    public PropertyChangeSupport getChangeSupport() {
        return changeSupport;
    }

    public Document toDocument() {
        Document doc = builder.newDocument();
        doc.setXmlStandalone(true);
        Element rootTag = doc.createElement(CONTEXT_TAG_NAME);
        doc.appendChild(rootTag);
        Element runTag = doc.createElement(RUN_CONFIGURATION_TAG_NAME);
        rootTag.appendChild(runTag);
        if (runElement != null) {
            runTag.setAttribute(RUNNING_ELEMENT_ATTR_NAME, runElement);
        }
        Document dbDoc = ConnectionSettings2XmlDom.settingsToDocument(dbSettings);
        Element dbTag = dbDoc.getDocumentElement();
        doc.adoptNode(dbTag);
        rootTag.appendChild(dbTag);
        return doc;
    }

    private static Element getElementByName(Element aParent, String aName) {
        NodeList nl = aParent.getElementsByTagName(aName);
        if (nl != null && nl.getLength() == 1 && nl.item(0) instanceof Element) {
            return (Element) nl.item(0);
        } else {
            return null;
        }
    }

    public static PlatypusSettings valueOf(Document aDoc) throws Exception {
        PlatypusSettings platypusSettings = new PlatypusSettings();
        NodeList projectNl = aDoc.getElementsByTagName(CONTEXT_TAG_NAME);
        if (projectNl != null && projectNl.getLength() == 1 && projectNl.item(0) instanceof Element) {
            Element projectTag = (Element) projectNl.item(0);
            Element runTag = getElementByName(projectTag, RUN_CONFIGURATION_TAG_NAME);
            if (runTag != null) {
                String sRunningElement = runTag.getAttribute(RUNNING_ELEMENT_ATTR_NAME);
                if (sRunningElement != null && !sRunningElement.isEmpty()) {
                    platypusSettings.runElement = sRunningElement;
                }
            }
            Node settingsTag = getElementByName(projectTag, ConnectionSettings2XmlDom.DB_SETTINGS_TAG_NAME);
            Document dbDoc = factory.newDocumentBuilder().newDocument();
            dbDoc.adoptNode(settingsTag);
            dbDoc.appendChild(settingsTag);
            EasSettings settings = XmlDom2ConnectionSettings.document2Settings(dbDoc); // deserialize connection settings. throw exceptions of non-database connection settings.
            if (!(settings instanceof DbConnectionSettings)) {
                throw new Exception("Platypus designer projects must contain information about two-tier connection to database");
            }
            DbConnectionSettings dbSettings = (DbConnectionSettings) settings;
            platypusSettings.dbSettings = dbSettings;
            return platypusSettings;
        }
        return null;
    }

    /**
     * Gets default application element to run.
     *
     * @return application element name
     */
    public String getRunElement() {
        return runElement;
    }

    /**
     * Sets default application element to run.
     *
     * @param aValue application element name
     */
    public void setRunElement(String aValue) {
        if (runElement == null ? aValue != null : !runElement.equals(aValue)) {
            String oldValue = runElement;
            runElement = aValue;
            changeSupport.firePropertyChange("runningElement", oldValue, runElement);
        }
    }
        
    /**
     * Gets project's database settings.
     *
     * @return Db settings
     */
    public DbConnectionSettings getDbSettings() {
        return dbSettings;
    }

    /**
     * Sets project's database settings.
     *
     * @param aValue Db settings
     */
    public void setDbSettings(DbConnectionSettings aValue) {
        if (dbSettings != aValue) {
            DbConnectionSettings oldValue = dbSettings;
            dbSettings = aValue;
            changeSupport.firePropertyChange("dbSettings", oldValue, dbSettings);
        }
    }
}
