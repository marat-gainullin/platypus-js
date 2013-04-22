/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.chart;

import com.bearsoft.rowset.events.RowChangeEvent;
import com.bearsoft.rowset.events.RowsetDeleteEvent;
import com.bearsoft.rowset.events.RowsetFilterEvent;
import com.bearsoft.rowset.events.RowsetInsertEvent;
import com.bearsoft.rowset.events.RowsetListener;
import com.bearsoft.rowset.events.RowsetNextPageEvent;
import com.bearsoft.rowset.events.RowsetRequeryEvent;
import com.bearsoft.rowset.events.RowsetRollbackEvent;
import com.bearsoft.rowset.events.RowsetSaveEvent;
import com.bearsoft.rowset.events.RowsetScrollEvent;
import com.bearsoft.rowset.events.RowsetSortEvent;
import com.eas.client.forms.api.Component;
import com.eas.client.model.script.ScriptableRowset;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

/**
 *
 * @author kl
 */
public abstract class AbstractChart extends Component<ChartPanel> implements RowsetListener {

    public static Color[] seriesColors = new Color[]{new Color(194,0,36), new Color(240,165,10), new Color(32, 68, 186)};
        
    protected ScriptableRowset<?> sRowset;
    protected JFreeChart chart;
    protected ChartPanel chartPanel;

    public AbstractChart() {
        super();
        createChartDataset();
        createChartPanel();
        setDelegate(chartPanel);
    }

    public void setDataset(ScriptableRowset<?> aRowset) {
        try {
            if (sRowset != null) {
                sRowset.unwrap().removeRowsetListener(this);
            }
            sRowset = aRowset;
            if (sRowset != null) {
                sRowset.unwrap().addRowsetListener(this);
            }
        } catch (Exception ex) {
            Logger.getLogger(AbstractChart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected abstract void createChartDataset();

    protected abstract void createChartPanel();

    protected void fireDataChanged() {
        delegate.invalidate();
        delegate.repaint();
    }

    @Override
    public boolean willScroll(RowsetScrollEvent rse) {
        return true;
    }

    @Override
    public boolean willFilter(RowsetFilterEvent rfe) {
        return true;
    }

    @Override
    public boolean willRequery(RowsetRequeryEvent rre) {
        return true;
    }

    @Override
    public boolean willNextPageFetch(RowsetNextPageEvent rnpe) {
        return true;
    }

    @Override
    public boolean willInsertRow(RowsetInsertEvent rie) {
        return true;
    }

    @Override
    public boolean willChangeRow(RowChangeEvent rce) {
        return true;
    }

    @Override
    public boolean willDeleteRow(RowsetDeleteEvent rde) {
        return true;
    }

    @Override
    public boolean willSort(RowsetSortEvent rse) {
        return true;
    }

    @Override
    public void rowsetFiltered(RowsetFilterEvent rfe) {
        fireDataChanged();
    }

    @Override
    public void rowsetRequeried(RowsetRequeryEvent rre) {
        fireDataChanged();
    }

    @Override
    public void rowsetNextPageFetched(RowsetNextPageEvent rnpe) {
        fireDataChanged();
    }

    @Override
    public void rowsetSaved(RowsetSaveEvent rse) {
    }

    @Override
    public void rowsetRolledback(RowsetRollbackEvent rre) {
        fireDataChanged();
    }

    @Override
    public void rowsetScrolled(RowsetScrollEvent rse) {
    }

    @Override
    public void rowsetSorted(RowsetSortEvent rse) {
        fireDataChanged();
    }

    @Override
    public void rowInserted(RowsetInsertEvent rie) {
        fireDataChanged();
    }

    @Override
    public void rowChanged(RowChangeEvent rce) {
        fireDataChanged();
    }

    @Override
    public void rowDeleted(RowsetDeleteEvent rde) {
        fireDataChanged();
    }
}
