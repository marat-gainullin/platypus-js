/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jdk.nashorn.api.scripting;

/**
 *
 * @author mg
 */
public class ScriptObjectMirrorAccessor {

    public static Object getScriptObject(ScriptObjectMirror aMirror) {
        return aMirror.getScriptObject();
    }
}
