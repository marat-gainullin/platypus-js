/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.opc.da.dcom;

import java.util.Date;
import java.util.logging.Logger;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIFlags;
import org.jinterop.dcom.core.JILocalCoClass;
import org.jinterop.dcom.core.JILocalInterfaceDefinition;
import org.jinterop.dcom.core.JILocalMethodDescriptor;
import org.jinterop.dcom.core.JILocalParamsDescriptor;
import org.jinterop.dcom.core.JIStruct;
import org.jinterop.dcom.core.JIVariant;

/**
 *
 * @author pk
 */
public class OPCDataCallbackImpl
{
    final static String IID_IOPCDataCallback = "39c13a70-011e-11d0-9675-0020afd8adb3";
    private OPCDataListener listener;
    private JILocalCoClass localClass;

    OPCDataCallbackImpl(OPCDataListener listener) throws JIException
    {
        this.listener = listener;
        createCoClass();
    }

    JILocalCoClass getLocalClass()
    {
        return localClass;
    }

    private void createCoClass() throws JIException
    {
        localClass = new JILocalCoClass(new JILocalInterfaceDefinition(IID_IOPCDataCallback, false), this, false);

        JILocalParamsDescriptor onDataChangeParams = new JILocalParamsDescriptor();
        onDataChangeParams.addInParamAsType(Integer.class, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT); //dwTransid
        onDataChangeParams.addInParamAsType(Integer.class, JIFlags.FLAG_NULL); //hGroup
        onDataChangeParams.addInParamAsType(Integer.class, JIFlags.FLAG_NULL); //hrMasterquality
        onDataChangeParams.addInParamAsType(Integer.class, JIFlags.FLAG_NULL); //hrMastererror
        onDataChangeParams.addInParamAsType(Integer.class, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT); //dwCount
        onDataChangeParams.addInParamAsObject(new JIArray(Integer.class, null, 1, true), JIFlags.FLAG_NULL); //phClientItems
        onDataChangeParams.addInParamAsObject(new JIArray(JIVariant.class, null, 1, true), JIFlags.FLAG_NULL); //pvValues
        onDataChangeParams.addInParamAsObject(new JIArray(Short.class, null, 1, true), JIFlags.FLAG_NULL); //pwQualities
        onDataChangeParams.addInParamAsObject(new JIArray(new FileTime().getStruct(), null, 1, true), JIFlags.FLAG_NULL); //pftTimeStamps
        onDataChangeParams.addInParamAsObject(new JIArray(Integer.class, null, 1, true), JIFlags.FLAG_NULL); //pErrors

        JILocalMethodDescriptor onDataChangeDesc = new JILocalMethodDescriptor("OnDataChange", 0, onDataChangeParams);
        localClass.getInterfaceDefinition().addMethodDescriptor(onDataChangeDesc);

        JILocalParamsDescriptor onReadCompleteParams = new JILocalParamsDescriptor();
        onReadCompleteParams.addInParamAsType(Integer.class, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT); //dwTransid
        onReadCompleteParams.addInParamAsType(Integer.class, JIFlags.FLAG_NULL); //hGroup
        onReadCompleteParams.addInParamAsType(Integer.class, JIFlags.FLAG_NULL); //hrMasterquality
        onReadCompleteParams.addInParamAsType(Integer.class, JIFlags.FLAG_NULL); //hrMastererror
        onReadCompleteParams.addInParamAsType(Integer.class, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT); //dwCount
        onReadCompleteParams.addInParamAsObject(new JIArray(Integer.class, null, 1, true), JIFlags.FLAG_NULL); //phClientItems
        onReadCompleteParams.addInParamAsObject(new JIArray(JIVariant.class, null, 1, true), JIFlags.FLAG_NULL); //pvValues
        onReadCompleteParams.addInParamAsObject(new JIArray(Short.class, null, 1, true), JIFlags.FLAG_NULL); //pwQualities
        onReadCompleteParams.addInParamAsObject(new JIArray(new FileTime().getStruct(), null, 1, true), JIFlags.FLAG_NULL); //pftTimeStamps
        onReadCompleteParams.addInParamAsObject(new JIArray(Integer.class, null, 1, true), JIFlags.FLAG_NULL); //pErrors
        JILocalMethodDescriptor onReadCompleteDesc = new JILocalMethodDescriptor("OnReadComplete", 1, onReadCompleteParams);
        localClass.getInterfaceDefinition().addMethodDescriptor(onReadCompleteDesc);

        JILocalParamsDescriptor onWriteCompleteParams = new JILocalParamsDescriptor();
        onWriteCompleteParams.addInParamAsType(Integer.class, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT); //dwTransid
        onWriteCompleteParams.addInParamAsType(Integer.class, JIFlags.FLAG_NULL); //hGroup
        onWriteCompleteParams.addInParamAsType(Integer.class, JIFlags.FLAG_NULL); //hrMastererror
        onWriteCompleteParams.addInParamAsType(Integer.class, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT); //dwCount
        onWriteCompleteParams.addInParamAsObject(new JIArray(Integer.class, null, 1, true), JIFlags.FLAG_NULL); //pClientHandles
        onWriteCompleteParams.addInParamAsObject(new JIArray(Integer.class, null, 1, true), JIFlags.FLAG_NULL); //pErrors
        JILocalMethodDescriptor onWriteCompleteDesc = new JILocalMethodDescriptor("OnWriteComplete", 2, onWriteCompleteParams);
        localClass.getInterfaceDefinition().addMethodDescriptor(onWriteCompleteDesc);

        JILocalParamsDescriptor onCancelCompleteParams = new JILocalParamsDescriptor();
        onCancelCompleteParams.addInParamAsType(Integer.class, JIFlags.FLAG_REPRESENTATION_UNSIGNED_INT); //dwTransid
        onCancelCompleteParams.addInParamAsType(Integer.class, JIFlags.FLAG_NULL); //hGroup
        JILocalMethodDescriptor onCancelCompleteDesc = new JILocalMethodDescriptor("OnCancelComplete", 3, onCancelCompleteParams);
        localClass.getInterfaceDefinition().addMethodDescriptor(onCancelCompleteDesc);
    }

    public void OnDataChange(int dwTransid, int hGroup, int hrMasterQuality, int hrMasterError, int dwCount,
            JIArray phClientItems, JIArray pvValues, JIArray pwQualities, JIArray pftTimeStamps, JIArray pErrors) throws JIException
    {
        Logger.getLogger(OPCDataCallbackImpl.class.getName()).finest("OnDataChange, transid="+dwTransid);
        Integer[] clientHandles = (Integer[]) phClientItems.getArrayInstance();
        Object[] values = new Object[dwCount];
        Short[] qualities = (Short[]) pwQualities.getArrayInstance();
        Date[] timeStamps = new Date[dwCount];
        Integer[] errors = (Integer[]) pErrors.getArrayInstance();

        JIStruct[] fileTimeStructs = (JIStruct[]) pftTimeStamps.getArrayInstance();
        for (int i = 0; i < fileTimeStructs.length; i++)
            timeStamps[i] = new FileTime(fileTimeStructs[i]).getTime();

        JIVariant[] valueVariants = (JIVariant[]) pvValues.getArrayInstance();
        for (int i = 0; i < valueVariants.length; i++)
            values[i] = valueVariants[i].getObject();

        listener.dataChanged(dwTransid, hGroup, hrMasterQuality, hrMasterError, clientHandles, values, qualities, timeStamps, errors);
    }

    public void OnReadComplete(int dwTransid, int hGroup, int hrMasterQuality, int hrMasterError, int dwCount,
            JIArray phClientItems, JIArray pvValues, JIArray pwQualities, JIArray pftTimeStamps, JIArray pErrors) throws JIException
    {
        Logger.getLogger(OPCDataCallbackImpl.class.getName()).finest("OnReadComplete, transid="+dwTransid);
        Integer[] clientHandles = (Integer[]) phClientItems.getArrayInstance();
        Object[] values = new Object[dwCount];
        Short[] qualities = (Short[]) pwQualities.getArrayInstance();
        Date[] timeStamps = new Date[dwCount];
        Integer[] errors = (Integer[]) pErrors.getArrayInstance();

        JIStruct[] fileTimeStructs = (JIStruct[]) pftTimeStamps.getArrayInstance();
        for (int i = 0; i < fileTimeStructs.length; i++)
            timeStamps[i] = new FileTime(fileTimeStructs[i]).getTime();

        JIVariant[] valueVariants = (JIVariant[]) pvValues.getArrayInstance();
        for (int i = 0; i < valueVariants.length; i++)
            values[i] = valueVariants[i].getObject();

        listener.readCompleted(dwTransid, hGroup, hrMasterQuality, hrMasterError, clientHandles, values, qualities, timeStamps, errors);
    }

    public void OnWriteComplete(int dwTransid, int hGroup, int hrMasterError, int dwCount,
            JIArray pClientHandles, JIArray pErrors)
    {
        Logger.getLogger(OPCDataCallbackImpl.class.getName()).finest("OnWriteComplete, transid="+dwTransid);
        Integer[] clientHandles = (Integer[]) pClientHandles.getArrayInstance();
        Integer[] errors = (Integer[]) pErrors.getArrayInstance();
        listener.writeCompleted(dwTransid, hGroup, hrMasterError, clientHandles, errors);
    }

    public void OnCancelComplete(int dwTransid, int hGroup)
    {
        Logger.getLogger(OPCDataCallbackImpl.class.getName()).finest("OnCancelComplete, transid="+dwTransid);
        listener.cancelCompleted(dwTransid, hGroup);
    }
}
