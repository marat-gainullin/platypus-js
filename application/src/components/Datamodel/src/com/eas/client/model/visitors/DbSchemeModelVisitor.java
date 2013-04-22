/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.visitors;

import com.eas.client.model.dbscheme.DbSchemeModel;
import com.eas.client.model.dbscheme.FieldsEntity;

/**
 *
 * @author mg
 */
public interface DbSchemeModelVisitor extends ModelVisitor<FieldsEntity> {

    public void visit(DbSchemeModel aModel);

    public void visit(FieldsEntity aEntity);
}
