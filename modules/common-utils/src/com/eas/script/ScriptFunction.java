package com.eas.script;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for detecting script-avaliable methods
 * 
 * @author vv
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptFunction {
    
    /**
     * Function's name
     * @return name
     */
    public String name() default "";// NOI18N
    
    /**
     * Function's jsDoc
     * @return jsDoc section text
     */
    public String jsDoc() default "";// NOI18N
    
    /**
     * Function's parameters names
     * @return 
     */
    public String[] params() default {};
}
