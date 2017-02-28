/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.exceptions;

/**
 * Base exception class for grid package.
 * @author mg
 */
public class GridException extends Exception {

    /**
     * {@inheritDoc}
     */
    public GridException() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    public GridException(String aMsg) {
        super(aMsg);
    }

    /**
     * {@inheritDoc}
     */
    public GridException(Exception aCause) {
        super(aCause);
    }
}
