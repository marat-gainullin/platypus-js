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
import com.eas.store.SerialMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.InvalidParameterValueException;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.NoSuchIdentifierException;
import org.opengis.referencing.operation.MathTransformFactory;

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
    public static final String PROP_PROJECTION_NAME = "projectionName";
    public static final String PROP_PROJECTION_PARAMETERS = "projectionParameters";
    public static final String PROP_MAP_EVENT_LISTENER = "mapEventListener";
    public static final String PROP_MAP_GEO_CRS_WKT = "geoCrsWkt";
    private List<RowsetFeatureDescriptor> features = new ArrayList<>();
    private String mapTitle;
    private ModelElementRef zoomFactorFieldRef;
    private String geoCrsWkt;
    private String projectionName;
    private ParameterValueGroup projectionParameters;
    private Map<String, String> projectionParametersMap;
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
            if (aInfo.zoomFactorFieldRef != zoomFactorFieldRef) {
                setZoomFactorFieldRef(aInfo.zoomFactorFieldRef.copy());
            }
            if (aInfo.geoCrsWkt == null ? geoCrsWkt != null : !aInfo.geoCrsWkt.equals(geoCrsWkt)) {
                setGeoCrsWkt(aInfo.geoCrsWkt);
            }
            if (aInfo.projectionName == null ? projectionName != null : !aInfo.projectionName.equals(projectionName)) {
                setProjectionName(aInfo.projectionName);
            }
            if (aInfo.projectionParameters != projectionParameters) {
                setProjectionParameters(aInfo.projectionParameters.clone());
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
    public String getGeoCrsWkt() {
        return geoCrsWkt;
    }

    @Serial
    public void setGeoCrsWkt(String aValue) {
        String old = geoCrsWkt;
        geoCrsWkt = aValue;
        firePropertyChange(PROP_MAP_GEO_CRS_WKT, old, aValue);
    }

    @Serial
    public String getProjectionName() {
        return projectionName;
    }

    @Serial
    public void setProjectionName(String aValue) {
        if (projectionName == null && aValue != null || projectionName != null && !projectionName.equals(aValue)) {
            final String old = projectionName;
            projectionName = aValue;
            firePropertyChange(PROP_PROJECTION_NAME, old, aValue);
            loadParameters(); // this will work only when deserializing.
        }
    }

    public ParameterValueGroup getProjectionParameters() {
        return projectionParameters;
    }

    public void setProjectionParameters(ParameterValueGroup aValue) {
        if (projectionParameters == null && aValue != null || projectionParameters != null && !projectionParameters.equals(aValue)) {
            final ParameterValueGroup old = projectionParameters;
            projectionParameters = aValue;
            firePropertyChange(PROP_PROJECTION_PARAMETERS, old, aValue);
        }
    }

    /**
     * Returns projection parameters as Map.
     *
     * <p>Use it only for serialization.</p>
     *
     * @return
     */
    @SerialMap(deserializeAs = HashMap.class, elementTagName = "projectionParameter", elementType = Object.class, keyType = String.class)
    public Map<String, String> getProjectionParametersMap() {
        if (projectionParameters == null) {
            return null;
        }
        final Map<String, String> map = new HashMap<>();
        for (GeneralParameterValue gpv : projectionParameters.values()) {
            if (gpv instanceof ParameterValue<?>) {
                final ParameterValue<?> value = (ParameterValue<?>) gpv;
                map.put(gpv.getDescriptor().getName().getCode(), value.getValue() == null ? null : value.getValue().toString());
            }
        }
        return map;
    }

    /**
     * Set map ParameterName:ParameterValue for projection parameters.
     *
     * <p>This method should only be used for deserialization.</p>
     *
     * @param aValue
     */
    @SerialMap(deserializeAs = HashMap.class, elementTagName = "projectionParameter", elementType = String.class, keyType = String.class)
    public void setProjectionParametersMap(Map<String, String> aValue) {
        projectionParametersMap = aValue;
        loadParameters();
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
    
    private void loadParameters() {
        if (projectionParametersMap != null && projectionName != null && !projectionName.isEmpty()) {
            try {
                final MathTransformFactory mtFactory = ReferencingFactoryFinder.getMathTransformFactory(null);
                final ParameterValueGroup parameters = mtFactory.getDefaultParameters(projectionName);
                for (Entry<String, String> entry : projectionParametersMap.entrySet()) {
                    try {
                        final ParameterValue<?> parameter = parameters.parameter(entry.getKey());
                        if (entry.getValue() == null) {
                            parameter.setValue(entry.getValue());
                        } else {
                            final Class<?> valueClass = parameter.getDescriptor().getValueClass();
                            if (Double.class.equals(valueClass)) {
                                parameter.setValue(Double.parseDouble(entry.getValue()));
                            } else if (Float.class.equals(valueClass)) {
                                parameter.setValue(Float.parseFloat(entry.getValue()));
                            } else if (Long.class.equals(valueClass)) {
                                parameter.setValue(Long.parseLong(entry.getValue()));
                            } else if (Integer.class.equals(valueClass)) {
                                parameter.setValue(Integer.parseInt(entry.getValue()));
                            } else if (Short.class.equals(valueClass)) {
                                parameter.setValue(Short.parseShort(entry.getValue()));
                            } else if (Byte.class.equals(valueClass)) {
                                parameter.setValue(Byte.parseByte(entry.getValue()));
                            } else if (Boolean.class.equals(valueClass)) {
                                parameter.setValue(Boolean.parseBoolean(entry.getValue()));
                            } else {
                                parameter.setValue(entry.getValue());
                            }
                        }
                    } catch (ParameterNotFoundException | InvalidParameterValueException ex) {
                        Logger.getLogger(DbMapDesignInfo.class.getName()).log(Level.SEVERE, String.format("Setting value for parameter %s, projection %s", entry.getKey(), projectionName), ex);
                    }
                }
                projectionParametersMap = null;
                projectionParameters = parameters;
            } catch (NoSuchIdentifierException ex) {
                Logger.getLogger(DbMapDesignInfo.class.getName()).log(Level.SEVERE, String.format("Loading default parameters for %s", projectionName), ex);
            }
        }
    }

    @Override
    protected void accept(DbControlsDesignInfoVisitor aVisitor) {
        aVisitor.visit(this);
    }
}
