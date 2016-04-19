/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.eas.designer.application.query.editing.riddle.RiddleTask;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import net.sf.jsqlparser.expression.NamedParameter;
import net.sf.jsqlparser.schema.Column;

/**
 *
 * @author mg
 */
public class GatherRelationsSubjectsRiddleTask implements RiddleTask {

    protected Set<String> parametersNames = new HashSet<>();
    protected Set<NamedParameter> parameters = new LinkedHashSet<>();
    protected Set<Column> columns = new HashSet<>();

    public Set<NamedParameter> getParameters() {
        return parameters;
    }

    public Set<Column> getColumns() {
        return columns;
    }

    @Override
    public boolean needToDelete(Object aExpression) {
        observe(aExpression);
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
        if (aExpression instanceof NamedParameter) {
            NamedParameter np = (NamedParameter) aExpression;
            if (!parametersNames.contains(np.getName())) {
                parametersNames.add(np.getName());
                parameters.add(np);
            }
        } else if (aExpression instanceof Column) {
            columns.add((Column) aExpression);
        }
    }
}
