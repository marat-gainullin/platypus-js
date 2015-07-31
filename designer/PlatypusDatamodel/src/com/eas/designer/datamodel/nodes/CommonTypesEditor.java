/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import com.eas.client.model.Model;
import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.sqldrivers.resolvers.TypesResolver;
import com.eas.script.Scripts;
import java.beans.PropertyEditorSupport;
import org.openide.util.Exceptions;

/**
 *
 * @author vv
 */
public class CommonTypesEditor extends PropertyEditorSupport {

    private final String[] values;

    private CommonTypesEditor(String[] aValues) {
        super();
        values = aValues;
    }

    public static synchronized CommonTypesEditor getNewInstanceFor(Model model) {
        try {
            if (model instanceof DbSchemeModel) {
                TypesResolver resolver = ((DbSchemeModel) model).getBasesProxy().getMetadataCache(((DbSchemeModel) model).getDatasourceName()).getDatasourceSqlDriver().getTypesResolver();
                return new CommonTypesEditor(resolver.getSupportedTypes().toArray(new String[]{}));
            } else {
                return new CommonTypesEditor(new String[]{
                    Scripts.STRING_TYPE_NAME,
                    Scripts.NUMBER_TYPE_NAME,
                    Scripts.BOOLEAN_TYPE_NAME,
                    Scripts.DATE_TYPE_NAME,
                    Scripts.GEOMETRY_TYPE_NAME, ""});
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            return new CommonTypesEditor(new String[]{ex.toString()});
        }
    }

    @Override
    public String getAsText() {
        return (String) (getValue() != null ? getValue() : "");
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        super.setValue(text == null || text.isEmpty() ? null : text);
    }

    @Override
    public String[] getTags() {
        return values;
    }

}
