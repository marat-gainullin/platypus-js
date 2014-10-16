/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo.datastore;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.events.RowsetNetErrorEvent;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSaveEvent;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.bearsoft.rowset.events.RowsetSortEvent;
import com.bearsoft.rowset.exceptions.InvalidColIndexException;
import com.eas.client.SQLUtils;
import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.client.model.application.ApplicationEntity;
import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.geotools.data.AbstractFeatureSource;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

/**
 *
 * @author pk
 */
public class RowsFeatureSource extends AbstractFeatureSource implements RowsetListener {

    private final DatamodelDataStore dataStore;
    private final RowsetFeatureDescriptor featureDescriptor;
    protected int typeColIndex = 0;
    protected int labelColIndex = 0;
    private final SimpleFeatureType featureType;
    private final List<FeatureListener> listeners = new ArrayList<>();

    public RowsFeatureSource(DatamodelDataStore aDataStore, SimpleFeatureType aFeatureType, RowsetFeatureDescriptor aFeatureDescriptor) throws Exception {
        super();
        dataStore = aDataStore;
        featureDescriptor = aFeatureDescriptor;
        featureType = aFeatureType;
        bind();
    }

    public RowsFeatureSource(Set<?> hints, DatamodelDataStore aDataStore, SimpleFeatureType aFeatureType, RowsetFeatureDescriptor aFeatureDescriptor) throws Exception {
        super(hints);
        dataStore = aDataStore;
        featureDescriptor = aFeatureDescriptor;
        featureType = aFeatureType;
        bind();
    }

    private void bind() throws Exception {
        final ApplicationEntity<?, ?, ?> entity = featureDescriptor.getEntity();
        if (entity == null) {
            throw new NullPointerException("null entity for descriptor of feature type " + featureDescriptor.getTypeName());
        }
        final Rowset rowset = entity.getRowset();
        if(rowset == null) {
            throw new NullPointerException("null rowset for descriptor of feature type " + featureDescriptor.getTypeName());
        }
        if (featureDescriptor.getTypeRef() != null) {
            typeColIndex = rowset.getFields().find(featureDescriptor.getTypeRef().getFieldName());
        }
        if (featureDescriptor.getStyle().getLabelField() != null) {
            labelColIndex = rowset.getFields().find(featureDescriptor.getStyle().getLabelField().getFieldName());
        }
        rowset.addRowsetListener(this);
    }

    @Override
    public DataStore getDataStore() {
        return dataStore;
    }

    @Override
    public ReferencedEnvelope getBounds(Query query) throws IOException {
        final ReferencedEnvelope bounds = new ReferencedEnvelope();
        final FeatureCollection<SimpleFeatureType, SimpleFeature> features = getFeatures(query);
        for (FeatureIterator<SimpleFeature> it = features.features(); it.hasNext();) {
            final SimpleFeature feature = it.next();
            bounds.include(feature.getBounds());
        }
        return bounds;
    }

    @Override
    public void addFeatureListener(FeatureListener fl) {
        synchronized (listeners) {
            listeners.add(fl);
        }
    }

    @Override
    public void removeFeatureListener(FeatureListener fl) {
        synchronized (listeners) {
            listeners.remove(fl);
        }
    }

    @Override
    public SimpleFeatureType getSchema() {
        return featureType;
    }

    @Override
    public boolean willScroll(RowsetScrollEvent event) {
        return true;
    }

    @Override
    public boolean willFilter(RowsetFilterEvent event) {
        return true;
    }

    @Override
    public boolean willRequery(RowsetRequeryEvent event) {
        return true;
    }

    @Override
    public boolean willNextPageFetch(RowsetNextPageEvent event) {
        return true;
    }

    @Override
    public boolean willInsertRow(RowsetInsertEvent event) {
        return true;
    }

    @Override
    public boolean willChangeRow(RowChangeEvent event) {
        return true;
    }

    @Override
    public boolean willDeleteRow(RowsetDeleteEvent event) {
        return true;
    }

    @Override
    public boolean willSort(RowsetSortEvent event) {
        return true;
    }

    @Override
    public void rowsetFiltered(RowsetFilterEvent event) {
        fireFeaturesChanged();
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent event) {
        fireFeaturesChanged();
    }

    private boolean checkRowTypeIsCompatible(Row aRow) {
        boolean needChanges;
        if (typeColIndex > 0) {
            try {
                Integer formalType = featureDescriptor.getTypeValue();
                Object oActualType = aRow.getColumnObject(typeColIndex);
                assert oActualType == null || oActualType instanceof Number : "Layer type must bind to numeric field";
                BigDecimal bdFormalType = null;
                if (formalType != null) {
                    bdFormalType = SQLUtils.number2BigDecimal(formalType);
                }
                BigDecimal bdActualType = null;
                if (oActualType != null) {
                    bdActualType = SQLUtils.number2BigDecimal((Number) oActualType);
                }
                if (bdFormalType == null && bdActualType == null || (bdFormalType != null && bdFormalType.equals(bdActualType))) {
                    needChanges = true;
                } else {
                    needChanges = false;
                }
            } catch (InvalidColIndexException ex) {
                Logger.getLogger(RowsFeatureSource.class.getName()).log(Level.SEVERE, null, ex);
                needChanges = false;
            }
        } else {
            needChanges = true;
        }
        return needChanges;
    }

    public void fireFeaturesChanged() {
        dataStore.clearTypeInfoCache(featureDescriptor.getTypeName());
        FeatureListener[] ls;
        synchronized (listeners) {
            ls = new FeatureListener[listeners.size()];
            ls = listeners.toArray(ls);
        }
        FeatureEvent e = new FeatureEvent(this, FeatureEvent.Type.CHANGED, (ReferencedEnvelope) null, (Filter) null);
        for (FeatureListener l : ls) {
            l.changed(e);
        }
    }

    @Override
    public void beforeRequery(RowsetRequeryEvent rre) {
    }

    @Override
    public void rowsetNetError(RowsetNetErrorEvent rnee) {
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent event) {
        fireFeaturesChanged();
    }

    @Override
    public void rowsetSaved(RowsetSaveEvent event) {
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent event) {
        fireFeaturesChanged();
    }

    @Override
    public void rowsetScrolled(RowsetScrollEvent event) {
    }

    @Override
    public void rowsetSorted(RowsetSortEvent event) {
    }

    @Override
    public void rowInserted(RowsetInsertEvent event) {
        if (checkRowTypeIsCompatible(event.getRow())) {
            fireFeaturesChanged();
        }
    }

    @Override
    public void rowChanged(RowChangeEvent event) {
        if (typeColIndex > 0) {
            // check if type field is changing
            if (event.getFieldIndex() == typeColIndex) {
                if (SQLUtils.smartEquals(event.getNewValue(), featureDescriptor.getTypeValue()) || SQLUtils.smartEquals(event.getOldValue(), featureDescriptor.getTypeValue())) {
                    fireFeaturesChanged();
                }
            } else if (checkRowTypeIsCompatible(event.getRowset().getCurrentRow())
                    && (event.getNewValue() instanceof Geometry
                    || event.getOldValue() instanceof Geometry
                    || event.getFieldIndex() == labelColIndex)) {
                fireFeaturesChanged();
            }
        } else {
            if (event.getFieldIndex() == labelColIndex || event.getNewValue() instanceof Geometry || event.getOldValue() instanceof Geometry) {
                fireFeaturesChanged();
            }
        }
    }

    @Override
    public void rowDeleted(RowsetDeleteEvent event) {
        if (checkRowTypeIsCompatible(event.getRow())) {
            fireFeaturesChanged();
        }
    }
}
