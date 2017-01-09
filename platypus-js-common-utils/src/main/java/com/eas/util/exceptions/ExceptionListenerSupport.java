/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util.exceptions;

import java.beans.ExceptionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Поддержка списка слушателей исключений.
 *
 * <p>Поскольку он реализует интерфейс <code>ExceptionListener</code>, он может
 * использоваться для построения цепочек слушателей.</p>
 *
 * @author pk
 */
public class ExceptionListenerSupport implements ExceptionListener, ExceptionThrower
{
    private List<ExceptionListener> listeners = new ArrayList<>();

    public void addExceptionListener(ExceptionListener l)
    {
        assert l != null;
        assert l != this; //protect from forming a cycle when firing the exceptionThrown event.
        listeners.add(l);
    }

    public void removeExceptionListener(ExceptionListener l)
    {
        assert l != null;
        listeners.remove(l);
    }

    public void exceptionThrown(Exception ex)
    {
        Object[] ls = listeners.toArray();
        if (ls.length == 0)
            Logger.getLogger(ExceptionListenerSupport.class.getName()).log(Level.SEVERE, null, ex);
        else
        {
            boolean informed = false;
            for (Object o : ls)
                if (o instanceof ExceptionListener)
                {
                    ((ExceptionListener) o).exceptionThrown(ex);
                    informed = true;
                }
                else
                    Logger.getLogger(ExceptionListenerSupport.class.getName()).warning(String.format("Not an instance of exception listener: %s", o.toString()));
            if (!informed)
                Logger.getLogger(ExceptionListenerSupport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
