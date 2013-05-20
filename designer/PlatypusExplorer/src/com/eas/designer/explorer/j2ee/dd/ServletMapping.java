/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Configures element defines a mapping between a servlet and a URL pattern.
 * @author vv
 */
public class ServletMapping implements ElementConvertable {
    public static final String TAG_NAME = "servlet-mapping";//NOI18N
    public static final String SERVLET_NAME_TAG_NAME = "servlet-name";//NOI18N
    public static final String URL_PATTERN_TAG_NAME = "url-pattern";//NOI18N
    private String servletName;
    private String urlPattern;

    public ServletMapping(String aServletName, String anUrlPattern) {
        servletName = aServletName;
        urlPattern = anUrlPattern;
    }

    public String getServletName() {
        return servletName;
    }

    public void setServletName(String aServletName) {
        servletName = aServletName;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String anUrlPattern) {
        urlPattern = anUrlPattern;
    }
    
    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        Element classNameElement = aDoc.createElement(SERVLET_NAME_TAG_NAME);
        classNameElement.setTextContent(servletName);
        element.appendChild(classNameElement);
        Element urlPatternElement = aDoc.createElement(URL_PATTERN_TAG_NAME);
        urlPatternElement.setTextContent(urlPattern);
        element.appendChild(urlPatternElement);
        return element;
    }
}
