/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jeta.forms.store.jml;

import com.jeta.forms.store.jml.dom.JMLDocument;
import com.jeta.forms.store.jml.dom.JMLNode;
import java.awt.Dimension;

/**
 *
 * @author Marat
 */
public class DimensionSerializer  implements JMLSerializer {

    public JMLNode serialize( JMLDocument document, Object obj) throws JMLException {
        JMLUtils.verifyObjectType( obj, Dimension.class );
	Dimension dim = (Dimension)obj;
        JMLNode node = JMLUtils.createObjectNode( document, obj );
        if ( dim != null ) {
            node.setAttribute("width", String.valueOf(dim.getWidth()));
            node.setAttribute("height", String.valueOf(dim.getHeight()));
        }
        return node;
    }

}
