/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.map;

import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.client.model.ModelElementRef;
import com.eas.controls.DesignInfo;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlsDesignInfoVisitor;
import com.eas.store.Serial;
import com.eas.store.SerialCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author pk
 */
public class DbMapDesignInfo extends DbControlDesignInfo {

    public static final String MAP_EVENT_HANDLER_FUNCTION_ARGUMENT = "e";
    public static final String MAP_EVENT_HANDLER_FUNCTION_NAME = "onMapEvent";
    public static final String PROP_FEATURES = "features";
    public static final String PROP_MAP_TITLE = "mapTitle";
    public static final String PROP_ZOOM_FACTOR_FIELD_REF = "zoomFactorFieldRef";
    public static final String PROP_MAP_EVENT_LISTENER = "mapEventListener";
    public static final String PROP_MAP_GEO_CRS_WKT = "crsWkt";
    private List<RowsetFeatureDescriptor> features = new ArrayList<>();
    private String mapTitle;
    private ModelElementRef zoomFactorFieldRef;
    private String crsWkt;
    private String backingUrl;
    private String mapEventListener = "";

    @Override
    public void assign(DesignInfo aSource) {
        super.assign(aSource);
        if (aSource != null && aSource instanceof DbMapDesignInfo) {
            DbMapDesignInfo aInfo = (DbMapDesignInfo) aSource;
            if (aInfo.features != features) {
                List<RowsetFeatureDescriptor> newFeatures = new ArrayList<>();
                for (int i = 0; i < aInfo.features.size(); i++) {
                    RowsetFeatureDescriptor fd = new RowsetFeatureDescriptor();
                    fd.assign(aInfo.features.get(i));
                    newFeatures.add(fd);
                }
                setFeatures(newFeatures);
            }
            if (aInfo.mapTitle == null ? mapTitle != null : !aInfo.mapTitle.equals(mapTitle)) {
                setMapTitle(aInfo.mapTitle);
            }
            if (aInfo.backingUrl == null ? backingUrl != null : !aInfo.backingUrl.equals(backingUrl)) {
                setMapTitle(aInfo.backingUrl);
            }
            if (aInfo.zoomFactorFieldRef != zoomFactorFieldRef) {
                setZoomFactorFieldRef(aInfo.zoomFactorFieldRef.copy());
            }
            if (aInfo.crsWkt == null ? crsWkt != null : !aInfo.crsWkt.equals(crsWkt)) {
                setCrsWkt(aInfo.crsWkt);
            }
            if (aInfo.mapEventListener == null ? mapEventListener != null : !aInfo.mapEventListener.equals(mapEventListener)) {
                setMapEventListener(aInfo.mapEventListener);
            }
        }
    }

    public Class<?> getControlClass() {
        return DbMap.class;
    }

    @SerialCollection(elementTagName = "feature", elementType = RowsetFeatureDescriptor.class, deserializeAs = ArrayList.class)
    public List<RowsetFeatureDescriptor> getFeatures() {
        return features;
    }

    @SerialCollection(elementTagName = "feature", elementType = RowsetFeatureDescriptor.class, deserializeAs = ArrayList.class)
    public void setFeatures(List<RowsetFeatureDescriptor> features) {
        if (this.features != features) {
            final List<RowsetFeatureDescriptor> oldValue = this.features;
            this.features = features == null ? Arrays.asList(new RowsetFeatureDescriptor[0]) : features;
            firePropertyChange(PROP_FEATURES, oldValue, this.features);
        }
    }

    @Serial
    public String getMapTitle() {
        return this.mapTitle;
    }

    @Serial
    public void setMapTitle(String aValue) {
        if (mapTitle == null && aValue != null || mapTitle != null && !mapTitle.equals(aValue)) {
            final String old = mapTitle;
            mapTitle = aValue;
            firePropertyChange(PROP_MAP_TITLE, old, aValue);
        }
    }

    @Serial
    public ModelElementRef getZoomFactorFieldRef() {
        return zoomFactorFieldRef;
    }

    @Serial
    public void setZoomFactorFieldRef(ModelElementRef aValue) {
        if (zoomFactorFieldRef == null && aValue != null || zoomFactorFieldRef != null && !zoomFactorFieldRef.equals(aValue)) {
            final ModelElementRef old = zoomFactorFieldRef;
            zoomFactorFieldRef = aValue;
            firePropertyChange(PROP_ZOOM_FACTOR_FIELD_REF, old, zoomFactorFieldRef);
        }
    }

    @Serial
    public String getCrsWkt() {
        return crsWkt;
    }

    @Serial
    public void setCrsWkt(String aValue) {
        String old = crsWkt;
        crsWkt = aValue;
        firePropertyChange(PROP_MAP_GEO_CRS_WKT, old, aValue);
    }
  
    @Serial
    public String getMapEventListener() {
        return mapEventListener;
    }

    @Serial
    public void setMapEventListener(String aValue) {
        if (aValue != null && aValue.isEmpty()) {
            aValue = null;
        }
        if (aValue == null && mapEventListener != null
                || aValue != null && !aValue.equals(mapEventListener)) {
            final String old = mapEventListener;
            mapEventListener = aValue;
            firePropertyChange(PROP_MAP_EVENT_LISTENER, old, aValue);
        }
    }

    public String getOnEvent()
    {
        return mapEventListener;
    }
    
    public void setOnEvent(String aValue)
    {
        mapEventListener = aValue;
    }
 
    @Override
    protected void accept(DbControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
    
    @Serial
    public String getBackingUrl() {
        return backingUrl;
    }
    
    @Serial
    public void setBackingUrl(String aBackingUrl) {
        backingUrl = aBackingUrl;
    }
}
