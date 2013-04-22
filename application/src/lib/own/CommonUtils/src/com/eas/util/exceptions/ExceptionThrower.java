/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.util.exceptions;

import java.beans.ExceptionListener;

/**
 *
 * @author pk
 */
public interface ExceptionThrower {
    public void addExceptionListener(ExceptionListener l);

    public void removeExceptionListener(ExceptionListener l);
}
