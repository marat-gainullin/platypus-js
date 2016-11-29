/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.design;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Intended to hide properties from designers.
 * This is analog of bean info. This approach doesn't lead to manual enumeration of prooperties
 * in beaninfo. May be used partial on the bean's property set.
 * @author mg
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Undesignable {
}
