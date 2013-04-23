/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.model;

import com.bearsoft.rowset.metadata.Field;

/**
 *
 * @author mg
 */
public interface ModelVisitor {

    public void visit(Model model);

    public void visit(Entity entity);

    public void visit(ParametersEntity entity);

    public void visit(Relation relation);

    public void visit(Field aField);
}
