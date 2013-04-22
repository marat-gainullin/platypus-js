/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bearsoft.rowset.wrappers.jdbc;

import com.bearsoft.rowset.compacts.CompactClob;
import java.sql.NClob;

/**
 * JDBC wrapper of NClob interface
 * @author mg
 */
public class NClobImpl extends ClobImpl implements NClob{

    /**
     * JDBC Wrapper constructor.
     * @param aClob Clob to be wrapped
     */
    public NClobImpl(CompactClob aClob) {
        super(aClob);
    }

}
