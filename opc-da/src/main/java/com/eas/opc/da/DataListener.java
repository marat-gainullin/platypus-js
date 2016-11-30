/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da;

/**
 *
 * @author pk
 */
public interface DataListener
{
    public void dataChanged(int transactionID, Group group, int masterQuality, int masterError, Item.State[] result);

    public void readCompleted(int transactionID, Group group, int masterQuality, int masterError, Item.State[] result);

    public void writeCompleted(int transactionID, Group group, int masterError, Item.State[] result);

    public void cancelCompleted(int transactionID, Group group);
}
