/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms;

import com.eas.script.ScriptFunction;
import javax.swing.JComponent;

/**
 *
 * @author Марат
 */
public interface HasChildren {

    public static final String CHILD_JSDOC = ""
            + "/**\n"
            + " * Gets the container's n-th component.\n"
            + " * @param index the component's index in the container\n"
            + " * @return the child component\n"
            + "*/";

    public JComponent child(int aIndex);

    public static final String CHILDREN_JSDOC = ""
            + "/**\n"
            + " * Gets the container's children components.\n"
            + " */";

    @ScriptFunction(jsDoc = CHILDREN_JSDOC)
    public JComponent[] children();

    public static final String REMOVE_JSDOC = ""
            + "/**\n"
            + " * Removes the specified component from this container.\n"
            + " * @param component the component to remove\n"
            + " */";

    @ScriptFunction(jsDoc = REMOVE_JSDOC, params = {"component"})
    public void remove(JComponent aComp);

    public static final String CLEAR_JSDOC = ""
            + "/**\n"
            + " * Removes all the components from this container.\n"
            + " */";

    @ScriptFunction(jsDoc = CLEAR_JSDOC)
    public void clear();

    public static final String COUNT_JSDOC = ""
            + "/**\n"
            + " * Gets the number of components in this panel.\n"
            + " */";

    @ScriptFunction(jsDoc = COUNT_JSDOC)
    public int getCount();
}
