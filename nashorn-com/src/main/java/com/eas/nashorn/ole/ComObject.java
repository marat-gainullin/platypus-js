package com.eas.client.scripts.ole;

import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.AbstractJSObject;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.IJIComObject;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

/**
 *
 * @author vv
 */
public class ComObject extends AbstractJSObject {

    private static final String PROPERTY_GET_DELEGATE_NAME = "propertyGetDelegate";
    private static final String FUNCTION_DELEGATE_NAME = "functionCallDelegate";
    private static final String INDEXED_COLLECTIONS_PROPERTY_NAME = "Item";
    IJIComObject comObject;
    IJIDispatch dispatch;

    private ComObject(IJIComObject aComObject) throws JIException {
        super();
        comObject = aComObject;
        dispatch = (IJIDispatch) JIObjectFactory.narrowObject((IJIComObject) comObject.queryInterface(IJIDispatch.IID));
    }

    protected static ComObject getInstance(IJIComObject aComObject) throws JIException {
        return new ComObject(aComObject);
    }

    @Override
    public String getClassName() {
        return ComObject.class.getSimpleName();
    }

    @Override
    public Object getMember(String name) {
        if(super.hasMember(name))
            return super.getMember(name);
        else{
            try {
                int dispId = dispatch.getIDsOfNames(name);
                return functionGet(name, true);
            } catch (JIException ex) {
                return null;
            }
        }
    }

    @Override
    public Object getSlot(int index) {
        if(super.hasSlot(index))
            return super.getSlot(index);
        else{
            try {
                int dispId = dispatch.getIDsOfNames(INDEXED_COLLECTIONS_PROPERTY_NAME);
                return propertyGet(dispId, new Object[]{index});
            } catch (JIException ex) {
                Logger.getLogger(ComObject.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
    }

    @Override
    public void setMember(String name, Object value) {
        try {
            int dispId = dispatch.getIDsOfNames(name);
            propertyPut(dispId, value);
        } catch (JIException ex) {
            super.setMember(name, value);
        }
    }

    @Override
    public boolean hasMember(String name) {
        return super.hasMember(name) || hasComMethod(name);
    }

    private boolean hasComMethod(String name) {
        try {
            dispatch.getIDsOfNames(name);
            return true;
        } catch (JIException ex) {
            //no op
        }
        return false;
    }

    private Object propertyGet(int dispId, Object[] args) throws JIException {
        JIVariant[] resultVariant = dispatch.get(dispId, args);
        return unwrapVariantArray(resultVariant);
    }

    private Object functionGet(String name, boolean isProperty) {
        /*
        Method functionDelegate = null;
        try {
            String delegateName = isProperty ? PROPERTY_GET_DELEGATE_NAME : FUNCTION_DELEGATE_NAME;
            functionDelegate = ComObject.class.getMethod(delegateName, Object[].class, Function.class);
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(ComObject.class.getName()).log(Level.SEVERE, null, ex);
        }
        FunctionObject func = new FunctionObject(name, functionDelegate, start);
        super.put(name, func, start);
        return func;
                */
        return null;
    }
/*
    public static Object propertyGetDelegate(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws JIException {
        ComObject thisComObject = (ComObject) thisObj;
        JIVariant[] resultVariant = null;
        try {
            resultVariant = thisComObject.dispatch.get(((FunctionObject) funObj).getFunctionName(), wrapObjectArray(args));
        } catch (JIException ex) {
            resultVariant = thisComObject.dispatch.callMethodA(((FunctionObject) funObj).getFunctionName(), wrapObjectArray(args));
        }
        return unwrapVariantArray(resultVariant);
    }

    public static Object functionCallDelegate(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws JIException {
        ComObject thisComObject = (ComObject) thisObj;
        JIVariant[] resultVariant = thisComObject.dispatch.callMethodA(((FunctionObject) funObj).getFunctionName(), wrapObjectArray(args));
        return unwrapVariantArray(resultVariant);
    }
*/
    private void propertyPut(int dispId, Object value) throws JIException {
        dispatch.put(dispId, wrapObject(value));
    }

    private static JIVariant[] wrapObjectArray(Object[] os) {
        if (os == null) {
            return null;
        }
        JIVariant[] variantArray = new JIVariant[os.length];
        for (int i = 0; i < os.length; i++) {
            variantArray[i] = wrapObject(os[i]);
        }
        return variantArray;
    }

    private static JIVariant wrapObject(Object o) {
        JIVariant variant = null;
        /*
        if (o instanceof String) {
            variant = new JIVariant((String) o);
        } else {
            variant = new JIVariant(o);
        }
        */
        return variant;
    }

    private static Object unwrapVariantArray(JIVariant[] resultVariant) throws JIException {
        if (resultVariant != null) {
            if (resultVariant.length == 1) {
                return unwrapVariant(resultVariant[0]);
            } else {
                Object[] objects = new Object[resultVariant.length];
                for (int i = 0; i < resultVariant.length; i++) {
                    objects[i] = unwrapVariant(resultVariant[i]);
                }
                return objects;
            }
        }
        return null;
    }

    private static Object unwrapVariant(JIVariant aVariant) throws JIException {
        if (aVariant.getType() == JIVariant.VT_UNKNOWN || aVariant.getType() == JIVariant.VT_DISPATCH) { //TODO: Check if we need other types unwrapping
            return ComObject.getInstance(aVariant.getObjectAsComObject());
        } else if (aVariant.getType() == JIVariant.VT_BSTR) {
            return aVariant.getObjectAsString2();
        } else {
            return aVariant.getObject();
        }
    }
}
