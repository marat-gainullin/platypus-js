/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo.selectiondatastore;

import com.eas.client.geo.GisUtilities;
import com.vividsolutions.jts.geom.Geometry;
import java.io.IOException;
import java.util.ArrayList;
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
public class SelectionPhantomsFeatureReader implements FeatureReader<SimpleFeatureType, SimpleFeature> {

    protected boolean closed;
    protected int listIndex = 0;
    protected SimpleFeatureType featureType;
    protected List<Geometry> selection;
    private SimpleFeatureBuilder featureBuilder;

    public SelectionPhantomsFeatureReader(SimpleFeatureType aFeatureType, List<SelectionEntry> aSelection) throws IOException {
        super();
        featureType = aFeatureType;
        featureBuilder = new SimpleFeatureBuilder(featureType);
        selection = new ArrayList<>();
        try {
            selection.addAll(GisUtilities.convertSelectionEntries2Geometries(aSelection));
        } catch (Exception ex) {
            throw new IOException(ex);
        }
    }

    public SimpleFeatureType getFeatureType() {
        return featureType;
    }

    public SimpleFeature next() throws IOException, IllegalArgumentException, NoSuchElementException {
        if (closed) {
            throw new IOException("Reader is closed!");
        }
        if (listIndex >= selection.size()) {
            throw new IOException("Illegal call of next while has no next element");
        }

//        SelectionEntry.VIEW_SHAPE_ATTR_NAME, SelectionEntry.GEOMETRY_BINDING_CLASS);

        Geometry gEntry = selection.get(listIndex++);
        featureBuilder.set(0, gEntry);
        SimpleFeature res = featureBuilder.buildFeature(null);
        return res;
    }

    public boolean hasNext() throws IOException {
        return listIndex < selection.size();
    }

    public void close() throws IOException {
        closed = true;
    }
}
