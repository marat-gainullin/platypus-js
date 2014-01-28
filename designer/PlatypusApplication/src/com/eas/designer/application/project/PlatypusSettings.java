package com.eas.designer.application.project;

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
    //public static final String DISPLAY_NAME_ATTR_NAME = "displayName";
    public static final String RUN_CONFIGURATION_TAG_NAME = "run";
    public static final String RUNNING_ELEMENT_ATTR_NAME = "appElement";
    public static final String DEFAULT_DATASOURCE_ATTR_NAME = "defaultDatasource";
    // Running configuration
    protected String runElement;
    protected String defaultDatasource;
    protected static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    protected DocumentBuilder builder;
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
        if (defaultDatasource != null && !defaultDatasource.isEmpty()) {
            rootTag.setAttribute(DEFAULT_DATASOURCE_ATTR_NAME, defaultDatasource);
        }
        doc.appendChild(rootTag);
        Element runTag = doc.createElement(RUN_CONFIGURATION_TAG_NAME);
        rootTag.appendChild(runTag);
        if (runElement != null) {
            runTag.setAttribute(RUNNING_ELEMENT_ATTR_NAME, runElement);
        }
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
            String defDatasource = projectTag.getAttribute(DEFAULT_DATASOURCE_ATTR_NAME);
            if (defDatasource != null && !defDatasource.isEmpty()) {
                platypusSettings.defaultDatasource = defDatasource;
            }
            Element runTag = getElementByName(projectTag, RUN_CONFIGURATION_TAG_NAME);
            if (runTag != null) {
                String sRunningElement = runTag.getAttribute(RUNNING_ELEMENT_ATTR_NAME);
                if (sRunningElement != null && !sRunningElement.isEmpty()) {
                    platypusSettings.runElement = sRunningElement;
                }
            }
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

    public String getDefaultDatasource() {
        return defaultDatasource;
    }

    public void setDefaultDatasource(String aValue) {
        if (defaultDatasource == null ? aValue != null : !defaultDatasource.equals(aValue)) {
            String oldValue = defaultDatasource;
            defaultDatasource = aValue;
            changeSupport.firePropertyChange("defaultDatasource", oldValue, defaultDatasource);
        }
    }

}
