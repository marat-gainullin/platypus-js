package com.eas.script;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation for detecting event methods
 * 
 * @author vv
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventMethod {
    
    /**
     * Event's script class
     * @return an event's class
     */
    public Class<?> eventClass();
    
}
