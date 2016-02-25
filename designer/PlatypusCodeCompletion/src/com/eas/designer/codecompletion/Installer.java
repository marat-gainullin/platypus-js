/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.codecompletion;

import java.util.Collections;
import java.util.Set;
import org.netbeans.contrib.yenta.Yenta;

/**
 *
 * @author mg
 */
public class Installer extends Yenta {

    @Override
    protected Set<String> friends() {
        return Collections.singleton("org.netbeans.modules.javascript2.editor");
    }

}
