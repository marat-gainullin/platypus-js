/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.dataflow;

/**
 *
 * @author mg
 */
public interface TransactionListener {

    public void commited() throws Exception;

    public void rolledback() throws Exception;
}
