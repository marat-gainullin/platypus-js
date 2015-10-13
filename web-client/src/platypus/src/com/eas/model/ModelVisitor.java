/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.model;

import com.eas.client.metadata.Field;

/**
 *
 * @author mg
 */
public interface ModelVisitor {

    public void visit(Model model);

    public void visit(Entity entity);

    public void visit(Relation relation);
    
    public void visit(ReferenceRelation relation);    

    public void visit(Field aField);
}
