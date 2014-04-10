/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.bound;

import com.bearsoft.org.netbeans.modules.form.ComponentContainer;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADVisualComponent;
import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.dbcontrols.map.DbMap;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mg
 */
public class RADModelMap extends RADVisualComponent<DbMap> implements ComponentContainer {

    public interface ModelMapListener extends ModelControlListener {

        public Object onEvent(Object anEvent);
    }
    protected List<RADModelMapLayer> layers = new ArrayList<>();

    @Override
    public RADComponent<?>[] getSubBeans() {
        return layers.toArray(new RADComponent<?>[]{});
    }

    @Override
    public void initSubComponents(RADComponent<?>[] initComponents) {
        layers.clear();
        getBeanInstance().getFeatures().clear();
        for (int i = 0; i < initComponents.length; i++) {
            if (initComponents[i] instanceof RADModelMapLayer) {
                RADModelMapLayer radLayer = (RADModelMapLayer) initComponents[i];
                radLayer.setParent(this);
                layers.add(radLayer);
                getBeanInstance().getFeatures().add(radLayer.getBeanInstance());
            }
        }
    }

    @Override
    public void reorderSubComponents(int[] perm) {
        RADModelMapLayer[] oldLayers = layers.toArray(new RADModelMapLayer[]{});
        RowsetFeatureDescriptor[] oldRawLayers = getBeanInstance().getFeatures().toArray(new RowsetFeatureDescriptor[]{});
        assert perm.length == oldLayers.length;
        assert perm.length == oldRawLayers.length;
        for (int i = 0; i < layers.size(); i++) {
            layers.set(perm[i], oldLayers[i]);
        }
        for (int i = 0; i < getBeanInstance().getFeatures().size(); i++) {
            getBeanInstance().getFeatures().set(perm[i], oldRawLayers[i]);
        }
    }

    @Override
    public void add(RADComponent<?> comp) {
        if (comp instanceof RADModelMapLayer) {
            RADModelMapLayer radLayer = (RADModelMapLayer) comp;
            layers.add(radLayer);
            if (radLayer.isInModel()) {
                getBeanInstance().getFeatures().add(radLayer.getBeanInstance());
            }
        }
    }

    @Override
    public void remove(RADComponent<?> comp) {
        if (comp instanceof RADModelMapLayer) {
            RADModelMapLayer radLayer = (RADModelMapLayer) comp;
            layers.remove(radLayer);
            getBeanInstance().getFeatures().remove(radLayer.getBeanInstance());
        }
    }

    @Override
    public int getIndexOf(RADComponent<?> comp) {
        return layers.indexOf(comp);
    }
}
