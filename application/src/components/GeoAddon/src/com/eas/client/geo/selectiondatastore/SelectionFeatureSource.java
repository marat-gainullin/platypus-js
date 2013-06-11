/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo.selectiondatastore;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.geotools.data.AbstractFeatureSource;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.data.Query;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.Filter;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 *
 * @author mg
 */
public class SelectionFeatureSource extends AbstractFeatureSource {

    protected final Set<FeatureListener> listeners = new HashSet<>();
    protected DataStore dataStore;
    protected SimpleFeatureType featureType;

    public SelectionFeatureSource(DataStore aDataStore, SimpleFeatureType aFeatureType) {
        super();
        dataStore = aDataStore;
        featureType = aFeatureType;
    }

    @Override
    public DataStore getDataStore() {
        return dataStore;
    }

    @Override
    public void addFeatureListener(FeatureListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeFeatureListener(FeatureListener listener) {
        listeners.add(listener);
    }

    @Override
    public SimpleFeatureType getSchema() {
        return featureType;
    }

    public void fireFeaturesChanged() {
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
    public ReferencedEnvelope getBounds(Query query) throws IOException {
        final ReferencedEnvelope bounds = new ReferencedEnvelope();
        final FeatureCollection<SimpleFeatureType, SimpleFeature> features = getFeatures(query);
        for (FeatureIterator<SimpleFeature> it = features.features(); it.hasNext();) {
            final SimpleFeature feature = it.next();
            bounds.include(feature.getBounds());
        }
        return bounds;
    }
}
