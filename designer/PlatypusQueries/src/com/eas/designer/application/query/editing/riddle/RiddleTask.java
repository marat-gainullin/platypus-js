/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing.riddle;

import net.sf.jsqlparser.expression.Expression;

/**
 *
 * @author mg
 */
public interface RiddleTask {

    /**
     * Tests whethere an expression is to be deleted.
     * This method also calls observe method.
     * @param aExpression
     * @return True if an expression is really need
     * to be deleted only according to the task.
     */
    public boolean needToDelete(Object aExpression);

    public void markAsDeleted(Object aExpression);

    public boolean markedAsDeleted(Object aExpression);

    public void observe(Object aExpression);
}
