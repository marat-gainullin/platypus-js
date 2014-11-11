/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public interface HasPublished {

    JSObject getPublished();

    void setPublished(JSObject aPublished);
}
