/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.j2ee.dd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author vv
 */
public class SecurityConstraint implements ElementConvertable {

    public static final String TAG_NAME = "security-constraint"; //NOI18N
    private List<WebResourceCollection> webResourceCollections = new ArrayList<>();
    private AuthConstraint authConstraint;
    
    public void addWebResourceCollection(WebResourceCollection aWebResourceCollection) {
        webResourceCollections.add(aWebResourceCollection);
    }
    
    public List<WebResourceCollection> getWebResourceCollections() {
        return Collections.unmodifiableList(webResourceCollections);
    }
    
    public void removeWebResourceCollection(String aWebResourceCollectionName) {
        if (aWebResourceCollectionName != null) {
            Iterator<WebResourceCollection> i = webResourceCollections.iterator();
            while (i.hasNext()) {
                WebResourceCollection l = i.next();
                if (aWebResourceCollectionName.equals(l.getName())) {
                    i.remove();
                }
            }
        }
    }
    
    public AuthConstraint getAuthConstraint() {
        return authConstraint;
    }

    public void setAuthConstraint(AuthConstraint anAuthConstraint) {
        authConstraint = anAuthConstraint;
    }
    
    @Override
    public Element getElement(Document aDoc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
