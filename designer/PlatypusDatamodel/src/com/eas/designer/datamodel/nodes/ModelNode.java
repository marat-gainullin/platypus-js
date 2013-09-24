/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.datamodel.nodes;

import com.eas.client.DbClient;
import com.eas.client.model.Entity;
import com.eas.client.model.Model;
import com.eas.client.model.gui.IconCache;
import java.awt.Image;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.openide.loaders.DataObject;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 * @author mg
 */
public class ModelNode<E extends Entity<?, ?, E>, M extends Model<E, ?, DbClient, ?>> extends AbstractNode {

    protected static final String DATAMODEL_ICON_NAME = "datamodel16.png"; //NOI18N
    protected DataObject dataObject;

    public ModelNode(ModelNodeChildren<E, M> aChildren, DataObject aDataObject) {
        super(aChildren);
        dataObject = aDataObject;
    }

    public boolean isVisibleRoot() {
        return true;
    }

    public DataObject getDataObject() {
        return dataObject;
    }

    @Override
    public String getName() {
        return dataObject.getName();
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.icon2Image(IconCache.getIcon(DATAMODEL_ICON_NAME));
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    public Node[] entitiesToNodes(Set<E> entities) {
        Set<Node> convertedNodes = new HashSet<>();
        if (entities != null) {
            Map<E, Node> entitiesToNodes = new HashMap<>();
            for (Node node : getChildren().getNodes()) {
                if (node instanceof EntityNode) {
                    entitiesToNodes.put(((EntityNode<E>) node).getEntity(), node);
                }
            }
            for (E entity : entities) {
                convertedNodes.add(entitiesToNodes.get(entity));
            }
        }
        return convertedNodes.toArray(new Node[0]);
    }

    public Node entitiyToNode(E entity) {
        Set<E> entities = new HashSet<>();
        entities.add(entity);
        Node[] res = entitiesToNodes(entities);
        if (res != null && res.length == 1) {
            return res[0];
        } else {
            return null;
        }
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = new Sheet();
        Sheet.Set pSet = Sheet.createPropertiesSet();
        sheet.put(pSet);
        PropertySupport.Name nameProp = new PropertySupport.Name(this, PROP_NAME, "");
        pSet.put(nameProp);
        return sheet;
    }
}
