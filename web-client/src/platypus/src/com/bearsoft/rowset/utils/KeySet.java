/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.rowset.utils;

import java.util.ArrayList;

/**
 *
 * @author mg
 */
public class KeySet extends ArrayList<Object> {

    @Override
    public synchronized void add(int index, Object element) {
        check4Compatible(element);
        super.add(index, element);
    }

    @Override
    public synchronized boolean add(Object element) {
        check4Compatible(element);
        return super.add(element);
    }

    private void check4Compatible(Object o) {
        /*
         * Replaced by rowset converters. Converters should be used wherever converting takes place.
        if(o instanceof BigInteger)
        {
        BigDecimal casted = new BigDecimal((BigInteger)o);
        o = casted;
        }
        else if(o instanceof BigDecimal)
        {
        }
        else if(o instanceof Double)
        {
        BigDecimal casted = new BigDecimal((Double)o);
        o = casted;
        }
        else if(o instanceof Integer)
        {
        BigDecimal casted = new BigDecimal((Integer)o);
        o = casted;
        }
        else if(o instanceof Long)
        {
        BigDecimal casted = new BigDecimal((Long)o);
        o = casted;
        }
        else if(o instanceof Float)
        {
        BigDecimal casted = new BigDecimal((Float)o);
        o = casted;
        }
        else if(o instanceof Short)
        {
        BigDecimal casted = new BigDecimal((Short)o);
        o = casted;
        }
        else if(o instanceof Byte)
        {
        BigDecimal casted = new BigDecimal((Byte)o);
        o = casted;
        }else 
        if (o instanceof Clob) {
            throw new IllegalArgumentException("can't filter with Clob values");
        } else if (o instanceof Blob) {
            throw new IllegalArgumentException("can't filter with Blob values");
        } else if (o instanceof Ref) {
            throw new IllegalArgumentException("can't filter with Ref values");
        } else if (o instanceof RowId) {
            throw new IllegalArgumentException("can't filter with RowId values");
        } else if (o instanceof Struct) {
            throw new IllegalArgumentException("can't filter with Struct values");
        } else if (o instanceof SQLXML) {
            throw new IllegalArgumentException("can't filter with SQLXML values");
        }
         */
    }
}
