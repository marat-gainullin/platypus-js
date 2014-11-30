/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.grid.olddesigninfos;

import com.eas.client.model.ModelElementRef;
import com.eas.client.model.ModelEntityParameterRef;
import com.eas.controls.DesignInfo;
import com.eas.store.Serial;

/**
 *
 * @author mg
 */
public class DbGridTreeDesignInfo extends DesignInfo {

    public static final int ONE_FIELD_ONE_QUERY_TREE_KIND = 1;
    public static final int FIELD_2_PARAMETER_TREE_KIND = 2;
    public static final int SCRIPT_PARAMETERS_TREE_KIND = 3;
    public static final String PARAM2GETCHILDREN = "param2GetChildren";
    public static final String PARAMETERSSETUPSCRIPT2GETCHILDREN = "parametersSetupScript2GetChildren";
    public static final String PARAMSOURCEFIELD = "paramSourceField";
    public static final String TREEKIND = "treeKind";
    public static final String UNARYLINKFIELD = "unaryLinkField";
    protected int treeKind = ONE_FIELD_ONE_QUERY_TREE_KIND;
    // ONE_FIELD_ONE_QUERY_TREE_KIND
    protected ModelElementRef unaryLinkField;
    // FIELD_2_PARAMETER_TREE_KIND
    protected ModelElementRef paramSourceField;
    protected ModelEntityParameterRef param2GetChildren;
    // SCRIPT_PARAMETERS_TREE_KIND
    protected String parametersSetupScript2GetChildren = null;

    public DbGridTreeDesignInfo() {
        super();
    }

    @Override
    public boolean isEqual(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DbGridTreeDesignInfo other = (DbGridTreeDesignInfo) obj;
        if (this.treeKind != other.treeKind) {
            return false;
        }
        if (this.unaryLinkField != other.unaryLinkField && (this.unaryLinkField == null || !this.unaryLinkField.equals(other.unaryLinkField))) {
            return false;
        }
        if (this.paramSourceField != other.paramSourceField && (this.paramSourceField == null || !this.paramSourceField.equals(other.paramSourceField))) {
            return false;
        }
        if (this.param2GetChildren != other.param2GetChildren && (this.param2GetChildren == null || !this.param2GetChildren.equals(other.param2GetChildren))) {
            return false;
        }
        if ((this.parametersSetupScript2GetChildren == null) ? (other.parametersSetupScript2GetChildren != null) : !this.parametersSetupScript2GetChildren.equals(other.parametersSetupScript2GetChildren)) {
            return false;
        }
        return true;
    }

    protected void assign(DbGridTreeDesignInfo aInfo) {
        if (aInfo != null) {
            setTreeKind(aInfo.getTreeKind());
            // ONE_FIELD_ONE_QUERY_TREE_KIND
            setUnaryLinkField(aInfo.getUnaryLinkField() != null ? aInfo.getUnaryLinkField().copy() : null);
            // FIELD_2_PARAMETER_TREE_KIND
            setParamSourceField(aInfo.getParamSourceField() != null ? aInfo.getParamSourceField().copy() : null);
            setParam2GetChildren(aInfo.getParam2GetChildren() != null ? aInfo.getParam2GetChildren().copy() : null);
            // SCRIPT_PARAMETERS_TREE_KIND
            setParametersSetupScript2GetChildren(aInfo.getParametersSetupScript2GetChildren() != null ? new String(aInfo.getParametersSetupScript2GetChildren().toCharArray()) : null);
        }
    }

    @Override
    public void assign(DesignInfo aSource) {
        if (aSource != null && aSource instanceof DbGridTreeDesignInfo) {
            assign((DbGridTreeDesignInfo) aSource);
        }
    }

    @Serial
    public int getTreeKind() {
        return treeKind;
    }

    @Serial
    public void setTreeKind(int aValue) {
        int old = treeKind;
        treeKind = aValue;
        firePropertyChange(TREEKIND, old, aValue);
    }

    @Serial
    public ModelElementRef getUnaryLinkField() {
        return unaryLinkField;
    }

    @Serial
    public void setUnaryLinkField(ModelElementRef aValue) {
        ModelElementRef old = unaryLinkField;
        unaryLinkField = aValue;
        firePropertyChange(UNARYLINKFIELD, old, aValue);
    }

    @Serial
    public ModelElementRef getParamSourceField() {
        return paramSourceField;
    }

    @Serial
    public void setParamSourceField(ModelElementRef aValue) {
        ModelElementRef old = paramSourceField;
        paramSourceField = aValue;
        firePropertyChange(PARAMSOURCEFIELD, old, aValue);
    }

    @Serial
    public ModelEntityParameterRef getParam2GetChildren() {
        return param2GetChildren;
    }

    @Serial
    public void setParam2GetChildren(ModelEntityParameterRef aValue) {
        ModelEntityParameterRef old = param2GetChildren;
        param2GetChildren = aValue;
        firePropertyChange(PARAM2GETCHILDREN, old, aValue);
    }

    @Serial
    public String getParametersSetupScript2GetChildren() {
        return parametersSetupScript2GetChildren;
    }

    @Serial
    public void setParametersSetupScript2GetChildren(String aValue) {
        String old = parametersSetupScript2GetChildren;
        parametersSetupScript2GetChildren = aValue;
        firePropertyChange(PARAMETERSSETUPSCRIPT2GETCHILDREN, old, aValue);
    }
}
