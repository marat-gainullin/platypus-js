/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.utils.scalableui;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 *
 * @author Marat
 */
public class ScalableTransferHandler extends TransferHandler{
    public static final String INNER_DROP_LOCATION_TAG = "ScalableDropLocation";
    public static final String INNER_DROP_ACTION_TAG = "ScalableDropAction";

    protected JScalablePanel scalable = null;
        
    public ScalableTransferHandler(JScalablePanel aScalable)
    {
        super();
        scalable = aScalable;
    }

    protected TransferHandler getScalableTransferHandler()
    {
        if(scalable != null && scalable.getInnerDropTargetComponent() != null)
        {
            Component lc = scalable.getInnerDropTargetComponent();
            if(lc instanceof JComponent)
            {
                JComponent jlc = (JComponent)lc;
                return jlc.getTransferHandler();
            }
        }
        return null;
    }
    
    protected Point getScalableDropTargetPoint()
    {
        if(scalable != null)
            return  scalable.getInnerDropTargetPoint();
        return null;
    }
    
    protected Component getScalableDropTarget()
    {
        if(scalable != null && scalable.getInnerDropTargetComponent() != null)
            return scalable.getInnerDropTargetComponent();
        return null;
    }
    
    @Override
    protected Transferable createTransferable(JComponent c) {
        return null;
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
    }

    @Override
    protected void exportDone(JComponent source, Transferable data, int action) {
    }

    @Override
    public int getSourceActions(JComponent c) {
        TransferHandler th = getScalableTransferHandler();
        if(th != null)
            return th.getSourceActions(c);
        return super.getSourceActions(c);
    }

    @Override
    public Icon getVisualRepresentation(Transferable t) {
        TransferHandler th = getScalableTransferHandler();
        if(th != null)
            return th.getVisualRepresentation(t);
        return super.getVisualRepresentation(t);
    }

    protected class ScalableDropLocation extends TransferHandler.DropLocation{
        ScalableDropLocation(Point pt)
        {
            super(pt);
        }
    }
        
    @Override
    public boolean canImport(TransferSupport support) {
        TransferHandler th = getScalableTransferHandler();
        if(th != null && getScalableDropTargetPoint() != null)
        {
            Component lcomp = getScalableDropTarget();
            if(lcomp instanceof JComponent)
            {
                JComponent jcomp = (JComponent)lcomp;
                jcomp.putClientProperty(INNER_DROP_LOCATION_TAG, new ScalableDropLocation(new Point(getScalableDropTargetPoint())));
                if(support.isDrop())
                    jcomp.putClientProperty(INNER_DROP_ACTION_TAG, support.getUserDropAction());
            }
            return th.canImport(new TransferSupport(lcomp, support.getTransferable()));
        }
        return super.canImport(support);
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        TransferHandler th = getScalableTransferHandler();
        if(th != null)
            return th.canImport(comp, transferFlavors);
        return super.canImport(comp, transferFlavors);
    }

    @Override
    public boolean importData(TransferSupport support) {
        TransferHandler th = getScalableTransferHandler();
        if(th != null && getScalableDropTargetPoint() != null)
        {
            Component lcomp = getScalableDropTarget();
            if(lcomp instanceof JComponent)
            {
                JComponent jcomp = (JComponent)lcomp;
                jcomp.putClientProperty(INNER_DROP_LOCATION_TAG, new ScalableDropLocation(new Point(getScalableDropTargetPoint())));
            }
            return th.importData(new TransferSupport(lcomp, support.getTransferable()));
        }
        return super.importData(support);
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        TransferHandler th = getScalableTransferHandler();
        if(th != null)
            return th.importData( (getScalableDropTarget() instanceof JComponent)? (JComponent)getScalableDropTarget():comp, t);
        return super.importData(comp, t);
    }
    
}
