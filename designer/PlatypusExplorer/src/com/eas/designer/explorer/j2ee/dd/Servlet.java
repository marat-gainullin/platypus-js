/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Servlet's configuration.
 * @author vv
 */
public class Servlet implements ElementConvertable {
    
    public static final String TAG_NAME = "servlet";//NOI18N
    public static final String SERVLET_NAME_TAG_NAME = "servlet-name";//NOI18N
    public static final String SERVLET_CLASS_TAG_NAME = "servlet-class";//NOI18N
    
    private String servletName;
    private String servletClass;
    private MultipartConfig multipartConfig;

    public Servlet(String aName, String aClassName) {
        servletName = aName;
        servletClass = aClassName;
    }
    
    public String getName() {
        return servletName;
    }

    public void setName(String name) {
        this.servletName = name;
    }

    public String getClassName() {
        return servletClass;
    }

    public void setClassName(String className) {
        this.servletClass = className;
    }
    
    public MultipartConfig getMultipartConfig() {
        return multipartConfig;
    }

    public void setMultipartConfig(MultipartConfig multipartConfig) {
        this.multipartConfig = multipartConfig;
    }
    
    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        Element servletNameElement = aDoc.createElement(SERVLET_NAME_TAG_NAME);
        servletNameElement.setTextContent(servletName);
        element.appendChild(servletNameElement);
        Element servletClassElement = aDoc.createElement(SERVLET_CLASS_TAG_NAME);
        servletClassElement.setTextContent(servletClass);
        element.appendChild(servletClassElement);
        if (multipartConfig != null) {
            element.appendChild(multipartConfig.getElement(aDoc));
        }
        return element;
    }
    
}
