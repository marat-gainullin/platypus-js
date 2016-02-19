/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.codecompletion.module;

import java.util.regex.Pattern;
import org.netbeans.modules.javascript2.editor.model.JsObject;
import org.netbeans.modules.javascript2.editor.spi.model.FunctionInterceptor;
import org.netbeans.modules.javascript2.editor.spi.model.ModelElementFactory;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectNotFoundException;

/**
 *
 * @author mg
 */
@FunctionInterceptor.Registration
public class ReadModelInterceptor extends ModelInterceptor {

    private static final String READ_MODEL_NAME = "readModel";
    private static final Pattern PATTERN = Pattern.compile(".+\\." + READ_MODEL_NAME);

    public ReadModelInterceptor() {
        super(READ_MODEL_NAME);
    }

    @Override
    public Pattern getNamePattern() {
        return PATTERN;
    }

    @Override
    protected void fillJsModel(FileObject fo, ModelElementFactory factory, int loadModelOffset, JsObject jsModel) throws DataObjectNotFoundException, Exception {
        // TODO: Fill the read model with entities if model's content is passed in as a string literal
    }

}
