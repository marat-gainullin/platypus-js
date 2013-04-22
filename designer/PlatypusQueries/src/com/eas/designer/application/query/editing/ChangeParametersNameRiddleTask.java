/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.eas.designer.application.query.editing.riddle.RiddleTask;
import net.sf.jsqlparser.expression.NamedParameter;

/**
 *
 * @author mg
 */
public class ChangeParametersNameRiddleTask implements RiddleTask {

    protected String oldName;
    protected String newName;

    public ChangeParametersNameRiddleTask(String aOldName, String aNewName) {
        super();
        oldName = aOldName;
        newName = aNewName;
    }

    @Override
    public boolean needToDelete(Object aExpression) {
        if (aExpression instanceof NamedParameter) {
            NamedParameter np = (NamedParameter) aExpression;
            if (np.getName() != null && np.getName().equalsIgnoreCase(oldName)) {
                np.setName(newName);
            }
        }
        return false;
    }

    @Override
    public void markAsDeleted(Object aExpression) {
    }

    @Override
    public boolean markedAsDeleted(Object aExpression) {
        return false;
    }

    @Override
    public void observe(Object aExpression) {
    }
}
