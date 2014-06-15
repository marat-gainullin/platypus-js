/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.eas.client.model.ModelElementRef;
import com.eas.client.model.application.ApplicationEntity;
import com.eas.gui.CascadedStyle;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import jdk.nashorn.api.scripting.JSObject;

/**
 * Interface for scalar data aware control. Scalar control views and edits a
 * value from particular row and oarticular field of a rowset. Example is
 * DbCombo It may be used as table cell renderer or editor.
 *
 * @see InitializingMethod
 * @author mg
 */
public interface ScalarDbControl extends DbControl, TableCellRenderer, TableCellEditor{

    public void configure() throws Exception;

    public void cleanup() throws Exception;

    /**
     * Returns datamodel's entity, the data editing occurs in.
     *
     * @return Entity instance, containing editing Rowset.
     */
    public ApplicationEntity<?, ?, ?> getBaseEntity();

    /**
     * Returns column index of editing field in data rowset (bounded column
     * index).
     *
     * @return Bounded column index
     */
    public int getColIndex();

    /**
     * Fires editing completed event to all DbControlEditingListener instances,
     * registered
     *
     * @param aValue
     * @see DbControlEditingListener
     */
    public void setEditingValue(Object aValue);

    /**
     * Returns whether editing value is modified. Unfortunately, not all the
     * controls have straight criterion of editing completed, like an action.
     * For example, multiline text editors have no such criterion. So we have to
     * consider, that value is not modified until data might be saved or
     * transmitted in a some way. In this case we explicitly check whether data
     * is changed.
     *
     * @return
     */
    public boolean isFieldContentModified();

    public Object achiveDisplayValue(Object aValue) throws Exception;

    /**
     * Sets whether this control have to be borderless. Sometimes, it's useful
     * to have a control without any borders. It may table cell editor or
     * somthing else.
     *
     * @param aBorderless
     */
    public void setBorderless(boolean aBorderless);

    /**
     * Sets whether this control is standalone. Standalone means that it is
     * ordinary control on a form, and Non-standalone means, that this instance
     * is used within a table as renderer or editor. The default value is true.
     *
     * @param aStandalone
     */
    public void setStandalone(boolean aStandalone);

    public void extraCellControls(JSObject aSelectFunction, boolean nullable) throws Exception;

    public void injectPublished(Object aPublished);
    
    public boolean haveNullerAction();

    /**
     * Applies a style to the control. Unfortunately, Swing's controls don't
     * support styles, and so, we have to manually apply a style to arbitrary
     * control's properties, such as background color, etc.
     *
     * @param aStyle
     */
    public void applyStyle(CascadedStyle aStyle);

    public boolean setValue2Rowset(Object aValue) throws Exception;

    public Object getValueFromRowset() throws Exception;

    public void fireCellEditingCompleted();

    public ModelElementRef getDatamodelElement();

    public void setDatamodelElement(ModelElementRef aValue) throws Exception;

    public JSObject getOnSelect();

    public void setOnSelect(JSObject aValue) throws Exception;

    public JSObject getOnRender();

    public void setOnRender(JSObject aValue);

    public boolean isSelectOnly();

    public void setSelectOnly(boolean aValue);
}
