/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.tomcat;

import org.w3c.dom.Element;

/**
 * Supports creation of Resource from XML element.
 * @author vv
 */
public class ResourceFactory {
    /**
     * Creates an instance of Resource.
     * @param element XML element
     * @return an instance of Resource or null if Resource type is unknown.
     */
    public static Resource getRealm(Element element) {
        String type = element.getAttribute(Resource.TYPE_ATTR_NAME);
        Resource res = null;
        switch (type) {
            case DataSourceResource.DATA_SOURCE_RESOURCE_TYPE_NAME:
                res = new DataSourceResource();
                res.load(element);
                break;
            default:
                res = new Resource();
                res.load(element);
                break;
        }
        return res;
    }
}
