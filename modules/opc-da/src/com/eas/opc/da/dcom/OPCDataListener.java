/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import java.util.Date;

/**
 *
 * @author pk
 */
public interface OPCDataListener
{
    public void dataChanged(Integer transid, Integer group, Integer masterQuality, Integer masterError, Integer[] clientHandles, Object[] values, Short[] qualities, Date[] timeStamps, Integer[] errors);

    public void readCompleted(Integer transid, Integer group, Integer masterQuality, Integer masterError, Integer[] clientHandles, Object[] values, Short[] qualities, Date[] timeStamps, Integer[] errors);

    public void writeCompleted(Integer transid, Integer group, Integer masterError, Integer[] clientHandles, Integer[] errors);

    public void cancelCompleted(Integer transid, Integer group);
}
