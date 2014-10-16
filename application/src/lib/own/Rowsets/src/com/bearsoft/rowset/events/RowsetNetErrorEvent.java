/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.events;

import com.bearsoft.rowset.Rowset;

/**
 *
 * @author mg
 */
public class RowsetNetErrorEvent extends RowsetEvent {

    protected Exception errorCause;

    public RowsetNetErrorEvent(Rowset source, Exception anErrorMessage) {
        super(source, RowsetEventMoment.AFTER);
        errorCause = anErrorMessage;
    }

    public Exception getErrorMessage() {
        return errorCause;
    }

}
