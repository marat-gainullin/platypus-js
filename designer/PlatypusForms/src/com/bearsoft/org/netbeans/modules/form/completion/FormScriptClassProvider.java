/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.bearsoft.org.netbeans.modules.form.FormUtils;
import com.eas.designer.application.module.completion.ScriptClassProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author vv
 */
@ServiceProvider(service=ScriptClassProvider.class)
public class FormScriptClassProvider implements ScriptClassProvider {

    @Override
    public Class getClassByName(String name) {
        return FormUtils.getPlatypusApiClassByName(name);
    }   
}
