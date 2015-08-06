/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.editing;

import com.eas.client.metadata.Field;
import com.eas.client.metadata.Parameter;


/**
 *
 * @author mg
 */
public class GeneratedParameter extends Parameter {

    /**
     * The default constructor.
     */
    public GeneratedParameter() {
        super();
    }

    /**
     * Constructor with name.
     * @param aName Name of the created parameter.
     */
    public GeneratedParameter(String aName) {
        super(aName);
    }

    /**
     * Constructor with name and description.
     * @param aName Name of the created parameter.
     * @param aDescription Description of the created parameter.
     */
    public GeneratedParameter(String aName, String aDescription) {
        super(aName, aDescription);
    }

    /**
     * Constructor with name, description and typeInfo.
     * @param aName Name of the created parameter.
     * @param aDescription Description of the created parameter.
     * @param aType Type info of the created parameter.
     */
    public GeneratedParameter(String aName, String aDescription, String aType) {
        super(aName, aDescription, aType);
    }

    /**
     * Copy constructor of parameter using base field information.
     * @param aSourceParam Source of created parameter.
     */
    public GeneratedParameter(Field aSourceField) {
        super(aSourceField);
    }

    /**
     * Copy constructor of parameter using full parameter information.
     * @param aSourceParam Source of created parameter.
     */
    public GeneratedParameter(Parameter aSourceParam) {
        super(aSourceParam);
    }
}
