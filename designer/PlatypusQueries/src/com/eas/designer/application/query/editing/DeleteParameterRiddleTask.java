/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.eas.client.metadata.Parameter;
import com.eas.designer.application.query.editing.riddle.RiddleTask;
import java.util.HashSet;
import java.util.Set;
import net.sf.jsqlparser.expression.NamedParameter;

/**
 *
 * @author mg
 */
public class DeleteParameterRiddleTask implements RiddleTask {

    protected Parameter param;
    protected Set<Object> deleted = new HashSet<>();

    public DeleteParameterRiddleTask(Parameter aParam) {
        super();
        param = aParam;
    }

    @Override
    public boolean needToDelete(Object aExpression) {
        if (aExpression instanceof NamedParameter) {
            NamedParameter nParam = (NamedParameter) aExpression;
            return nParam.getName().equalsIgnoreCase(param.getName());
        }
        return false;
    }

    @Override
    public void markAsDeleted(Object aExpression) {
        deleted.add(aExpression);
    }

    @Override
    public boolean markedAsDeleted(Object aExpression) {
        return deleted.contains(aExpression);
    }

    @Override
    public void observe(Object aExpression) {
    }
}
