/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.util;

import com.eas.script.EventMethod;
import java.lang.reflect.Method;

/**
 *
 * @author vv
 */
public class PropertiesUtils {

    public static final String BEANY_PREFIX_GET = "get";// NOI18N
    public static final String BEANY_PREFIX_SET = "set";// NOI18N
    public static final String BEANY_PREFIX_IS = "is";// NOI18N

    public static boolean isBeanPatternMethod(Method method) {
        return ((method.getName().startsWith(BEANY_PREFIX_GET) || method.getName().startsWith(BEANY_PREFIX_IS)) && method.getParameterTypes().length == 0)
                || (method.getName().startsWith(BEANY_PREFIX_SET) && method.getParameterTypes().length == 1);
    }

    public static String getPropertyName(String methodName) {
        String capitalizedPropName = null;
        if (methodName.startsWith(PropertiesUtils.BEANY_PREFIX_GET) || methodName.startsWith(PropertiesUtils.BEANY_PREFIX_SET)) {
            capitalizedPropName = methodName.substring(3);
            assert !capitalizedPropName.isEmpty();
        } else if (methodName.startsWith(PropertiesUtils.BEANY_PREFIX_IS)) {
            capitalizedPropName = methodName.substring(2);
            assert !capitalizedPropName.isEmpty();
        }
        if (capitalizedPropName.length() > 1 && capitalizedPropName.toUpperCase().equals(capitalizedPropName)) {
            return capitalizedPropName;
        } else {
            return capitalizedPropName.substring(0, 1).toLowerCase() + capitalizedPropName.substring(1);
        }
    }

    public static void setPropertyReturnType(PropBox pb, Method method) {
        String typeName = getTypeName(method.getReturnType());
        if (typeName != null) {
            pb.typeName = typeName;
        }
    }

    public static void setPropertyEventClass(PropBox pb, Method method) {
        if (method.isAnnotationPresent(EventMethod.class)) {
            pb.eventClass = method.getAnnotation(EventMethod.class).eventClass();
        }
    }

    public static void setPropertyAccessStatus(PropBox pb, String methodName) {
        if (methodName.startsWith(PropertiesUtils.BEANY_PREFIX_GET) || methodName.startsWith(BEANY_PREFIX_IS)) {
            pb.readable = true;
            pb.readMethodName = methodName;
        } else if (methodName.startsWith(PropertiesUtils.BEANY_PREFIX_SET)) {
            pb.writeable = true;
            pb.writeMethodName = methodName;
        }
    }

    public static boolean isNumberClass(Class<?> clazz) {
        return Number.class.isAssignableFrom(clazz)
                || Byte.TYPE.equals(clazz)
                || Short.TYPE.equals(clazz)
                || Integer.TYPE.equals(clazz)
                || Long.TYPE.equals(clazz)
                || Float.TYPE.equals(clazz)
                || Double.TYPE.equals(clazz);
    }

    public static String getTypeName(Class<?> aType) {
        if (!aType.equals(Void.TYPE)) {
            if (isNumberClass(aType)) {
                return "Number"; //NOI18N
            } else if (Boolean.class.isAssignableFrom(aType) || Boolean.TYPE.equals(aType)) {
                return "Boolean"; //NOI18N
            } else if (aType.isArray()) {
                Class<?> cl = aType;
                int dimensions = 0;
                while (cl.isArray()) {
                    dimensions++;
                    cl = cl.getComponentType();
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < dimensions; i++) {
                    sb.append("[]"); //NOI18N
                }
                return sb.toString();
            } else {
                return aType.getSimpleName();
            }
        }
        return null;
    }

    public static class PropBox {

        public String name;
        public String typeName;
        public Class<?> eventClass;
        public boolean readable;
        public boolean writeable;
        public String jsDoc;

        public String readMethodName;
        public String writeMethodName;
    }
}
