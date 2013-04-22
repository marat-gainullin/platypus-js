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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptFunction {
    /**
     * Function's jsDoc
     * @return jsDoc HTML
     */
    public String jsDoc() default "";// NOI18N
    
    /**
     * Function's jsDoc simple text
     * @return text string
     */
    public String jsDocText() default "";// NOI18N
}
