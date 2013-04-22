/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.geo.selectiondatastore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.geotools.data.AbstractDataStore;
import org.geotools.data.FeatureReader;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author mg
 */
public class SelectionDataStore extends AbstractDataStore {

    public static final String SELECTION_FEATURE_TYPE_NAME = "selected features points";
    public static final String SELECTION_FEATURE_PHANTOM_TYPE_NAME = "selected features phantoms";
    public static final String BAD_NAME_MSG_TEMPLATE = "Only \"%s\", \"%s=\" types are allowed";
    protected final SimpleFeatureTypeBuilder simpleFeatureTypeBuilder = new SimpleFeatureTypeBuilder();
    protected static final String[] typesNames = new String[]{SELECTION_FEATURE_TYPE_NAME, SELECTION_FEATURE_PHANTOM_TYPE_NAME};
    protected SimpleFeatureType selectionFeatureType;
    protected SimpleFeatureType selectionFeaturePhantomType;
    protected List<SelectionEntry> selection = new ArrayList<>();
    // selection layer Crs
    protected CoordinateReferenceSystem crs;
    // featureSource is persistent. It's used multiple times.
    protected SelectionFeatureSource featureSource;
    protected SelectionFeatureSource phantomFeatureSource;
    // Feature source is not persistent. It's used only once at a time.

    public SelectionDataStore(CoordinateReferenceSystem aCrs) {
        super();
        crs = aCrs;
        buildSelectionFeatureTypes();
        featureSource = new SelectionFeatureSource(this, selectionFeatureType);
        phantomFeatureSource = new SelectionFeatureSource(this, selectionFeaturePhantomType);
    }

    public void setSelection(List<SelectionEntry> aSelection) {
        selection.clear();
        add2Selection(aSelection);
    }

    public void clear() {
        selection.clear();
        fireSelectionChanged();
    }

    public void add2Selection(List<SelectionEntry> aSelectionEntries) {
        selection.addAll(aSelectionEntries);
        fireSelectionChanged();
    }

    public List<SelectionEntry> getSelection() {
        return selection;
    }

    public boolean isEmpty() {
        return selection.isEmpty();
    }

    public int size() {
        return selection.size();
    }

    public void fireSelectionChanged() {
        featureSource.fireFeaturesChanged();
        phantomFeatureSource.fireFeaturesChanged();
    }

    private void buildSelectionFeatureTypes() {
        if (crs == null) {
            throw new NullPointerException("Crs must present");
        }
        simpleFeatureTypeBuilder.setName(SELECTION_FEATURE_TYPE_NAME);
        simpleFeatureTypeBuilder.setCRS(crs);
        simpleFeatureTypeBuilder.add(SelectionEntry.VIEW_SHAPE_ATTR_NAME, SelectionEntry.SELECTION_ENTRY_GEOMETRY_BINDING_CLASS);
        simpleFeatureTypeBuilder.add(SelectionEntry.ENTITY_ID_ATTR_NAME, Long.class);
        simpleFeatureTypeBuilder.add(SelectionEntry.GEOM_COL_INDEX_ATTR_NAME, Integer.class);
        simpleFeatureTypeBuilder.add(SelectionEntry.MULTI_GEOMETRY_INDEX_ATTR_NAME, Integer.class);
        simpleFeatureTypeBuilder.add(SelectionEntry.COORDINATE_INDEX_ATTR_NAME, Integer.class);
        simpleFeatureTypeBuilder.add(SelectionEntry.THIS_ENTRY_ATTR_NAME, SelectionEntry.class);
        selectionFeatureType = simpleFeatureTypeBuilder.buildFeatureType();

        simpleFeatureTypeBuilder.setName(SELECTION_FEATURE_PHANTOM_TYPE_NAME);
        simpleFeatureTypeBuilder.setCRS(crs);
        simpleFeatureTypeBuilder.add(SelectionEntry.VIEW_SHAPE_ATTR_NAME, SelectionEntry.SELECTION_PHANTOM_GEOMETRY_BINDING_CLASS);
        selectionFeaturePhantomType = simpleFeatureTypeBuilder.buildFeatureType();

    }

    @Override
    public String[] getTypeNames() throws IOException {
        return typesNames;
    }

    @Override
    public SimpleFeatureType getSchema(String aTypeName) throws IOException {
        switch (aTypeName) {
            case SELECTION_FEATURE_TYPE_NAME:
                return selectionFeatureType;
            case SELECTION_FEATURE_PHANTOM_TYPE_NAME:
                return selectionFeaturePhantomType;
            default:
                throw new IOException(String.format(BAD_NAME_MSG_TEMPLATE, SELECTION_FEATURE_TYPE_NAME, SELECTION_FEATURE_PHANTOM_TYPE_NAME));
        }
    }

    @Override
    protected FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader(String aTypeName) throws IOException {
        switch (aTypeName) {
            case SELECTION_FEATURE_TYPE_NAME:
                return new SelectionFeatureReader(selectionFeatureType, selection);
            case SELECTION_FEATURE_PHANTOM_TYPE_NAME:
                return new SelectionPhantomsFeatureReader(selectionFeaturePhantomType, selection);
            default:
                throw new IOException(String.format(BAD_NAME_MSG_TEMPLATE, SELECTION_FEATURE_TYPE_NAME, SELECTION_FEATURE_PHANTOM_TYPE_NAME));
        }
    }

    @Override
    public SimpleFeatureSource getFeatureSource(String aTypeName) throws IOException {
        switch (aTypeName) {
            case SELECTION_FEATURE_TYPE_NAME:
                return featureSource;
            case SELECTION_FEATURE_PHANTOM_TYPE_NAME:
                return phantomFeatureSource;
            default:
                throw new IOException(String.format(BAD_NAME_MSG_TEMPLATE, SELECTION_FEATURE_TYPE_NAME, SELECTION_FEATURE_PHANTOM_TYPE_NAME));
        }
    }
}
