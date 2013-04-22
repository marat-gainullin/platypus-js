/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.dbcontrols;

import java.util.EventObject;

/**
 *
 * @author mg
 */
public interface FakeDbControlEvents {

    public Object selectValue(Object aField);
    public Object handleValue(Object aRowId, Object aCell, Object aRow);
    public Object handleCell(Object aRowId, Object aColumnId, Object aCell, Object aRow);
    public Object link(Object aDataSource);
    public Object mapEvent(EventObject anEvent);
}
