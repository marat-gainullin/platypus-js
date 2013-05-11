/* Datamodel license.
 * Exclusive rights on this code in any form
 * are belong to it's author. This code was
 * developed for commercial purposes only. 
 * For any questions and any actions with this
 * code in any form you have to contact to it's
 * author.
 * All rights reserved.
 */
package com.eas.client.model.gui.edits;

/**
 *
 * @author mg
 */
public class EditEntityContentEdit extends DatamodelEdit {

    public EditEntityContentEdit() {
        super();
    }

    @Override
    public boolean isNeedConnectors2Reroute() {
        return true;
    }

    @Override
    protected void redoWork() {
    }

    @Override
    protected void undoWork() {
    }
}