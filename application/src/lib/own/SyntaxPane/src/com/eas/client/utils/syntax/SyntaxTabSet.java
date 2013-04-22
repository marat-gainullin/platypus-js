/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.client.utils.syntax;

import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

/**
 *
 * @author Marat
 */
class SyntaxTabSet extends TabSet{
    private int tabPixelSize = 44;

    public SyntaxTabSet() {
        super(new TabStop[]{new TabStop(0)});
    }
    
    public void setTabPixelSize(int atabPixelSize)
    {
        tabPixelSize = atabPixelSize;
    }
    
    @Override
    public TabStop getTabAfter(float location) {
        return new TabStop(location+tabPixelSize);
    }
}
