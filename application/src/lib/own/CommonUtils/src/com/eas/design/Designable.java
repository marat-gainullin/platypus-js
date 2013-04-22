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
 * Intended for use with designers. This class is analog of bean info, but it
 * may be used partially on bean's property set. Category is for property set's
 * name. Display name is for display name in property sheets.
 *
 * @author mg
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Designable {

    public String category() default "";

    public String displayName() default "";

    public String description() default "";
}
