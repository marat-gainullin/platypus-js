/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.changes;

/**
 *
 * @author mg
 */
public class ChangesTags {
    
    public static final int CHANGE_TYPE_INSERT = 1;
    public static final int CHANGE_TYPE_DELETE = 2;
    public static final int CHANGE_TYPE_UPDATE = 3;
    public static final int CHANGE_TYPE_COMMAND = 4;
    //
    public static final int CHANGES_TAG = 1;// Root (1 level)
    public static final int CHANGE_TAG = 1;// Data elemen1 (2 level)
    // 3 level
    public static final int CHANGE_TYPE_TAG = 1;// single element
    public static final int CHANGE_ENTITY_TAG = 2;// single element
    public static final int CHANGE_VALUE_TAG = 3;// multiple elements
    public static final int CHANGE_KEY_TAG = 4;// multiple elements
    public static final int CHANGE_PARAMETER_TAG = 5;// parameter element
    // 4 level
    public static final int VALUE_NAME_TAG = 1;
    public static final int TYPE_ID_TAG = 2;
    public static final int TYPE_NAME_TAG = 3;
    public static final int TYPE_CLASS_NAME_TAG = 4;
    public static final int VALUE_TAG = 5;// ordinary value
    public static final int CUSTOM_VALUE_TAG = 6;// complicated value, such as Jts geometry 
}
