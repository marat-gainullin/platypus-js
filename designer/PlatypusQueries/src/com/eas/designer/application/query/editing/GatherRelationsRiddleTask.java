/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.eas.designer.application.query.editing.riddle.RiddleTask;
import java.util.HashSet;
import java.util.Set;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Relation;

/**
 *
 * @author mg
 */
public class GatherRelationsRiddleTask implements RiddleTask {

    protected Set<Relation> relations = new HashSet<>();

    public GatherRelationsRiddleTask() {
        super();
    }

    public Set<Relation> getRelations() {
        return relations;
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
        if (aExpression instanceof Relation) {
            assert aExpression instanceof BinaryExpression : "Relations expressions must be of inherited from BinaryExpression class";
            relations.add((Relation) aExpression);
        }
    }
}
