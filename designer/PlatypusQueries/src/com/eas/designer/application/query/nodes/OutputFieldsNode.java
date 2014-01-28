/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.eas.client.model.gui.IconCache;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import java.awt.Image;
import java.io.IOException;
import org.openide.nodes.AbstractNode;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author vv
 */
public class OutputFieldsNode extends AbstractNode {
    
    public OutputFieldsNode(PlatypusQueryDataObject aDataObject) {
        super(new OutputFieldsNodeChildren(aDataObject));
    }

    @Override
    public void destroy() throws IOException {
        ((OutputFieldsNodeChildren)getChildren()).removeNotify();
        super.destroy();
    }
    
    @Override
    public Image getIcon(int type) {
        return ImageUtilities.icon2Image(IconCache.getIcon("edit-list-out.png")); //NOI18N
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
    
    @Override
    public String getName() {
        return NbBundle.getMessage(OutputFieldsNode.class, "MSG_OutputFieldsNodeName");//NOI18N
    }
    
}
