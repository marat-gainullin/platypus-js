/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.changes;

/**
 *
 * @author mg
 */
public interface ChangeVisitor {
    
    public void visit(Insert aChange) throws Exception;
    public void visit(Update aChange) throws Exception;
    public void visit(Delete aChange) throws Exception;
    public void visit(Command aChange) throws Exception;
}
