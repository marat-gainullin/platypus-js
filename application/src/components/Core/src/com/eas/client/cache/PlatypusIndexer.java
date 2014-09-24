/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.AppElementFiles;

/**
 *
 * @author mg
 */
public interface PlatypusIndexer {

    public AppElementFiles nameToFiles(String aName) throws Exception;

}
