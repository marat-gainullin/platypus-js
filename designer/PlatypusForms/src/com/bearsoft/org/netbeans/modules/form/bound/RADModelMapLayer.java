/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.bound;

import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADProperty;
import com.bearsoft.org.netbeans.modules.form.editors.EnumEditor;
import com.eas.client.geo.PointSymbol;
import com.eas.client.geo.RowsetFeatureDescriptor;
import java.beans.PropertyDescriptor;

/**
 *
 * @author mg
 */
public class RADModelMapLayer extends RADComponent<RowsetFeatureDescriptor> {

    @Override
    public void setStoredName(String name) {
        super.setStoredName(name);
        if (getBeanInstance() != null) {
            getBeanInstance().setTypeName(name);
        }
    }

    @Override
    protected void setBeanInstance(RowsetFeatureDescriptor aBeanInstance) {
        super.setBeanInstance(aBeanInstance);
        if (getBeanInstance() != null) {
            getBeanInstance().setTypeName(getName());
        }
    }

    @Override
    protected RADProperty<?> createBeanProperty(PropertyDescriptor desc, Object[] propAccessClsf, Object[] propParentChildDepClsf) {
        if (desc != null && "pointSymbolName".equals(desc.getName())) {
            desc.setValue(EnumEditor.ENUMERATION_VALUES_KEY, new Object[]{
                        PointSymbol.CIRCLE.getName(), PointSymbol.CIRCLE.name(), PointSymbol.CIRCLE.name(),
                        PointSymbol.CROSS.getName(), PointSymbol.CROSS.name(), PointSymbol.CROSS.name(),
                        PointSymbol.SQUARE.getName(), PointSymbol.SQUARE.name(), PointSymbol.SQUARE.name(),
                        PointSymbol.STAR.getName(), PointSymbol.STAR.name(), PointSymbol.STAR.name(),
                        PointSymbol.TRIANGLE.getName(), PointSymbol.TRIANGLE.name(), PointSymbol.TRIANGLE.name(),
                        PointSymbol.X.getName(), PointSymbol.X.name(), PointSymbol.X.name()
                    });
        }
        if (desc != null && "geometryBinding".equals(desc.getName())) {
            desc.setValue(EnumEditor.ENUMERATION_VALUES_KEY, new Object[]{
                        com.vividsolutions.jts.geom.Polygon.class.getSimpleName(), com.vividsolutions.jts.geom.Polygon.class.getName(), com.vividsolutions.jts.geom.Polygon.class.getName(),
                        com.vividsolutions.jts.geom.Point.class.getSimpleName(), com.vividsolutions.jts.geom.Point.class.getName(), com.vividsolutions.jts.geom.Point.class.getName(),
                        com.vividsolutions.jts.geom.MultiPoint.class.getSimpleName(), com.vividsolutions.jts.geom.MultiPoint.class.getName(), com.vividsolutions.jts.geom.MultiPoint.class.getName(),
                        com.vividsolutions.jts.geom.MultiPolygon.class.getSimpleName(), com.vividsolutions.jts.geom.MultiPolygon.class.getName(), com.vividsolutions.jts.geom.MultiPolygon.class.getName(),
                        com.vividsolutions.jts.geom.LineString.class.getSimpleName(), com.vividsolutions.jts.geom.LineString.class.getName(), com.vividsolutions.jts.geom.LineString.class.getName(),
                        com.vividsolutions.jts.geom.MultiLineString.class.getSimpleName(), com.vividsolutions.jts.geom.MultiLineString.class.getName(), com.vividsolutions.jts.geom.MultiLineString.class.getName()
                    });
        }
        return super.createBeanProperty(desc, propAccessClsf, propParentChildDepClsf);
    }
}
