/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client;

import com.eas.client.cache.PlatypusIndexer;
import com.eas.script.Scripts;
import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public interface ModulesProxy extends PlatypusIndexer{
    
    public ModuleStructure getModule(String aName, Scripts.Space aSpace, Consumer<ModuleStructure> onSuccess, Consumer<Exception> onFailure) throws Exception;
    
    public File getResource(String aName, Scripts.Space aSpace, Consumer<File> onSuccess, Consumer<Exception> onFailure) throws Exception;
    
    public Path getLocalPath();
    
}
