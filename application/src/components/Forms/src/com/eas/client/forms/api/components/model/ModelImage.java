/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.dbcontrols.image.DbImage;
import com.eas.script.ScriptFunction;

/**
 *
 * @author mg
 */
public class ModelImage extends ScalarModelComponent<DbImage> {

    protected ModelImage(DbImage aDelegate) {
        super();
        setDelegate(aDelegate);
    }

    public ModelImage() {
        super();
        setDelegate(new DbImage());
    }

    @ScriptFunction(jsDoc = "Determines if component is editable.")
    public boolean isEditable() {
        return delegate.isEditable();
    }

    @ScriptFunction
    public void setEditable(boolean aValue) {
        delegate.setEditable(aValue);
    }
    
    @ScriptFunction(jsDoc = "Determines if image is displayed with real dimensions and not scaled. If False, than image is fitted and scaled with mouse wheel.")
    public boolean isPlain()
    {
        return delegate.isPlain();
    }
    
    @ScriptFunction
    public void setPlain(boolean aValue)
    {
        delegate.setPlain(aValue);
    }
}
