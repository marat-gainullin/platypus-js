/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import java.io.File;

/**
 *
 * @author mg
 */
public interface PlatypusIndexer {

    public File nameToFile(String aName) throws Exception;

    public String getDefaultModuleName(File aFile);
}
