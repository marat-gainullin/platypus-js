/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo.selectiondatastore;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import org.geotools.data.FeatureReader;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 *
 * @author mg
 */
public class SelectionFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature> {

    protected boolean closed;
    protected int listIndex = 0;
    protected SimpleFeatureType featureType;
    protected List<SelectionEntry> selection;
    private SimpleFeatureBuilder featureBuilder;

    public SelectionFeatureReader(SimpleFeatureType aFeatureType, List<SelectionEntry> aSelection) {
        super();
        featureType = aFeatureType;
        featureBuilder = new SimpleFeatureBuilder(featureType);
        selection = aSelection;
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    @Override
    public SimpleFeature next() throws IOException, IllegalArgumentException, NoSuchElementException {
        if (closed) {
            throw new IOException("Reader is closed!");
        }
        if (listIndex >= selection.size()) {
            throw new IOException("Illegal call of next while has no next element");
        }

//        SelectionEntry.VIEW_SHAPE_ATTR_NAME, SelectionEntry.GEOMETRY_BINDING_CLASS);
//        SelectionEntry.ENTITY_ID_ATTR_NAME, Long.class);
//        SelectionEntry.GEOM_COL_INDEX_ATTR_NAME, Integer.class);
//        SelectionEntry.MULTI_GEOMETRY_INDEX_ATTR_NAME, Integer.class);
//        SelectionEntry.COORDINATE_INDEX_ATTR_NAME, Integer.class);
//        SelectionEntry.THIS_ENTRY_ATTR_NAME, SelectionEntry.class);

        SelectionEntry sEntry = selection.get(listIndex++);
        featureBuilder.set(0, sEntry.getViewShape());
        featureBuilder.set(1, sEntry.getEntity());
        featureBuilder.set(2, sEntry.getGeometryColIndex());
        featureBuilder.set(3, sEntry.getGeometryOfInterestIndex());
        featureBuilder.set(4, sEntry.getCoordinateOfInterestIndex());
        featureBuilder.set(5, sEntry);
        SimpleFeature res = featureBuilder.buildFeature(null);
        return res;
    }

    @Override
    public boolean hasNext() throws IOException {
        return listIndex < selection.size();
    }

    @Override
    public void close() throws IOException {
        closed = true;
    }
}
