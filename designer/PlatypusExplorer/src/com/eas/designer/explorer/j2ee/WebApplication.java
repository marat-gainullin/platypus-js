/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee;

import com.eas.xml.dom.XmlDomUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Simplified representation of deployment descriptor(web.xml) file.
 *
 * @author vv
 */
public class WebApplication {

    public static final String TAG_NAME = "web-app";//NOI18N
    protected static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    protected DocumentBuilder builder;
    private List<ContextParam> contextParams = new ArrayList<>();

    public WebApplication() throws ParserConfigurationException {
        factory.setNamespaceAware(true);
        builder = factory.newDocumentBuilder();
    }

    public Document toDocument() {
        Document doc = builder.newDocument();
        doc.setXmlStandalone(false);
        Element webApp = doc.createElement(TAG_NAME);
        webApp.setAttribute("xmlns", "http://java.sun.com/xml/ns/javaee"); //NOI18N
        webApp.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"); //NOI18N
        webApp.setAttribute("xsi:schemaLocation", "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_3_0.xsd"); //NOI18N
        webApp.setAttribute("version", "3.0"); //NOI18N
        doc.appendChild(webApp);
        for (ContextParam p : contextParams) {
            webApp.appendChild(p.getElement(doc));
        }

        return doc;
    }

    public List<ContextParam> getContextParams() {
        return Collections.unmodifiableList(contextParams);
    }

    public void addInitParam(ContextParam aParam) {
        contextParams.add(aParam);
    }

    public void removeInitParam(String aParamName) {
        if (aParamName != null) {
            Iterator<ContextParam> i = contextParams.iterator();
            while (i.hasNext()) {
                ContextParam param = i.next();
                if (aParamName.equals(param.name)) {
                    i.remove();
                }
            }
        }
    }

    public static class ContextParam {

        public static final String TAG_NAME = "context-param";//NOI18N
        public static final String NAME_TAG_NAME = "param-name";//NOI18N
        public static final String VALUE_TAG_NAME = "param-value";//NOI18N
        public static final String DESCRIPTION_TAG_NAME = "description";//NOI18N
        public final String name;
        public String value;

        public ContextParam(String aName, String aValue) {
            checkName(aName);
            name = aName;
            value = aValue;
            descriptionTag = null;
        }

        public ContextParam(String aName, String aValue, String aDescription) {
            checkName(aName);
            name = aName;
            value = aValue;
            descriptionTag = aDescription;
        }
        
        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getDescription() {
            return descriptionTag;
        }

        public void setDescription(String description) {
            this.descriptionTag = description;
        }
        public String descriptionTag;

        private void checkName(String aName) {
            if (aName == null) {
                throw new IllegalArgumentException("Parameter's name mustn't be null.");
            }
            if (aName.isEmpty()) {
                throw new IllegalArgumentException("Parameter's name mustn't be empty.");
            }
        }

        public Element getElement(Document aDoc) {
            Element element = aDoc.createElement(TAG_NAME);
            if (descriptionTag != null) {
                Element desc = aDoc.createElement(DESCRIPTION_TAG_NAME);
                desc.setTextContent(descriptionTag);
                element.appendChild(desc);
            }
            Element nameTag = aDoc.createElement(NAME_TAG_NAME);
            nameTag.setTextContent(name);
            element.appendChild(nameTag);
            Element valueTag = aDoc.createElement(VALUE_TAG_NAME);
            valueTag.setTextContent(value != null ? value : ""); //NOI18N
            element.appendChild(valueTag);
            return element;
        }
    }
}
