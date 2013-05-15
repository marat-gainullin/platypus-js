/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.xml.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mg
 */
public class XmlDomUtils {

    public static BigDecimal readBigDecimalAttribute(Element aElement, String attributeName, BigDecimal defaultValue) {
        if (aElement != null) {
            String aValue = aElement.getAttribute(attributeName);
            if (aValue != null && !aValue.isEmpty()) {
                aValue = aValue.trim();
                if (!aValue.isEmpty() && !aValue.toLowerCase().equals("null")) {
                    try {
                        return BigDecimal.valueOf(Double.valueOf(aValue));
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(XmlDomUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return defaultValue;
    }

    public static BigInteger readBigIntegerAttribute(Element aElement, String attributeName, BigInteger defaultValue) {
        if (aElement != null) {
            String aValue = aElement.getAttribute(attributeName);
            if (aValue != null && !aValue.isEmpty()) {
                aValue = aValue.trim();
                if (!aValue.isEmpty() && !aValue.toLowerCase().equals("null")) {
                    try {
                        return BigInteger.valueOf(Long.valueOf(aValue));
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(XmlDomUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return defaultValue;
    }

    public static Date readDateAttribute(Element aElement, String attributeName, Date defaultValue) {
        if (aElement != null) {
            String aValue = aElement.getAttribute(attributeName);
            if (aValue != null && !aValue.isEmpty()) {
                aValue = aValue.trim();
                if (!aValue.isEmpty() && !aValue.toLowerCase().equals("null")) {
                    try {
                        return Date.valueOf(aValue);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(XmlDomUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return defaultValue;
    }

    public static Time readTimeAttribute(Element aElement, String attributeName, Time defaultValue) {
        if (aElement != null) {
            String aValue = aElement.getAttribute(attributeName);
            if (aValue != null && !aValue.isEmpty()) {
                aValue = aValue.trim();
                if (!aValue.isEmpty() && !aValue.toLowerCase().equals("null")) {
                    try {
                        return Time.valueOf(aValue);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(XmlDomUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return defaultValue;
    }

    public static Boolean readBooleanAttribute(Element aElement, String attributeName, Boolean defaultValue) {
        if (aElement != null) {
            String aValue = aElement.getAttribute(attributeName);
            if (aValue != null && !aValue.isEmpty()) {
                aValue = aValue.trim();
                if (!aValue.isEmpty() && !aValue.toLowerCase().equals("null")) {
                    try {
                        return Boolean.valueOf(aValue);
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(XmlDomUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return defaultValue;
    }

    public static Short readShortAttribute(Element aElement, String attributeName, Short defaultValue) {
        if (aElement != null) {
            String aValue = aElement.getAttribute(attributeName);
            if (aValue != null && !aValue.isEmpty()) {
                aValue = aValue.trim();
                if (!aValue.isEmpty() && !aValue.toLowerCase().equals("null")) {
                    try {
                        return Short.valueOf(aValue);
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(XmlDomUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return defaultValue;
    }

    public static Double readDoubleAttribute(Element aElement, String attributeName, Double defaultValue) {
        if (aElement != null) {
            String aValue = aElement.getAttribute(attributeName);
            if (aValue != null && !aValue.isEmpty()) {
                aValue = aValue.trim();
                if (!aValue.isEmpty() && !aValue.toLowerCase().equals("null")) {
                    try {
                        return Double.valueOf(aValue);
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(XmlDomUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return defaultValue;
    }

    public static Float readFloatAttribute(Element aElement, String attributeName, Float defaultValue) {
        if (aElement != null) {
            String aValue = aElement.getAttribute(attributeName);
            if (aValue != null && !aValue.isEmpty()) {
                aValue = aValue.trim();
                if (!aValue.isEmpty() && !aValue.toLowerCase().equals("null")) {
                    try {
                        return Float.valueOf(aValue);
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(XmlDomUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return defaultValue;
    }

    public static Byte readByteAttribute(Element aElement, String attributeName, Byte defaultValue) {
        if (aElement != null) {
            String aValue = aElement.getAttribute(attributeName);
            if (aValue != null && !aValue.isEmpty()) {
                aValue = aValue.trim();
                if (!aValue.isEmpty() && !aValue.toLowerCase().equals("null")) {
                    try {
                        return Byte.valueOf(aValue);
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(XmlDomUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return defaultValue;
    }

    public static Integer readIntegerAttribute(Element aElement, String attributeName, Integer defaulValue) {
        if (aElement != null) {
            String aValue = aElement.getAttribute(attributeName);
            if (aValue != null && !aValue.isEmpty()) {
                aValue = aValue.trim();
                if (!aValue.isEmpty() && !aValue.toLowerCase().equals("null")) {
                    try {
                        return Integer.valueOf(aValue);
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(XmlDomUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return defaulValue;
    }

    public static Long readLongAttribute(Element aElement, String attributeName, Long defaulValue) {
        if (aElement != null) {
            String aValue = aElement.getAttribute(attributeName);
            if (aValue != null && !aValue.isEmpty()) {
                aValue = aValue.trim();
                if (!aValue.isEmpty() && !aValue.toLowerCase().equals("null")) {
                    try {
                        return Long.valueOf(aValue);
                    } catch (NumberFormatException ex) {
                        Logger.getLogger(XmlDomUtils.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        return defaulValue;
    }

    public static List<Element> elementsByTagName(Node element, String aName) {
        if (element != null && aName != null && !aName.isEmpty()) {
            NodeList nl = element.getChildNodes();
            if (nl != null && nl.getLength() > 0) {
                List<Element> res = new ArrayList<>();
                for (int i = 0; i < nl.getLength(); i++) {
                    if (nl.item(i) != null && nl.item(i) instanceof Element) {
                        Element el = (Element) nl.item(i);
                        if (el != null) {
                            String tagName = el.getTagName();
                            if (tagName != null && !tagName.isEmpty()
                                    && tagName.equals(aName)) {
                                res.add(el);
                            }
                        }
                    }
                }
                return res;
            }
        }
        return null;
    }
 
    public static Element getElementByTagName(Element aParent, String aName) {
        NodeList nl = aParent.getElementsByTagName(aName);
        if (nl != null && nl.getLength() == 1 && nl.item(0) instanceof Element) {
            return (Element) nl.item(0);
        } else {
            return null;
        }
    }


}
