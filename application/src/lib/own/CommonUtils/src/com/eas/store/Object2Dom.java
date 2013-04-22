/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.store;

import com.eas.xml.dom.XmlDomUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mg
 */
public class Object2Dom {

    public final static String BEANY_GETTER_PREFIX = "get";
    public final static String BEANY_IS_PREFIX = "is";
    public final static String BEANY_SETTER_PREFIX = "set";
    //
    public final static String MAP_KEY_TAG_NAME = "key";
    public final static String MAP_VALUE_TAG_NAME = "value";
    public final static String PRIMITIVE_VALUE_ATTR_NAME = "data";
    protected static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public static Document transform(Object o, String rootTagName) {
        return transform(o, rootTagName, true);
    }

    public static Document transform(Object o, String rootTagName, boolean saveDefaultValues) {
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
	    doc.setXmlStandalone(true);
            Element root = doc.createElement(rootTagName);
            doc.appendChild(root);
            generateFromGetters(doc, root, o, saveDefaultValues);
            return doc;
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Object2Dom.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected static void generateFromGetters(Document doc, Element currentNode, Object o, boolean saveDefaultValues) {
        if (o != null) {
            Object defaultInstance = null;
            if (!saveDefaultValues) {
                try {
                    defaultInstance = o.getClass().newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    defaultInstance = null;
                }
            }
            Method[] methods = o.getClass().getMethods();
            if (methods != null && methods.length > 0) {
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i] != null) {
                        Method method = methods[i];
                        if (!method.isBridge() && !method.isSynthetic() && !method.isVarArgs()) {
                            int modifiers = method.getModifiers();
                            Class<?>[] pTypes = method.getParameterTypes();

                            Serial serAnn = method.getAnnotation(Serial.class);
                            ClassedSerial serClassedAnn = method.getAnnotation(ClassedSerial.class);

                            SerialCollection serCollAnn = method.getAnnotation(SerialCollection.class);
                            ClassedSerialCollection serClassedCollAnn = method.getAnnotation(ClassedSerialCollection.class);

                            SerialMap serMapAnn = method.getAnnotation(SerialMap.class);
                            ClassedSerialMap serClassedMapAnn = method.getAnnotation(ClassedSerialMap.class);

                            if ((serAnn != null || serClassedAnn != null
                                    || serCollAnn != null || serClassedCollAnn != null
                                    || serMapAnn != null || serClassedMapAnn != null)
                                    && Modifier.isPublic(modifiers)
                                    && !Modifier.isAbstract(modifiers)
                                    && !Modifier.isStatic(modifiers)) {
                                if (pTypes == null || pTypes.length == 0) {
                                    String mName = method.getName();
                                    if (mName != null && !mName.isEmpty()
                                            && ((mName.length() > BEANY_GETTER_PREFIX.length() && mName.startsWith(BEANY_GETTER_PREFIX))
                                            || (mName.length() > BEANY_IS_PREFIX.length() && mName.startsWith(BEANY_IS_PREFIX)))) {
                                        String mNameTail = mName;
                                        if (mName.startsWith(BEANY_GETTER_PREFIX)) {
                                            mNameTail = mName.substring(BEANY_GETTER_PREFIX.length());
                                        } else {
                                            mNameTail = mName.substring(BEANY_IS_PREFIX.length());
                                        }
                                        String firstLetter = mNameTail.substring(0, 1).toLowerCase();
                                        String tagName = firstLetter;
                                        if (mNameTail.length() > 1) {
                                            tagName += mNameTail.substring(1);
                                        }
                                        try {
                                            Object value = method.invoke(o);
                                            if (defaultInstance == null || !isOfPrimitiveType(value) || !isValuesEqual(value, method.invoke(defaultInstance))) {
                                                generateAttributeOrTag(doc, currentNode, tagName, value, serCollAnn, serMapAnn, serClassedAnn, serClassedCollAnn, serClassedMapAnn, saveDefaultValues);
                                            }
                                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                            Logger.getLogger(Object2Dom.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isOfPrimitiveType(Object o) {
        return o instanceof Integer
                || o instanceof Float
                || o instanceof Short
                || o instanceof Long
                || o instanceof Double
                || o instanceof Byte
                || o instanceof Boolean
                || o instanceof String
                || o instanceof Character;
    }

    private static void generatePrimitiveValue(Element currentNode, Object o) {
        if (currentNode != null) {
            if (o instanceof Integer) {
                currentNode.setAttribute(PRIMITIVE_VALUE_ATTR_NAME, String.valueOf((Integer) o));
            } else if (o instanceof Float) {
                currentNode.setAttribute(PRIMITIVE_VALUE_ATTR_NAME, String.valueOf((Float) o));
            } else if (o instanceof Short) {
                currentNode.setAttribute(PRIMITIVE_VALUE_ATTR_NAME, String.valueOf((Short) o));
            } else if (o instanceof Long) {
                currentNode.setAttribute(PRIMITIVE_VALUE_ATTR_NAME, String.valueOf((Long) o));
            } else if (o instanceof Double) {
                currentNode.setAttribute(PRIMITIVE_VALUE_ATTR_NAME, String.valueOf((Double) o));
            } else if (o instanceof Byte) {
                currentNode.setAttribute(PRIMITIVE_VALUE_ATTR_NAME, String.valueOf((Byte) o));
            } else if (o instanceof Boolean) {
                currentNode.setAttribute(PRIMITIVE_VALUE_ATTR_NAME, String.valueOf((Boolean) o));
            } else if (o instanceof String) {
                currentNode.setAttribute(PRIMITIVE_VALUE_ATTR_NAME, String.valueOf((String) o));
            } else if (o instanceof Character) {
                currentNode.setAttribute(PRIMITIVE_VALUE_ATTR_NAME, String.valueOf((Character) o));
            }
        }
    }

    private static void generateAttributeOrTag(Document doc, Element currentNode, String tagName, Object tagValue, SerialCollection elementDefinition, SerialMap entryDefinition, ClassedSerial classedTagDefinition, ClassedSerialCollection classedElementDefinition, ClassedSerialMap classedEntryDefinition, boolean saveDefaultValues) {
        if (tagName != null && !tagName.isEmpty()
                && currentNode != null && doc != null
                && tagValue != null) {
            if (tagValue instanceof Integer) {
                currentNode.setAttribute(tagName, String.valueOf((Integer) tagValue));
            } else if (tagValue instanceof Float) {
                currentNode.setAttribute(tagName, String.valueOf((Float) tagValue));
            } else if (tagValue instanceof Short) {
                currentNode.setAttribute(tagName, String.valueOf((Short) tagValue));
            } else if (tagValue instanceof Long) {
                currentNode.setAttribute(tagName, String.valueOf((Long) tagValue));
            } else if (tagValue instanceof Double) {
                currentNode.setAttribute(tagName, String.valueOf((Double) tagValue));
            } else if (tagValue instanceof Byte) {
                currentNode.setAttribute(tagName, String.valueOf((Byte) tagValue));
            } else if (tagValue instanceof Boolean) {
                currentNode.setAttribute(tagName, String.valueOf((Boolean) tagValue));
            } else if (tagValue instanceof String) {
                currentNode.setAttribute(tagName, String.valueOf((String) tagValue));
            } else if (tagValue instanceof Character) {
                currentNode.setAttribute(tagName, String.valueOf((Character) tagValue));
            } else {
                if (tagValue instanceof Collection) {
                    if (elementDefinition != null || classedElementDefinition != null) {
                        Collection col = (Collection) tagValue;
                        if (elementDefinition != null) {
                            tagName = elementDefinition.elementTagName();
                        } else if (classedElementDefinition != null) {
                            tagName = classedElementDefinition.elementTagName();
                        }
                        for (Object oElement : col) {
                            Element nestedElement = doc.createElement(tagName);
                            if (classedElementDefinition != null && oElement != null) {
                                nestedElement.setAttribute(classedElementDefinition.elementClassHint(), oElement.getClass().getSimpleName());
                            }
                            currentNode.appendChild(nestedElement);
                            if (isOfPrimitiveType(oElement)) {
                                generatePrimitiveValue(nestedElement, oElement);
                            } else {
                                generateFromGetters(doc, nestedElement, oElement, saveDefaultValues);
                            }
                        }
                    }
                } else if (tagValue instanceof Map) {
                    if (entryDefinition != null || classedEntryDefinition != null) {
                        if (entryDefinition != null) {
                            tagName = entryDefinition.elementTagName();
                        } else {
                            tagName = classedEntryDefinition.elementTagName();
                        }
                        Map map = (Map) tagValue;
                        for (Object oEntry : map.entrySet()) {
                            Entry entry = (Entry) oEntry;
                            Object oKey = entry.getKey();
                            Object oValue = entry.getValue();
                            Element nestedElement = doc.createElement(tagName);
                            Element keyElement = doc.createElement(MAP_KEY_TAG_NAME);
                            Element valueElement = doc.createElement(MAP_VALUE_TAG_NAME);
                            if (classedEntryDefinition != null && oValue != null) {
                                valueElement.setAttribute(classedEntryDefinition.elementClassHint(), oValue.getClass().getSimpleName());
                            }
                            currentNode.appendChild(nestedElement);
                            nestedElement.appendChild(keyElement);
                            nestedElement.appendChild(valueElement);
                            if (isOfPrimitiveType(oKey)) {
                                generatePrimitiveValue(keyElement, oKey);
                            } else {
                                generateFromGetters(doc, keyElement, oKey, saveDefaultValues);
                            }
                            if (isOfPrimitiveType(oValue)) {
                                generatePrimitiveValue(valueElement, oValue);
                            } else {
                                generateFromGetters(doc, valueElement, oValue, saveDefaultValues);
                            }
                        }
                    }
                } else {
                    Element nestedElement = doc.createElement(tagName);
                    if (classedTagDefinition != null && tagValue != null) {
                        nestedElement.setAttribute(classedTagDefinition.propertyClassHint(), tagValue.getClass().getSimpleName());
                    }
                    currentNode.appendChild(nestedElement);
                    generateFromGetters(doc, nestedElement, tagValue, saveDefaultValues);
                }
            }
        }
    }

    public static void transform(Object o, Document doc) {
        if (doc != null && o != null) {
            NodeList nl = doc.getChildNodes();
            if (nl != null && nl.getLength() == 1) {
                Node rootNode = nl.item(0);
                if (rootNode != null && rootNode instanceof Element) {
                    readBySetters(o, (Element) rootNode);
                }
            }
        }
    }
    protected static final Object NO_ATTRIBUTE_OR_TAG_MARKER = new Object();
    // we need to cache class setters information for performance
    protected static final Map<String, List<Method>> classedMethodsCache = new HashMap<>();

    protected static void readBySetters(Object o, Element currentNode) {
        if (o != null) {
            String oClassName = o.getClass().getName();
            List<Method> vMethods = classedMethodsCache.get(oClassName);
            if (vMethods != null) {
                for (int i = 0; i < vMethods.size(); i++) {
                    Method method = vMethods.get(i);
                    String mName = method.getName();

                    Class<?>[] pTypes = method.getParameterTypes();
                    ClassedSerial serClassedAnn = method.getAnnotation(ClassedSerial.class);

                    SerialCollection serCollAnn = method.getAnnotation(SerialCollection.class);
                    ClassedSerialCollection serClassedCollAnn = method.getAnnotation(ClassedSerialCollection.class);

                    SerialMap serMapAnn = method.getAnnotation(SerialMap.class);
                    ClassedSerialMap serClassedMapAnn = method.getAnnotation(ClassedSerialMap.class);

                    String mNameTail = mName.substring(BEANY_SETTER_PREFIX.length());
                    String firstLetter = mNameTail.substring(0, 1).toLowerCase();
                    String tagName = firstLetter;
                    if (mNameTail.length() > 1) {
                        tagName += mNameTail.substring(1);
                    }
                    try {
                        Object read = readAttributeOrTag(o, currentNode, tagName, pTypes[0], serCollAnn, serMapAnn, serClassedAnn, serClassedCollAnn, serClassedMapAnn);
                        if (read != NO_ATTRIBUTE_OR_TAG_MARKER) {
                            Object result = method.invoke(o, read);
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(Object2Dom.class.getName()).log(Level.SEVERE, mName, ex);
                    }
                }
            } else {
                vMethods = new ArrayList<>();
                Method[] methods = o.getClass().getMethods();
                if (methods != null && methods.length > 0) {
                    for (int i = 0; i < methods.length; i++) {
                        if (methods[i] != null) {
                            Method method = methods[i];
                            if (!method.isBridge() && !method.isSynthetic() && !method.isVarArgs()) {
                                int modifiers = method.getModifiers();
                                Class<?>[] pTypes = method.getParameterTypes();
                                Class<?> retType = method.getReturnType();

                                Serial serAnn = method.getAnnotation(Serial.class);
                                ClassedSerial serClassedAnn = method.getAnnotation(ClassedSerial.class);

                                SerialCollection serCollAnn = method.getAnnotation(SerialCollection.class);
                                ClassedSerialCollection serClassedCollAnn = method.getAnnotation(ClassedSerialCollection.class);

                                SerialMap serMapAnn = method.getAnnotation(SerialMap.class);
                                ClassedSerialMap serClassedMapAnn = method.getAnnotation(ClassedSerialMap.class);

                                if ((serAnn != null || serClassedAnn != null
                                        || serCollAnn != null || serClassedCollAnn != null
                                        || serMapAnn != null || serClassedMapAnn != null)
                                        && (retType == null || void.class.isAssignableFrom(retType))
                                        && Modifier.isPublic(modifiers)
                                        && !Modifier.isAbstract(modifiers)
                                        && !Modifier.isStatic(modifiers)) {
                                    if (pTypes != null && pTypes.length == 1) {
                                        String mName = method.getName();
                                        if (mName != null && !mName.isEmpty()
                                                && mName.length() > BEANY_SETTER_PREFIX.length() && mName.startsWith(BEANY_SETTER_PREFIX)) {
                                            String mNameTail = mName.substring(BEANY_SETTER_PREFIX.length());
                                            String firstLetter = mNameTail.substring(0, 1).toLowerCase();
                                            String tagName = firstLetter;
                                            if (mNameTail.length() > 1) {
                                                tagName += mNameTail.substring(1);
                                            }
                                            try {
                                                Object read = readAttributeOrTag(o, currentNode, tagName, pTypes[0], serCollAnn, serMapAnn, serClassedAnn, serClassedCollAnn, serClassedMapAnn);
                                                if (read != NO_ATTRIBUTE_OR_TAG_MARKER) {
                                                    Object result = method.invoke(o, read);
                                                }
                                                // Good method. Add it to the cache.
                                                vMethods.add(method);
                                            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                                                Logger.getLogger(Object2Dom.class.getName()).log(Level.SEVERE, mName, ex);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                classedMethodsCache.put(oClassName, vMethods);
            }
        }
    }

    private static boolean isPrimitiveType(Class<?> oType) {
        return Boolean.class.isAssignableFrom(oType)
                || boolean.class.isAssignableFrom(oType)
                || Byte.class.isAssignableFrom(oType)
                || byte.class.isAssignableFrom(oType)
                || Double.class.isAssignableFrom(oType)
                || double.class.isAssignableFrom(oType)
                || Float.class.isAssignableFrom(oType)
                || float.class.isAssignableFrom(oType)
                || Integer.class.isAssignableFrom(oType)
                || int.class.isAssignableFrom(oType)
                || Long.class.isAssignableFrom(oType)
                || long.class.isAssignableFrom(oType)
                || Short.class.isAssignableFrom(oType)
                || short.class.isAssignableFrom(oType)
                || String.class.isAssignableFrom(oType)
                || char.class.isAssignableFrom(oType)
                || Character.class.isAssignableFrom(oType);
    }

    private static Object readPrimitive(Class<?> oType, Element currentNode) {
        if (currentNode != null && oType != null) {
            if (currentNode.hasAttribute(PRIMITIVE_VALUE_ATTR_NAME)) {
                if (Boolean.class.isAssignableFrom(oType)
                        || boolean.class.isAssignableFrom(oType)) {
                    return XmlDomUtils.readBooleanAttribute(currentNode, PRIMITIVE_VALUE_ATTR_NAME, false);
                }
                if (Byte.class.isAssignableFrom(oType)
                        || byte.class.isAssignableFrom(oType)) {
                    return XmlDomUtils.readByteAttribute(currentNode, PRIMITIVE_VALUE_ATTR_NAME, null);
                }
                if (Double.class.isAssignableFrom(oType)
                        || double.class.isAssignableFrom(oType)) {
                    return XmlDomUtils.readDoubleAttribute(currentNode, PRIMITIVE_VALUE_ATTR_NAME, null);
                }
                if (Float.class.isAssignableFrom(oType)
                        || float.class.isAssignableFrom(oType)) {
                    return XmlDomUtils.readFloatAttribute(currentNode, PRIMITIVE_VALUE_ATTR_NAME, null);
                }
                if (Integer.class.isAssignableFrom(oType)
                        || int.class.isAssignableFrom(oType)) {
                    return XmlDomUtils.readIntegerAttribute(currentNode, PRIMITIVE_VALUE_ATTR_NAME, null);
                }
                if (Long.class.isAssignableFrom(oType)
                        || long.class.isAssignableFrom(oType)) {
                    return XmlDomUtils.readLongAttribute(currentNode, PRIMITIVE_VALUE_ATTR_NAME, null);
                }
                if (Short.class.isAssignableFrom(oType)
                        || short.class.isAssignableFrom(oType)) {
                    return XmlDomUtils.readShortAttribute(currentNode, PRIMITIVE_VALUE_ATTR_NAME, null);
                }
                if (String.class.isAssignableFrom(oType)) {
                    return currentNode.getAttribute(PRIMITIVE_VALUE_ATTR_NAME);
                }
                if (Character.class.isAssignableFrom(oType)
                        || char.class.isAssignableFrom(oType)) {
                    String value = currentNode.getAttribute(PRIMITIVE_VALUE_ATTR_NAME);
                    if (value != null && value.length() == 1) {
                        return value.charAt(0);
                    } else {
                        return null;
                    }
                }
            }
        }
        return null;
    }

    protected static Object readAttributeOrTag(Object owner, Element currentNode, String tagName, Class<?> tagType, SerialCollection serCollDefinition, SerialMap serMapDefinition, ClassedSerial classHintDefinition, ClassedSerialCollection serCollClassedDefinition, ClassedSerialMap serMapClassedDefinition) {
        if (currentNode != null && tagName != null
                && !tagName.isEmpty() && tagType != null) {
            if (currentNode.hasAttribute(tagName)) {
                if (Boolean.class.isAssignableFrom(tagType)
                        || boolean.class.isAssignableFrom(tagType)) {
                    return XmlDomUtils.readBooleanAttribute(currentNode, tagName, false);
                }
                if (Byte.class.isAssignableFrom(tagType)
                        || byte.class.isAssignableFrom(tagType)) {
                    return XmlDomUtils.readByteAttribute(currentNode, tagName, null);
                }
                if (Double.class.isAssignableFrom(tagType)
                        || double.class.isAssignableFrom(tagType)) {
                    return XmlDomUtils.readDoubleAttribute(currentNode, tagName, null);
                }
                if (Float.class.isAssignableFrom(tagType)
                        || float.class.isAssignableFrom(tagType)) {
                    return XmlDomUtils.readFloatAttribute(currentNode, tagName, null);
                }
                if (Integer.class.isAssignableFrom(tagType)
                        || int.class.isAssignableFrom(tagType)) {
                    return XmlDomUtils.readIntegerAttribute(currentNode, tagName, null);
                }
                if (Long.class.isAssignableFrom(tagType)
                        || long.class.isAssignableFrom(tagType)) {
                    return XmlDomUtils.readLongAttribute(currentNode, tagName, null);
                }
                if (Short.class.isAssignableFrom(tagType)
                        || short.class.isAssignableFrom(tagType)) {
                    return XmlDomUtils.readShortAttribute(currentNode, tagName, null);
                }
                if (String.class.isAssignableFrom(tagType)) {
                    return currentNode.getAttribute(tagName);
                }
                if (Character.class.isAssignableFrom(tagType)) {
                    String value = currentNode.getAttribute(tagName);
                    if (value != null && value.length() == 1) {
                        return value.charAt(0);
                    } else {
                        return null;
                    }
                }
            } else {
                if (Collection.class.isAssignableFrom(tagType) && (serCollDefinition != null || serCollClassedDefinition != null)) {
                    if (serCollDefinition != null) {
                        tagName = serCollDefinition.elementTagName();
                    } else {
                        tagName = serCollClassedDefinition.elementTagName();
                    }
                } else if (Map.class.isAssignableFrom(tagType) && (serMapDefinition != null || serMapClassedDefinition != null)) {
                    if (serMapDefinition != null) {
                        tagName = serMapDefinition.elementTagName();
                    } else {
                        tagName = serMapClassedDefinition.elementTagName();
                    }
                }

                // collection's or map's elements/entries list
                List<Element> nl = XmlDomUtils.elementsByTagName(currentNode, tagName);
                if (nl != null && !nl.isEmpty()) {
                    try {
                        Object tagValue = null;
                        if (serCollDefinition != null) {
                            tagValue = serCollDefinition.deserializeAs().newInstance();
                        } else if (serCollClassedDefinition != null) {
                            tagValue = serCollClassedDefinition.deserializeAs().newInstance();
                        } else if (serMapDefinition != null) {
                            tagValue = serMapDefinition.deserializeAs().newInstance();
                        } else if (serMapClassedDefinition != null) {
                            tagValue = serMapClassedDefinition.deserializeAs().newInstance();
                        } else {
                            try {
                                tagValue = tagType.newInstance();
                            } catch (InstantiationException | IllegalAccessException ex) {
                                // Abstract classes case.
                                tagValue = null;
                                // Hope that containing class supports PropertiesSimpleFactory interface.
                            }
                        }
                        if (tagValue != null && Collection.class.isAssignableFrom(tagType) && (serCollDefinition != null || serCollClassedDefinition != null)) {
                            Class<?> elementType = null;
                            if (serCollDefinition != null) {
                                elementType = serCollDefinition.elementType();
                            }
                            for (int i = 0; i < nl.size(); i++) {
                                Element node = nl.get(i);
                                if (node != null) {
                                    Object element = null;
                                    if (elementType != null) {
                                        if (node.hasAttributes() || node.hasChildNodes()) {
                                            if (isPrimitiveType(elementType)) {
                                                element = readPrimitive(elementType, node);
                                            } else {
                                                element = elementType.newInstance();
                                                readBySetters(element, node);
                                            }
                                        }
                                    } else {
                                        String attrName = serCollClassedDefinition.elementClassHint();
                                        if (node.hasAttribute(attrName)
                                                && owner instanceof PropertiesSimpleFactory) {
                                            String className = node.getAttribute(attrName);
                                            element = ((PropertiesSimpleFactory) owner).createPropertyObjectInstance(className);
                                            readBySetters(element, node);
                                        }
                                    }
                                    ((Collection) tagValue).add(element);
                                }

                            }
                        } else if (tagValue != null && Map.class.isAssignableFrom(tagType) && (serMapDefinition != null || serMapClassedDefinition != null)) {
                            Class<?> keyType = null;
                            if (serMapDefinition != null) {
                                keyType = serMapDefinition.keyType();
                            } else {
                                keyType = serMapClassedDefinition.keyType();
                            }
                            Class<?> valueType = null;
                            if (serMapDefinition != null) {
                                valueType = serMapDefinition.elementType();
                            }
                            for (int i = 0; i < nl.size(); i++) {
                                Element node = nl.get(i);
                                if (node != null) {
                                    List<Element> keyEls = XmlDomUtils.elementsByTagName(node, MAP_KEY_TAG_NAME);
                                    List<Element> valEls = XmlDomUtils.elementsByTagName(node, MAP_VALUE_TAG_NAME);
                                    if (keyEls != null && keyEls.size() == 1
                                            && valEls != null && valEls.size() == 1) {
                                        Object key = null;
                                        Object val = null;
                                        Element keyEl = keyEls.get(0);
                                        Element valEl = valEls.get(0);
                                        // key
                                        if (isPrimitiveType(keyType)) {
                                            key = readPrimitive(keyType, keyEl);
                                        } else {
                                            key = keyType.newInstance();
                                            readBySetters(key, keyEl);
                                        }
                                        // value
                                        if (valueType != null) {
                                            if (valEl.hasAttributes() || valEl.hasChildNodes()) {
                                                if (isPrimitiveType(valueType)) {
                                                    val = readPrimitive(valueType, valEl);
                                                } else {
                                                    val = valueType.newInstance();
                                                    readBySetters(val, valEl);
                                                }
                                            }
                                        } else {
                                            if (serMapClassedDefinition != null) {
                                                String attrName = serMapClassedDefinition.elementClassHint();
                                                if (valEl.hasAttribute(attrName)
                                                        && owner instanceof PropertiesSimpleFactory) {
                                                    String className = valEl.getAttribute(attrName);
                                                    val = ((PropertiesSimpleFactory) owner).createPropertyObjectInstance(className);
                                                    readBySetters(val, valEl);
                                                }
                                            }
                                        }
                                        ((Map) tagValue).put(key, val);
                                    }
                                }

                            }
                        } else if (nl.size() == 1) {
                            Node node = nl.get(0);
                            if (node != null && node instanceof Element) {
                                Element nestedElement = (Element) node;
                                if (classHintDefinition != null) {
                                    String attrName = classHintDefinition.propertyClassHint();
                                    if (owner instanceof PropertiesSimpleFactory) {
                                        if (nestedElement.hasAttribute(attrName)) {
                                            String className = nestedElement.getAttribute(attrName);
                                            tagValue = ((PropertiesSimpleFactory) owner).createPropertyObjectInstance(className);
                                        }
                                    }
                                }
                                if (tagValue != null) {
                                    readBySetters(tagValue, nestedElement);
                                }
                            }
                        }
                        return tagValue;
                    } catch (InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(Object2Dom.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    return NO_ATTRIBUTE_OR_TAG_MARKER;
                }
            }
        }
        return null;
    }

    public static boolean isValuesEqual(Object value1, Object value2) {
        if (value1 == null && value2 != null) {
            return false;
        }
        if (value2 == null && value1 != null) {
            return false;
        }
        if (value1 != null && value2 != null && !value1.equals(value2)) {
            return false;
        }
        return true;
    }
}
