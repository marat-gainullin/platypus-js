/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols;

import com.eas.client.model.application.ApplicationModel;

/**
 * Base interace for data aware controls.
 * Db controls must listen to property changed event from thier's design info.
 * Because of that, DbControl is PropertyChangeListener too.
 * Data aware controls may be of types:
 *  - scalar (field) controls.
 *  - rowset controls.
 *  - multiple rowsets controls.
 * Scalar control views and edits a value from a particular row and a particular field of a rowset. Example of such control is is DbCombo
 * Rowset controls views and edits one rowset. Example of such control is DbGrid.
 * Multiple rowsets controls views and edits multiple rowsets at the same time. Example of such control is DbMap.
 * @author mg
 */
public interface DbControl {


    /**
     * Returns the working datamodel.
     * @return Datamodel instance.
     * @see Datamodel
     */
    public ApplicationModel<?, ?> getModel();

    /**
     * Sets a datamodel to work with.
     * @param aModel Datamodel instance.
     * @throws Exception
     * @see Datamodel
     */
    public void setModel(ApplicationModel<?, ?> aModel) throws Exception;

    /**
     * Begins an udate session. While update session is in progress, no data editing events will be generated.
     */
    public void beginUpdate();

    /**
     * Returns whether unpating session is in progress.
     * @return Updating session progress flag.
     */
    public boolean isUpdating();

    /**
     * Ends an updating session.
     */
    public void endUpdate();

}
