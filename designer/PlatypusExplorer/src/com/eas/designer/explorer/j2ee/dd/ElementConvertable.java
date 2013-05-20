/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Allowing conversion to XML element.
 * @author vv
 */
public interface ElementConvertable {

    /**
     * Converts to XML element.
     * @param XML document
     * @return XML element
     */
    Element getElement(Document aDoc);
}
