/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.indexer;

import org.openide.filesystems.FileObject;

/**
 * Container for data of an application elements 
 * @author vv
 */
public class AppElementInfo {
    
    public final String appElementId;
    public final FileObject primaryFileObject;
    
    public AppElementInfo(String anAppElementId, FileObject aPrimaryFileObject) {
        appElementId = anAppElementId;
        primaryFileObject = aPrimaryFileObject;
    }
}
