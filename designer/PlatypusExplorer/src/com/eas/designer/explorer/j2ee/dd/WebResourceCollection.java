/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Defines the area of the Web application to which this security constraint is applied.
 * @author vv
 */
public class WebResourceCollection implements ElementConvertable {

    public static final String TAG_NAME = "web-resource-collection"; //NOI18N
    public static final String NAME_TAG_NAME = "web-resource-name"; //NOI18N
    public static final String DESCRIPTION_TAG_NAME = "description"; //NOI18N
    public static final String URL_PATTERN_TAG_NAME = "url-pattern"; //NOI18N
    public static final String HTTP_METHOD_TAG_NAME = "http-method"; //NOI18N
    private String name;
    private String description;
    private String urlPattern;
    private String httpMethod;

    public WebResourceCollection(String aName) {
        name = aName;
    }

    public String getName() {
        return name;
    }

    public void setName(String aName) {
        name = aName;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String aValue) {
        description = aValue;
    }

    public String getUrlPattern() {
        return urlPattern;
    }

    public void setUrlPattern(String aValue) {
        urlPattern = aValue;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String aValue) {
        httpMethod = aValue;
    }
    
    @Override
    public Element getElement(Document aDoc) {
        Element element = aDoc.createElement(TAG_NAME);
        Element nameElement = aDoc.createElement(NAME_TAG_NAME);
        nameElement.setTextContent(name);   
        if (description != null) {
            Element descElement = aDoc.createElement(DESCRIPTION_TAG_NAME);
            descElement.setTextContent(description);
            element.appendChild(descElement);
        }
        if (urlPattern != null) {
            Element urlPatternElement = aDoc.createElement(URL_PATTERN_TAG_NAME);
            urlPatternElement.setTextContent(urlPattern);
            element.appendChild(urlPatternElement);
        }
        if (httpMethod != null) {
            Element httpMethodElement = aDoc.createElement(HTTP_METHOD_TAG_NAME);
            httpMethodElement.setTextContent(httpMethod);
            element.appendChild(httpMethodElement);
        }
        return element;
    }
    
}
