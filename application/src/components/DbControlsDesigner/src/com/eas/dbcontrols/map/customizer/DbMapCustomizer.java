/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DbMapCustomizer.java
 *
 * Created on Jan 7, 2010, 4:43:40 PM
 */
package com.eas.dbcontrols.map.customizer;

import com.eas.client.geo.FeatureStyleDescriptor;
import com.eas.client.geo.PointSymbol;
import com.eas.client.geo.RowsetFeatureDescriptor;
import com.eas.dbcontrols.DbControl;
import com.eas.dbcontrols.DbControlCustomizer;
import com.eas.dbcontrols.DbControlsDesignUtils;
import com.eas.dbcontrols.DesignIconCache;
import com.eas.dbcontrols.FieldRefRenderer;
import com.eas.dbcontrols.ScriptEvents;
import com.eas.dbcontrols.edits.ModifyMapEventEdit;
import com.eas.dbcontrols.map.DbMap;
import com.eas.dbcontrols.map.DbMapDesignInfo;
import com.eas.dbcontrols.map.customizer.actions.AddDataSourceAction;
import com.eas.dbcontrols.map.customizer.actions.ClearMapEventAction;
import com.eas.dbcontrols.map.customizer.actions.ClearProjectionAction;
import com.eas.dbcontrols.map.customizer.actions.ClearStyleFieldAction;
import com.eas.dbcontrols.map.customizer.actions.ClearTypeFieldAction;
import com.eas.dbcontrols.map.customizer.actions.ClearZoomFactorFieldAction;
import com.eas.dbcontrols.map.customizer.actions.CopyDataSourceAction;
import com.eas.dbcontrols.map.customizer.actions.CutDataSourceAction;
import com.eas.dbcontrols.map.customizer.actions.GeoCrsWktChangeAction;
import com.eas.dbcontrols.map.customizer.actions.ModifyMapEventListenerAction;
import com.eas.dbcontrols.map.customizer.actions.MoveDownDataSourceAction;
import com.eas.dbcontrols.map.customizer.actions.MoveUpDataSourceAction;
import com.eas.dbcontrols.map.customizer.actions.PasteDataSourceAction;
import com.eas.dbcontrols.map.customizer.actions.RemoveDataSourceAction;
import com.eas.dbcontrols.map.customizer.actions.SelectFillColorAction;
import com.eas.dbcontrols.map.customizer.actions.SelectHaloColorAction;
import com.eas.dbcontrols.map.customizer.actions.SelectLabelFieldAction;
import com.eas.dbcontrols.map.customizer.actions.SelectLabelFontAction;
import com.eas.dbcontrols.map.customizer.actions.SelectLineColorAction;
import com.eas.dbcontrols.map.customizer.actions.SelectTypeFieldAction;
import com.eas.dbcontrols.map.customizer.actions.SelectZoomFactorFieldAction;
import com.eas.dbcontrols.map.customizer.edits.ModifyActivenessEdit;
import com.eas.dbcontrols.map.customizer.edits.ModifyBindingEdit;
import com.eas.dbcontrols.map.customizer.edits.ModifyCRSEdit;
import com.eas.dbcontrols.map.customizer.edits.ModifySelectableEdit;
import com.eas.dbcontrols.map.customizer.edits.ModifyStyleEdit;
import com.eas.dbcontrols.map.customizer.edits.ModifyTypeNameEdit;
import com.eas.dbcontrols.map.customizer.edits.ModifyTypeValueEdit;
import com.eas.util.edits.ModifyBeanPropertyEdit;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.undo.CompoundEdit;
import org.geotools.parameter.Parameter;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.ParameterNotFoundException;
import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchIdentifierException;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.OperationMethod;

/**
 *
 * @author pk
 */
public class DbMapCustomizer extends DbControlCustomizer {

    private final FeaturesListModel featuresListModel;
    private final ListCellRenderer featureRenderer = new FeatureDataSourceCellRenderer();
    private ScriptFunctionsComboBoxModel mapEventListenersModel = new ScriptFunctionsComboBoxModel();
    private final ListCellRenderer projectionTypeRenderer = new OperationMethodRenderer();
    private final FeatureDataSourceTransferHandler featuresTransferHandler;
    private final FieldRefRenderer labelFieldRenderer = new FieldRefRenderer(),
            zoomFactorFieldRenderer = new FieldRefRenderer(),
            typeFieldRenderer = new FieldRefRenderer();
    private final float DEFAULT_SIZE_VALUE = 1f;
    private final int DEFAULT_OPACITY_VALUE = 100;
    private final MathTransformFactory mtFactory;
    private final MapProjectionsListModel projectionsModel;
    private final ProjectionParametersTableModel projectionParametersModel;
//    private final ScriptGeneratorListener mapEventListenerGenerator;
    protected RowsetFeatureDescriptor selectedDescriptor;
    protected RowsetFeatureDescriptorChangeListener selectedDescriptorListener = new RowsetFeatureDescriptorChangeListener();
    protected ModifyMapEventListenerAction modifyMapEventAction;
    protected ClearMapEventAction clearMapEventAction;

    /** Creates new form DbMapCustomizer
     * @throws ClassNotFoundException
     */
    public DbMapCustomizer() throws ClassNotFoundException {
        mtFactory = ReferencingFactoryFinder.getMathTransformFactory(null);
        featuresListModel = new FeaturesListModel();
        projectionsModel = new MapProjectionsListModel(mtFactory);
        projectionParametersModel = new ProjectionParametersTableModel(undoSupport);
        featuresTransferHandler = new FeatureDataSourceTransferHandler(this);
        fillActionMap();
        initComponents();
        lstFeatures.setCellRenderer(featureRenderer);
        lstFeatures.getSelectionModel().addListSelectionListener(new FeaturesListSelectionListener());
        lstFeatures.setTransferHandler(featuresTransferHandler);
        lstFeatures.setDragEnabled(true);
        lstFeatures.setDropMode(DropMode.INSERT);
        installShortcut(lstFeatures, KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK), CutDataSourceAction.class);
        installShortcut(lstFeatures, KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), CopyDataSourceAction.class);
        installShortcut(lstFeatures, KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), PasteDataSourceAction.class);
        installShortcut(lstFeatures, KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.CTRL_DOWN_MASK), MoveUpDataSourceAction.class);
        installShortcut(lstFeatures, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.CTRL_DOWN_MASK), MoveDownDataSourceAction.class);
        installShortcut(lstFeatures, KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), AddDataSourceAction.class);
        installShortcut(lstFeatures, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), RemoveDataSourceAction.class);

        cbGeometry.setModel(new GeometryBindingComboModel());
        cbGeometry.setRenderer(new GeometryBindingCellRenderer());
        final ImageIcon clearIcon = DesignIconCache.getIcon("16x16/delete.png");
        btnClearLabelFont.setIcon(clearIcon);
        btnClearLabelField.setIcon(clearIcon);
        btnClearTypeField.setIcon(clearIcon);
        btnClearSize.setIcon(clearIcon);
        btnClearLineColor.setIcon(clearIcon);
        btnClearFillColor.setIcon(clearIcon);
        btnClearOpacity.setIcon(clearIcon);
        btnClearZoomFactorField.setIcon(clearIcon);
        btnClearProjection.setIcon(clearIcon);
        btnClearMapEventListener.setIcon(clearIcon);
        btnClearHaloColor.setIcon(clearIcon);
        final ImageIcon selectIcon = DesignIconCache.getIcon("16x16/ellipsis.gif");
        btnSelectLabelFont.setIcon(selectIcon);
        btnSelectLabelField.setIcon(selectIcon);
        btnSelectTypeField.setIcon(selectIcon);
        btnSelectLineColor.setIcon(selectIcon);
        btnSelectFillColor.setIcon(selectIcon);
        btnSelectZoomFactorField.setIcon(selectIcon);
        btnSelectHaloColor.setIcon(selectIcon);
        slOpacity.getModel().setExtent(0);
        slOpacity.getModel().setMaximum(100);
        slOpacity.getModel().setMinimum(0);
        slOpacity.getModel().setValue(100);

        typeFieldRenderer.setBorder(new EtchedBorder());
        fieldRenderer.setBorder(new EtchedBorder());
        labelFieldRenderer.setBorder(new EtchedBorder());
        zoomFactorFieldRenderer.setBorder(new EtchedBorder());

        cbProjectionType.setRenderer(projectionTypeRenderer);
        TableColumn parameterColumn = tblProjectionParameters.getColumnModel().getColumn(ProjectionParametersTableModel.PARAMETER_NAME_COLUMN);
        parameterColumn.setHeaderValue(DbControlsDesignUtils.getLocalizedString(projectionParametersModel.getColumnName(ProjectionParametersTableModel.PARAMETER_NAME_COLUMN)));
        parameterColumn.setCellRenderer(new ParameterCellRenderer());
        TableColumn valueColumn = tblProjectionParameters.getColumnModel().getColumn(ProjectionParametersTableModel.PARAMETER_VALUE_COLUMN);
        valueColumn.setHeaderValue(DbControlsDesignUtils.getLocalizedString(projectionParametersModel.getColumnName(ProjectionParametersTableModel.PARAMETER_VALUE_COLUMN)));
        valueColumn.setCellEditor(new ParameterCellEditor());
        cbMapEventListener.setAction(getActionMap().get(ModifyMapEventListenerAction.class));
//        mapEventListenerGenerator = new ScriptGeneratorListener(mapEventListenersModel,
//                DbMapDesignInfo.MAP_EVENT_HANDLER_FUNCTION_NAME,
//                String.format("(%s)", DbMapDesignInfo.MAP_EVENT_HANDLER_FUNCTION_ARGUMENT));
    }

    protected final void fillActionMap() {
        ActionMap am = getActionMap();
        am.put(AddDataSourceAction.class, new AddDataSourceAction(this));
        am.put(RemoveDataSourceAction.class, new RemoveDataSourceAction(this));
        am.put(CutDataSourceAction.class, new CutDataSourceAction(this));
        am.put(CopyDataSourceAction.class, new CopyDataSourceAction(this));
        am.put(PasteDataSourceAction.class, new PasteDataSourceAction(this));
        am.put(MoveUpDataSourceAction.class, new MoveUpDataSourceAction(this));
        am.put(MoveDownDataSourceAction.class, new MoveDownDataSourceAction(this));
        am.put(SelectLineColorAction.class, new SelectLineColorAction(this));
        am.put(SelectFillColorAction.class, new SelectFillColorAction(this));
        am.put(SelectHaloColorAction.class, new SelectHaloColorAction(this));
        am.put(ClearStyleFieldAction.class, new ClearStyleFieldAction(this));
        am.put(SelectLabelFieldAction.class, new SelectLabelFieldAction(this));
        am.put(ClearZoomFactorFieldAction.class, new ClearZoomFactorFieldAction(this));
        am.put(SelectZoomFactorFieldAction.class, new SelectZoomFactorFieldAction(this));
        am.put(SelectLabelFontAction.class, new SelectLabelFontAction(this));
        am.put(ClearProjectionAction.class, new ClearProjectionAction(this));
        modifyMapEventAction = new ModifyMapEventListenerAction(this);
        am.put(ModifyMapEventListenerAction.class, modifyMapEventAction);
        clearMapEventAction = new ClearMapEventAction(this);
        am.put(ClearMapEventAction.class, clearMapEventAction);
        am.put(SelectTypeFieldAction.class, new SelectTypeFieldAction(this));
        am.put(ClearTypeFieldAction.class, new ClearTypeFieldAction(this));
        am.put(GeoCrsWktChangeAction.class, new GeoCrsWktChangeAction(this));
    }

    public JList getFeaturesList() {
        return lstFeatures;
    }

    @Override
    public void setObject(Object aObject) {
        final Object oldBean = bean;
        super.setObject(aObject);
        if (oldBean != aObject && aObject instanceof DbControl) {
            if (aObject != null && aObject instanceof DbMap) {
                try {
                    updateMapTitle();
                    updateMapProjection();
                    updateMapEvent();
                    updateZoomFactorField();
                    updateProjectionParameters();
                } catch (Exception ex) {
                    Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mnuLayers = new javax.swing.JPopupMenu();
        miAddLayer = new javax.swing.JMenuItem();
        miRemoveLayer = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        miCutLayer = new javax.swing.JMenuItem();
        miCopyLayer = new javax.swing.JMenuItem();
        miPasteLayer = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        miMoveUpLayer = new javax.swing.JMenuItem();
        miMoveDownLayer = new javax.swing.JMenuItem();
        btgPointSymbol = new javax.swing.ButtonGroup();
        tpSettings = new javax.swing.JTabbedPane();
        pnlMapSettings = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        tfTitle = new javax.swing.JTextField();
        lblZoomFactorField = new javax.swing.JLabel();
        pnlZoomFactorField = new javax.swing.JPanel();
        tfZoomFactorField = new javax.swing.JTextField();
        btnSelectZoomFactorField = new javax.swing.JButton();
        btnClearZoomFactorField = new javax.swing.JButton();
        pnlProjection = new javax.swing.JPanel();
        lblProjectionType = new javax.swing.JLabel();
        cbProjectionType = new javax.swing.JComboBox();
        lblProjectionParameters = new javax.swing.JLabel();
        spProjectionParameters = new javax.swing.JScrollPane();
        tblProjectionParameters = new javax.swing.JTable();
        btnClearProjection = new javax.swing.JButton();
        lblMapEventListener = new javax.swing.JLabel();
        cbMapEventListener = new javax.swing.JComboBox();
        btnClearMapEventListener = new javax.swing.JButton();
        lblGeoCrsWkt = new javax.swing.JLabel();
        tfGeoCrsWkt = new javax.swing.JTextField();
        spDataSources = new javax.swing.JSplitPane();
        scrollFeatures = new javax.swing.JScrollPane();
        lstFeatures = new javax.swing.JList();
        pnlFeatureType = new javax.swing.JPanel();
        lblTypeName = new javax.swing.JLabel();
        tfTypeName = new javax.swing.JTextField();
        lblGeometry = new javax.swing.JLabel();
        cbGeometry = new javax.swing.JComboBox();
        lblCRS = new javax.swing.JLabel();
        tfCRS = new javax.swing.JTextField();
        pnlPointSymbol = new javax.swing.JPanel();
        btnCirclePoint = new javax.swing.JToggleButton();
        lblPoint = new javax.swing.JLabel();
        btnSquarePoint = new javax.swing.JToggleButton();
        btnCrossPoint = new javax.swing.JToggleButton();
        btnXPoint = new javax.swing.JToggleButton();
        btnTrianglePoint = new javax.swing.JToggleButton();
        btnStarPoint = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        lblLabelFont = new javax.swing.JLabel();
        btnSelectLabelFont = new javax.swing.JButton();
        btnClearLabelFont = new javax.swing.JButton();
        lblLabelField = new javax.swing.JLabel();
        btnSelectLabelField = new javax.swing.JButton();
        btnClearLabelField = new javax.swing.JButton();
        lblSize = new javax.swing.JLabel();
        spnSize = new javax.swing.JSpinner();
        btnClearSize = new javax.swing.JButton();
        lblLineColor = new javax.swing.JLabel();
        btnSelectLineColor = new javax.swing.JButton();
        btnClearLineColor = new javax.swing.JButton();
        lblFillColor = new javax.swing.JLabel();
        btnSelectFillColor = new javax.swing.JButton();
        btnClearFillColor = new javax.swing.JButton();
        lblOpacity = new javax.swing.JLabel();
        slOpacity = new javax.swing.JSlider();
        spnOpacity = new javax.swing.JSpinner();
        btnClearOpacity = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        lblLineColorExample = new javax.swing.JLabel();
        lblFillColorExample = new javax.swing.JLabel();
        pnlLabelField = new javax.swing.JPanel();
        tfLabelField = new javax.swing.JTextField();
        pnlLabelFont = new javax.swing.JPanel();
        tfLabelFont = new javax.swing.JTextField();
        lblHaloColor = new javax.swing.JLabel();
        lblHaloColorExample = new javax.swing.JLabel();
        btnSelectHaloColor = new javax.swing.JButton();
        btnClearHaloColor = new javax.swing.JButton();
        lblTypeField = new javax.swing.JLabel();
        spinTypeValue = new javax.swing.JSpinner();
        btnSelectTypeField = new javax.swing.JButton();
        btnClearTypeField = new javax.swing.JButton();
        pnlTypeField = new javax.swing.JPanel();
        tfTypeField = new javax.swing.JTextField();
        cbActiveLayer = new javax.swing.JCheckBox();
        cbSelectableLayer = new javax.swing.JCheckBox();

        miAddLayer.setAction(getActionMap().get(AddDataSourceAction.class));
        miAddLayer.setText(DbControlsDesignUtils.getLocalizedString("map.menu.layer.add")); // NOI18N
        mnuLayers.add(miAddLayer);

        miRemoveLayer.setAction(getActionMap().get(RemoveDataSourceAction.class));
        miRemoveLayer.setText(DbControlsDesignUtils.getLocalizedString("map.menu.layer.remove")); // NOI18N
        mnuLayers.add(miRemoveLayer);
        mnuLayers.add(jSeparator1);

        miCutLayer.setAction(getActionMap().get(CutDataSourceAction.class));
        miCutLayer.setText(DbControlsDesignUtils.getLocalizedString("map.menu.layer.cut")); // NOI18N
        mnuLayers.add(miCutLayer);

        miCopyLayer.setAction(getActionMap().get(CopyDataSourceAction.class));
        miCopyLayer.setText(DbControlsDesignUtils.getLocalizedString("map.menu.layer.copy")); // NOI18N
        mnuLayers.add(miCopyLayer);

        miPasteLayer.setAction(getActionMap().get(PasteDataSourceAction.class));
        miPasteLayer.setText(DbControlsDesignUtils.getLocalizedString("map.menu.layer.paste")); // NOI18N
        mnuLayers.add(miPasteLayer);
        mnuLayers.add(jSeparator2);

        miMoveUpLayer.setAction(getActionMap().get(MoveUpDataSourceAction.class));
        miMoveUpLayer.setText(DbControlsDesignUtils.getLocalizedString("map.menu.layer.up")); // NOI18N
        mnuLayers.add(miMoveUpLayer);

        miMoveDownLayer.setAction(getActionMap().get(MoveDownDataSourceAction.class));
        miMoveDownLayer.setText(DbControlsDesignUtils.getLocalizedString("map.menu.layer.down")); // NOI18N
        mnuLayers.add(miMoveDownLayer);

        setLayout(new java.awt.BorderLayout());

        pnlMapSettings.setLayout(new java.awt.GridBagLayout());

        lblTitle.setLabelFor(tfTitle);
        lblTitle.setText(DbControlsDesignUtils.getLocalizedString("map.title")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 12, 6);
        pnlMapSettings.add(lblTitle, gridBagConstraints);

        tfTitle.setColumns(25);
        tfTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfTitleActionPerformed(evt);
            }
        });
        tfTitle.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfTitleFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 12, 12);
        pnlMapSettings.add(tfTitle, gridBagConstraints);

        lblZoomFactorField.setText(DbControlsDesignUtils.getLocalizedString("map.zoom.lbl")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 6);
        pnlMapSettings.add(lblZoomFactorField, gridBagConstraints);

        pnlZoomFactorField.setLayout(new java.awt.BorderLayout());

        tfZoomFactorField.setEditable(false);
        tfZoomFactorField.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
        pnlZoomFactorField.add(tfZoomFactorField, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        pnlMapSettings.add(pnlZoomFactorField, gridBagConstraints);

        btnSelectZoomFactorField.setAction(getActionMap().get(SelectZoomFactorFieldAction.class));
        btnSelectZoomFactorField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        pnlMapSettings.add(btnSelectZoomFactorField, gridBagConstraints);

        btnClearZoomFactorField.setAction(getActionMap().get(ClearZoomFactorFieldAction.class));
        btnClearZoomFactorField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 12);
        pnlMapSettings.add(btnClearZoomFactorField, gridBagConstraints);

        pnlProjection.setLayout(new java.awt.GridBagLayout());

        lblProjectionType.setText(DbControlsDesignUtils.getLocalizedString("map.projection.type.lbl")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        pnlProjection.add(lblProjectionType, gridBagConstraints);

        cbProjectionType.setModel(projectionsModel);
        cbProjectionType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbProjectionTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 11, 0, 0);
        pnlProjection.add(cbProjectionType, gridBagConstraints);

        lblProjectionParameters.setText(DbControlsDesignUtils.getLocalizedString("map.projection.parameters.lbl")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(11, 0, 0, 0);
        pnlProjection.add(lblProjectionParameters, gridBagConstraints);

        tblProjectionParameters.setModel(projectionParametersModel);
        spProjectionParameters.setViewportView(tblProjectionParameters);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        pnlProjection.add(spProjectionParameters, gridBagConstraints);

        btnClearProjection.setAction(getActionMap().get(ClearProjectionAction.class));
        btnClearProjection.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        pnlProjection.add(btnClearProjection, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 12);
        pnlMapSettings.add(pnlProjection, gridBagConstraints);

        lblMapEventListener.setLabelFor(cbMapEventListener);
        lblMapEventListener.setText(DbControlsDesignUtils.getLocalizedString("map.event.lbl")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 6);
        pnlMapSettings.add(lblMapEventListener, gridBagConstraints);

        cbMapEventListener.setEditable(true);
        cbMapEventListener.setModel(mapEventListenersModel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 0);
        pnlMapSettings.add(cbMapEventListener, gridBagConstraints);

        btnClearMapEventListener.setAction(getActionMap().get(ClearMapEventAction.class));
        btnClearMapEventListener.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 12);
        pnlMapSettings.add(btnClearMapEventListener, gridBagConstraints);

        lblGeoCrsWkt.setLabelFor(tfGeoCrsWkt);
        lblGeoCrsWkt.setText(DbControlsDesignUtils.getLocalizedString("map.geo.crs.wkt.lbl")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 12, 12, 0);
        pnlMapSettings.add(lblGeoCrsWkt, gridBagConstraints);

        tfGeoCrsWkt.setColumns(25);
        tfGeoCrsWkt.setAction(getActionMap().get(GeoCrsWktChangeAction.class));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 12, 12);
        pnlMapSettings.add(tfGeoCrsWkt, gridBagConstraints);

        tpSettings.addTab(DbControlsDesignUtils.getLocalizedString("map.settings.title"), pnlMapSettings); // NOI18N

        spDataSources.setDividerLocation(150);
        spDataSources.setResizeWeight(0.3);

        lstFeatures.setModel(featuresListModel);
        lstFeatures.setComponentPopupMenu(mnuLayers);
        scrollFeatures.setViewportView(lstFeatures);

        spDataSources.setLeftComponent(scrollFeatures);

        lblTypeName.setLabelFor(tfTypeName);
        lblTypeName.setText(DbControlsDesignUtils.getLocalizedString("map.layer.name.lbl")); // NOI18N
        lblTypeName.setEnabled(false);

        tfTypeName.setEnabled(false);
        tfTypeName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfTypeNameActionPerformed(evt);
            }
        });
        tfTypeName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfTypeNameFocusLost(evt);
            }
        });

        lblGeometry.setLabelFor(cbGeometry);
        lblGeometry.setText(DbControlsDesignUtils.getLocalizedString("map.layer.geometry.type.lbl")); // NOI18N
        lblGeometry.setEnabled(false);

        cbGeometry.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "com.vividsolutions.jts.geom.LineString", "com.vividsolutions.jts.geom.MultiLineString", "com.vividsolutions.jts.geom.MultiPoint", "com.vividsolutions.jts.geom.MultiPolygon", "com.vividsolutions.jts.geom.Point", "com.vividsolutions.jts.geom.Polygon", " " }));
        cbGeometry.setEnabled(false);
        cbGeometry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbGeometryActionPerformed(evt);
            }
        });

        lblCRS.setLabelFor(tfCRS);
        lblCRS.setText(DbControlsDesignUtils.getLocalizedString("map.layer.crs.lbl")); // NOI18N
        lblCRS.setEnabled(false);

        tfCRS.setEnabled(false);
        tfCRS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfCRSActionPerformed(evt);
            }
        });
        tfCRS.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tfCRSFocusLost(evt);
            }
        });

        btgPointSymbol.add(btnCirclePoint);
        btnCirclePoint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/dbcontrols/resources/32x32/Circle.png"))); // NOI18N
        btnCirclePoint.setEnabled(false);
        btnCirclePoint.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnCirclePointStateChanged(evt);
            }
        });

        lblPoint.setText(DbControlsDesignUtils.getLocalizedString("map.layer.symbol.lbl")); // NOI18N
        lblPoint.setEnabled(false);

        btgPointSymbol.add(btnSquarePoint);
        btnSquarePoint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/dbcontrols/resources/32x32/Square.png"))); // NOI18N
        btnSquarePoint.setEnabled(false);
        btnSquarePoint.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnSquarePointStateChanged(evt);
            }
        });
        btnSquarePoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSquarePointActionPerformed(evt);
            }
        });

        btgPointSymbol.add(btnCrossPoint);
        btnCrossPoint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/dbcontrols/resources/32x32/Cross.png"))); // NOI18N
        btnCrossPoint.setEnabled(false);
        btnCrossPoint.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnCrossPointStateChanged(evt);
            }
        });

        btgPointSymbol.add(btnXPoint);
        btnXPoint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/dbcontrols/resources/32x32/X.png"))); // NOI18N
        btnXPoint.setEnabled(false);
        btnXPoint.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnXPointStateChanged(evt);
            }
        });

        btgPointSymbol.add(btnTrianglePoint);
        btnTrianglePoint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/dbcontrols/resources/32x32/Triangle.png"))); // NOI18N
        btnTrianglePoint.setEnabled(false);
        btnTrianglePoint.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnTrianglePointStateChanged(evt);
            }
        });

        btgPointSymbol.add(btnStarPoint);
        btnStarPoint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/dbcontrols/resources/32x32/Star.png"))); // NOI18N
        btnStarPoint.setEnabled(false);
        btnStarPoint.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                btnStarPointStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pnlPointSymbolLayout = new javax.swing.GroupLayout(pnlPointSymbol);
        pnlPointSymbol.setLayout(pnlPointSymbolLayout);
        pnlPointSymbolLayout.setHorizontalGroup(
            pnlPointSymbolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPointSymbolLayout.createSequentialGroup()
                .addGroup(pnlPointSymbolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPoint)
                    .addGroup(pnlPointSymbolLayout.createSequentialGroup()
                        .addComponent(btnCirclePoint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSquarePoint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCrossPoint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnXPoint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTrianglePoint)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStarPoint)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlPointSymbolLayout.setVerticalGroup(
            pnlPointSymbolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPointSymbolLayout.createSequentialGroup()
                .addComponent(lblPoint)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlPointSymbolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnStarPoint)
                    .addComponent(btnTrianglePoint)
                    .addComponent(btnXPoint)
                    .addComponent(btnCrossPoint)
                    .addComponent(btnSquarePoint)
                    .addComponent(btnCirclePoint)))
        );

        jPanel3.setLayout(new java.awt.GridBagLayout());

        lblLabelFont.setText(DbControlsDesignUtils.getLocalizedString("map.layer.label.font.lbl")); // NOI18N
        lblLabelFont.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        jPanel3.add(lblLabelFont, gridBagConstraints);

        btnSelectLabelFont.setAction(getActionMap().get(SelectLabelFontAction.class));
        btnSelectLabelFont.setEnabled(false);
        btnSelectLabelFont.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnSelectLabelFont, gridBagConstraints);

        btnClearLabelFont.setAction(getActionMap().get(ClearStyleFieldAction.class));
        btnClearLabelFont.setActionCommand(ClearStyleFieldAction.LABELFONT);
        btnClearLabelFont.setEnabled(false);
        btnClearLabelFont.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnClearLabelFont, gridBagConstraints);

        lblLabelField.setText(DbControlsDesignUtils.getLocalizedString("map.layer.label.dmref.lbl")); // NOI18N
        lblLabelField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        jPanel3.add(lblLabelField, gridBagConstraints);

        btnSelectLabelField.setAction(getActionMap().get(SelectLabelFieldAction.class));
        btnSelectLabelField.setEnabled(false);
        btnSelectLabelField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnSelectLabelField, gridBagConstraints);

        btnClearLabelField.setAction(getActionMap().get(ClearStyleFieldAction.class));
        btnClearLabelField.setActionCommand(ClearStyleFieldAction.LABELFIELD);
        btnClearLabelField.setEnabled(false);
        btnClearLabelField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnClearLabelField, gridBagConstraints);

        lblSize.setLabelFor(spnSize);
        lblSize.setText(DbControlsDesignUtils.getLocalizedString("map.layer.width.lbl")); // NOI18N
        lblSize.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        jPanel3.add(lblSize, gridBagConstraints);

        spnSize.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(1.0f), Float.valueOf(0.0f), null, Float.valueOf(0.01f)));
        spnSize.setEnabled(false);
        spnSize.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnSizeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(spnSize, gridBagConstraints);

        btnClearSize.setAction(getActionMap().get(ClearStyleFieldAction.class));
        btnClearSize.setActionCommand(ClearStyleFieldAction.SIZE);
        btnClearSize.setEnabled(false);
        btnClearSize.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnClearSize, gridBagConstraints);

        lblLineColor.setText(DbControlsDesignUtils.getLocalizedString("map.layer.color.line.lbl")); // NOI18N
        lblLineColor.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        jPanel3.add(lblLineColor, gridBagConstraints);

        btnSelectLineColor.setAction(getActionMap().get(SelectLineColorAction.class));
        btnSelectLineColor.setEnabled(false);
        btnSelectLineColor.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnSelectLineColor, gridBagConstraints);

        btnClearLineColor.setAction(getActionMap().get(ClearStyleFieldAction.class));
        btnClearLineColor.setActionCommand(ClearStyleFieldAction.LINECOLOR);
        btnClearLineColor.setEnabled(false);
        btnClearLineColor.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnClearLineColor, gridBagConstraints);

        lblFillColor.setText(DbControlsDesignUtils.getLocalizedString("map.layer.color.fill.lbl")); // NOI18N
        lblFillColor.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        jPanel3.add(lblFillColor, gridBagConstraints);

        btnSelectFillColor.setAction(getActionMap().get(SelectFillColorAction.class));
        btnSelectFillColor.setEnabled(false);
        btnSelectFillColor.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnSelectFillColor, gridBagConstraints);

        btnClearFillColor.setAction(getActionMap().get(ClearStyleFieldAction.class));
        btnClearFillColor.setActionCommand(ClearStyleFieldAction.FILLCOLOR);
        btnClearFillColor.setEnabled(false);
        btnClearFillColor.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnClearFillColor, gridBagConstraints);

        lblOpacity.setLabelFor(spnSize);
        lblOpacity.setText(DbControlsDesignUtils.getLocalizedString("map.layer.opacity.lbl")); // NOI18N
        lblOpacity.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        jPanel3.add(lblOpacity, gridBagConstraints);

        slOpacity.setEnabled(false);
        slOpacity.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slOpacityStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(slOpacity, gridBagConstraints);

        spnOpacity.setModel(new javax.swing.SpinnerNumberModel(100, 0, 100, 1));
        spnOpacity.setEnabled(false);
        spnOpacity.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnOpacityStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(spnOpacity, gridBagConstraints);

        btnClearOpacity.setAction(getActionMap().get(ClearStyleFieldAction.class));
        btnClearOpacity.setActionCommand(ClearStyleFieldAction.OPACITY);
        btnClearOpacity.setEnabled(false);
        btnClearOpacity.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnClearOpacity, gridBagConstraints);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 323, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 47, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel3.add(jPanel4, gridBagConstraints);

        lblLineColorExample.setBackground(new java.awt.Color(0, 153, 0));
        lblLineColorExample.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
        lblLineColorExample.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lblLineColorExample.setEnabled(false);
        lblLineColorExample.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 1);
        jPanel3.add(lblLineColorExample, gridBagConstraints);

        lblFillColorExample.setBackground(new java.awt.Color(0, 153, 0));
        lblFillColorExample.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
        lblFillColorExample.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lblFillColorExample.setEnabled(false);
        lblFillColorExample.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 1);
        jPanel3.add(lblFillColorExample, gridBagConstraints);

        pnlLabelField.setLayout(new java.awt.BorderLayout());

        tfLabelField.setEditable(false);
        tfLabelField.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
        tfLabelField.setEnabled(false);
        pnlLabelField.add(tfLabelField, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(pnlLabelField, gridBagConstraints);

        pnlLabelFont.setLayout(new java.awt.BorderLayout());

        tfLabelFont.setEditable(false);
        tfLabelFont.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
        tfLabelFont.setEnabled(false);
        pnlLabelFont.add(tfLabelFont, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(pnlLabelFont, gridBagConstraints);

        lblHaloColor.setText(DbControlsDesignUtils.getLocalizedString("map.layer.color.halo.lbl")); // NOI18N
        lblHaloColor.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 5);
        jPanel3.add(lblHaloColor, gridBagConstraints);

        lblHaloColorExample.setBackground(new java.awt.Color(0, 153, 0));
        lblHaloColorExample.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
        lblHaloColorExample.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        lblHaloColorExample.setEnabled(false);
        lblHaloColorExample.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 1);
        jPanel3.add(lblHaloColorExample, gridBagConstraints);

        btnSelectHaloColor.setAction(getActionMap().get(SelectHaloColorAction.class));
        btnSelectHaloColor.setEnabled(false);
        btnSelectHaloColor.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnSelectHaloColor, gridBagConstraints);

        btnClearHaloColor.setAction(getActionMap().get(ClearStyleFieldAction.class));
        btnClearHaloColor.setActionCommand(ClearStyleFieldAction.HALOCOLOR);
        btnClearHaloColor.setEnabled(false);
        btnClearHaloColor.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnClearHaloColor, gridBagConstraints);

        lblTypeField.setText(DbControlsDesignUtils.getLocalizedString("map.layer.type.dmref.lbl")); // NOI18N
        lblTypeField.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(lblTypeField, gridBagConstraints);

        spinTypeValue.setModel(new javax.swing.SpinnerNumberModel(0, 0, 1000000000, 1));
        spinTypeValue.setEnabled(false);
        spinTypeValue.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinTypeValueStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel3.add(spinTypeValue, gridBagConstraints);

        btnSelectTypeField.setAction(getActionMap().get(SelectTypeFieldAction.class));
        btnSelectTypeField.setEnabled(false);
        btnSelectTypeField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnSelectTypeField, gridBagConstraints);

        btnClearTypeField.setAction(getActionMap().get(ClearTypeFieldAction.class));
        btnClearTypeField.setEnabled(false);
        btnClearTypeField.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        jPanel3.add(btnClearTypeField, gridBagConstraints);

        pnlTypeField.setLayout(new java.awt.BorderLayout());

        tfTypeField.setEditable(false);
        tfTypeField.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
        tfTypeField.setEnabled(false);
        pnlTypeField.add(tfTypeField, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 2.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel3.add(pnlTypeField, gridBagConstraints);

        cbActiveLayer.setText(DbControlsDesignUtils.getLocalizedString("map.layer.active.chk")); // NOI18N
        cbActiveLayer.setEnabled(false);
        cbActiveLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbActiveLayerActionPerformed(evt);
            }
        });

        cbSelectableLayer.setText(DbControlsDesignUtils.getLocalizedString("map.layer.selectable.chk")); // NOI18N
        cbSelectableLayer.setEnabled(false);
        cbSelectableLayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbSelectableLayerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlFeatureTypeLayout = new javax.swing.GroupLayout(pnlFeatureType);
        pnlFeatureType.setLayout(pnlFeatureTypeLayout);
        pnlFeatureTypeLayout.setHorizontalGroup(
            pnlFeatureTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFeatureTypeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFeatureTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbSelectableLayer)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlFeatureTypeLayout.createSequentialGroup()
                        .addGroup(pnlFeatureTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTypeName)
                            .addComponent(lblGeometry)
                            .addComponent(lblCRS))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlFeatureTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfCRS)
                            .addComponent(tfTypeName)
                            .addComponent(cbGeometry, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(pnlPointSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbActiveLayer))
                .addContainerGap())
        );
        pnlFeatureTypeLayout.setVerticalGroup(
            pnlFeatureTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlFeatureTypeLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlFeatureTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTypeName)
                    .addComponent(tfTypeName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFeatureTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGeometry)
                    .addComponent(cbGeometry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlFeatureTypeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCRS)
                    .addComponent(tfCRS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(pnlPointSymbol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 4, Short.MAX_VALUE)
                .addComponent(cbSelectableLayer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbActiveLayer)
                .addContainerGap())
        );

        spDataSources.setRightComponent(pnlFeatureType);

        tpSettings.addTab(DbControlsDesignUtils.getLocalizedString("map.layers.title"), spDataSources); // NOI18N

        add(tpSettings, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void tfTypeNameActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tfTypeNameActionPerformed
    {//GEN-HEADEREND:event_tfTypeNameActionPerformed
        final Object[] selected = lstFeatures.getSelectedValues();
        if (selected.length == 1) {
            final RowsetFeatureDescriptor feature = (RowsetFeatureDescriptor) selected[0];
            final ModifyTypeNameEdit edit = new ModifyTypeNameEdit(feature, feature.getTypeName(), tfTypeName.getText());
            feature.setTypeName(tfTypeName.getText());
            undoSupport.postEdit(edit);
        } else {
            throw new IllegalStateException(String.format("Selected %d arguments.", selected.length));
        }
    }//GEN-LAST:event_tfTypeNameActionPerformed

    private void tfTypeNameFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_tfTypeNameFocusLost
    {//GEN-HEADEREND:event_tfTypeNameFocusLost
        final Object[] selected = lstFeatures.getSelectedValues();
        if (selected.length == 1) {
            tfTypeName.setText(((RowsetFeatureDescriptor) selected[0]).getTypeName());
        } else {
            throw new IllegalStateException(String.format("Selected %d arguments.", selected.length));
        }
    }//GEN-LAST:event_tfTypeNameFocusLost

    private void cbGeometryActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbGeometryActionPerformed
    {//GEN-HEADEREND:event_cbGeometryActionPerformed
        final Object[] selected = lstFeatures.getSelectedValues();
        if (selected.length == 1) {
            final Class binding = (Class) cbGeometry.getSelectedItem();
            final RowsetFeatureDescriptor desc = (RowsetFeatureDescriptor) selected[0];
            if (!binding.getName().equals(desc.getGeometryBinding())) {
                final ModifyBindingEdit edit = new ModifyBindingEdit(desc, desc.getGeometryBinding(), binding.getName());
                desc.setGeometryBinding(binding.getName());
                undoSupport.postEdit(edit);
            }
        } else {
            throw new IllegalStateException(String.format("Selected %d arguments.", selected.length));
        }
    }//GEN-LAST:event_cbGeometryActionPerformed

    private void tfCRSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tfCRSActionPerformed
    {//GEN-HEADEREND:event_tfCRSActionPerformed
        final Object[] selected = lstFeatures.getSelectedValues();
        if (selected.length == 1) {
            final RowsetFeatureDescriptor feature = (RowsetFeatureDescriptor) selected[0];
            final String crsWkt = tfCRS.getText();
            try {
                if (crsWkt != null && (crsWkt.isEmpty() || CRS.parseWKT(crsWkt) != null)) {
                    final ModifyCRSEdit edit = new ModifyCRSEdit(feature, feature.getCrsWkt(), crsWkt);
                    feature.setCrsWkt(crsWkt);
                    undoSupport.postEdit(edit);
                } else {
                    JOptionPane.showMessageDialog(this, "Wrong WKT for CRS.", "Invalid WKT", JOptionPane.ERROR_MESSAGE);
                }
            } catch (FactoryException ex) {
                JOptionPane.showMessageDialog(this, String.format("Wrong WKT for CRS." + "\n%s", ex.getMessage()), "Invalid WKT", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            throw new IllegalStateException(String.format("Selected %d arguments.", selected.length));
        }
    }

    private void validateCRS(final String crs) {
        try {
            if (CRS.parseWKT(crs) != null);
        } catch (FactoryException ex) {
            Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_tfCRSActionPerformed

    private void tfCRSFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_tfCRSFocusLost
    {//GEN-HEADEREND:event_tfCRSFocusLost
        final Object[] selected = lstFeatures.getSelectedValues();
        if (selected.length == 1) {
            tfCRS.setText(((RowsetFeatureDescriptor) selected[0]).getCrsWkt());
        } else {
            throw new IllegalStateException(String.format("Selected %d arguments.", selected.length));
        }
    }//GEN-LAST:event_tfCRSFocusLost

    private void tfTitleActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tfTitleActionPerformed
    {//GEN-HEADEREND:event_tfTitleActionPerformed
        try {
            String newTitle = tfTitle.getText();
            if (newTitle != null && newTitle.isEmpty()) {
                newTitle = null;
            }
            ModifyBeanPropertyEdit<String> edit = new ModifyBeanPropertyEdit<>(String.class, designInfo, DbMapDesignInfo.PROP_MAP_TITLE, ((DbMapDesignInfo) designInfo).getMapTitle(), newTitle);
            ((DbMapDesignInfo) designInfo).setMapTitle(newTitle);
            undoSupport.postEdit(edit);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
            ModifyBeanPropertyEdit.showNoSetterError(this, DbMapDesignInfo.PROP_MAP_TITLE, designInfo);
        }
    }//GEN-LAST:event_tfTitleActionPerformed

    private void tfTitleFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_tfTitleFocusLost
    {//GEN-HEADEREND:event_tfTitleFocusLost
        tfTitle.setText(((DbMapDesignInfo) designInfo).getMapTitle());
    }//GEN-LAST:event_tfTitleFocusLost

    private void spnSizeStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spnSizeStateChanged
    {//GEN-HEADEREND:event_spnSizeStateChanged
        if (!updatingView) {
            final Object[] selected = lstFeatures.getSelectedValues();
            if (selected.length == 1) {
                try {
                    final RowsetFeatureDescriptor feature = (RowsetFeatureDescriptor) selected[0];
                    final FeatureStyleDescriptor oldStyle = feature.getStyle();
                    final float spinnerValue = ((Number) spnSize.getValue()).floatValue();
                    if (Math.abs(spinnerValue - (oldStyle.getSize() == null ? DEFAULT_SIZE_VALUE : oldStyle.getSize())) >= 1e-3) {
                        final FeatureStyleDescriptor newStyle = oldStyle.clone();
                        newStyle.setSize(spinnerValue);
                        final ModifyStyleEdit edit = new ModifyStyleEdit(feature, oldStyle, newStyle);
                        feature.setStyle(newStyle);
                        undoSupport.postEdit(edit);
                    }
                } catch (CloneNotSupportedException ex) {
                    Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }//GEN-LAST:event_spnSizeStateChanged

    private void spnOpacityStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_spnOpacityStateChanged
    {//GEN-HEADEREND:event_spnOpacityStateChanged
        if (!updatingView) {
            final int newOpacity = ((Number) spnOpacity.getValue()).intValue();
            if (setStyleOpacity(newOpacity)) {
                updatingView = true;
                try {
                    slOpacity.setValue(newOpacity);
                } finally {
                    updatingView = false;
                }
            }
        }
    }//GEN-LAST:event_spnOpacityStateChanged

    private void slOpacityStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_slOpacityStateChanged
    {//GEN-HEADEREND:event_slOpacityStateChanged
        if (!updatingView) {
            final int newOpacity = ((Number) slOpacity.getValue()).intValue();
            if (!slOpacity.getModel().getValueIsAdjusting() && setStyleOpacity(newOpacity)) {
                updatingView = true;
                try {
                    spnOpacity.setValue(newOpacity);
                } finally {
                    updatingView = false;
                }
            }
        }
    }//GEN-LAST:event_slOpacityStateChanged

    private void btnCirclePointStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_btnCirclePointStateChanged
    {//GEN-HEADEREND:event_btnCirclePointStateChanged
        if (btnCirclePoint.isSelected()) {
            setPointStyle(PointSymbol.CIRCLE);
        }
    }//GEN-LAST:event_btnCirclePointStateChanged

    private void btnSquarePointStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_btnSquarePointStateChanged
    {//GEN-HEADEREND:event_btnSquarePointStateChanged
        if (btnSquarePoint.isSelected()) {
            setPointStyle(PointSymbol.SQUARE);
        }
    }//GEN-LAST:event_btnSquarePointStateChanged

    private void btnCrossPointStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_btnCrossPointStateChanged
    {//GEN-HEADEREND:event_btnCrossPointStateChanged
        if (btnCrossPoint.isSelected()) {
            setPointStyle(PointSymbol.CROSS);
        }
    }//GEN-LAST:event_btnCrossPointStateChanged

    private void btnXPointStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_btnXPointStateChanged
    {//GEN-HEADEREND:event_btnXPointStateChanged
        if (btnXPoint.isSelected()) {
            setPointStyle(PointSymbol.X);
        }
    }//GEN-LAST:event_btnXPointStateChanged

    private void btnTrianglePointStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_btnTrianglePointStateChanged
    {//GEN-HEADEREND:event_btnTrianglePointStateChanged
        if (btnTrianglePoint.isSelected()) {
            setPointStyle(PointSymbol.TRIANGLE);
        }
    }//GEN-LAST:event_btnTrianglePointStateChanged

    private void btnStarPointStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_btnStarPointStateChanged
    {//GEN-HEADEREND:event_btnStarPointStateChanged
        if (btnStarPoint.isSelected()) {
            setPointStyle(PointSymbol.STAR);
        }
    }//GEN-LAST:event_btnStarPointStateChanged

    private void cbProjectionTypeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbProjectionTypeActionPerformed
    {//GEN-HEADEREND:event_cbProjectionTypeActionPerformed
        if (cbProjectionType.getSelectedItem() == null) {
            if (((DbMapDesignInfo) designInfo).getProjectionName() != null) {
                try {
                    ModifyBeanPropertyEdit<String> projectionEdit = new ModifyBeanPropertyEdit<>(String.class, designInfo, DbMapDesignInfo.PROP_PROJECTION_NAME, ((DbMapDesignInfo) designInfo).getProjectionName(), null);
                    try {
                        ModifyBeanPropertyEdit<ParameterValueGroup> parametersEdit = new ModifyBeanPropertyEdit<>(ParameterValueGroup.class, designInfo, DbMapDesignInfo.PROP_PROJECTION_PARAMETERS, ((DbMapDesignInfo) designInfo).getProjectionParameters(), null);
                        ((DbMapDesignInfo) designInfo).setProjectionName(null);
                        ((DbMapDesignInfo) designInfo).setProjectionParameters(null);
                        final CompoundEdit compoundEdit = new CompoundEdit();
                        compoundEdit.addEdit(projectionEdit);
                        compoundEdit.addEdit(parametersEdit);
                        compoundEdit.end();
                        undoSupport.postEdit(compoundEdit);
                    } catch (NoSuchMethodException ex) {
                        Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                        ModifyBeanPropertyEdit.showNoSetterError(this, DbMapDesignInfo.PROP_PROJECTION_PARAMETERS, designInfo);
                    }
                } catch (NoSuchMethodException ex) {
                    Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                    ModifyBeanPropertyEdit.showNoSetterError(this, DbMapDesignInfo.PROP_PROJECTION_NAME, designInfo);
                }
            }
        } else {
            try {
                final OperationMethod selectedProjection = (OperationMethod) cbProjectionType.getSelectedItem();
                final String newProjectionName = selectedProjection.getName().getCode();
                if (!newProjectionName.equals(((DbMapDesignInfo) designInfo).getProjectionName())) {
                    // Projection type changed indeed.
                    ModifyBeanPropertyEdit<String> projectionEdit = new ModifyBeanPropertyEdit<>(String.class, designInfo, DbMapDesignInfo.PROP_PROJECTION_NAME, ((DbMapDesignInfo) designInfo).getProjectionName(), newProjectionName);
                    try {
                        final ParameterValueGroup newParameters = mtFactory.getDefaultParameters(newProjectionName);
                        final ParameterValueGroup oldParameters = ((DbMapDesignInfo) designInfo).getProjectionParameters();
                        if (((DbMapDesignInfo) designInfo).getProjectionName() != null && ((DbMapDesignInfo) designInfo).getProjectionParameters() != null) {
                            final ParameterValueGroup oldDefaults = mtFactory.getDefaultParameters(((DbMapDesignInfo) designInfo).getProjectionName());
                            duplicateSameParametersValues(oldParameters, oldDefaults, newParameters);
                        }
                        ModifyBeanPropertyEdit<ParameterValueGroup> parametersEdit = new ModifyBeanPropertyEdit<>(
                                ParameterValueGroup.class, designInfo, DbMapDesignInfo.PROP_PROJECTION_PARAMETERS, oldParameters, newParameters);
                        ((DbMapDesignInfo) designInfo).setProjectionName(newProjectionName);
                        ((DbMapDesignInfo) designInfo).setProjectionParameters(newParameters);
                        final CompoundEdit compoundEdit = new CompoundEdit();
                        compoundEdit.addEdit(projectionEdit);
                        compoundEdit.addEdit(parametersEdit);
                        compoundEdit.end();
                        undoSupport.postEdit(compoundEdit);
                    } catch (NoSuchMethodException ex) {
                        Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                        ModifyBeanPropertyEdit.showNoSetterError(this, DbMapDesignInfo.PROP_PROJECTION_PARAMETERS, designInfo);
                    }
                }
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                ModifyBeanPropertyEdit.showNoSetterError(this, DbMapDesignInfo.PROP_PROJECTION_NAME, designInfo);
            } catch (NoSuchIdentifierException ex) {
                Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, ex, "Error changing projection type", JOptionPane.ERROR_MESSAGE);
                projectionParametersModel.setParameters(null);
            }
        }
    }//GEN-LAST:event_cbProjectionTypeActionPerformed

    private void cbActiveLayerActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cbActiveLayerActionPerformed
    {//GEN-HEADEREND:event_cbActiveLayerActionPerformed
        final Object[] selected = lstFeatures.getSelectedValues();
        if (selected.length == 1) {
            final RowsetFeatureDescriptor feature = (RowsetFeatureDescriptor) selected[0];
            if (cbActiveLayer.isSelected() != feature.isActive()) {
                final ModifyActivenessEdit edit = new ModifyActivenessEdit(feature, feature.isActive(), cbActiveLayer.isSelected());
                feature.setActive(cbActiveLayer.isSelected());
                undoSupport.postEdit(edit);
            }
        }
    }//GEN-LAST:event_cbActiveLayerActionPerformed

    private void spinTypeValueStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinTypeValueStateChanged
        if (!updatingView) {
            Object oValue = spinTypeValue.getValue();
            if (oValue instanceof Integer) {
                setFeatureTypeValue((Integer) oValue);
            } else {
                setFeatureTypeValue(null);
            }
        }
    }//GEN-LAST:event_spinTypeValueStateChanged

    private void cbSelectableLayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbSelectableLayerActionPerformed
        final Object[] selected = lstFeatures.getSelectedValues();
        if (selected.length == 1) {
            final RowsetFeatureDescriptor feature = (RowsetFeatureDescriptor) selected[0];
            if (cbSelectableLayer.isSelected() != feature.isSelectable()) {
                final ModifySelectableEdit edit = new ModifySelectableEdit(feature, feature.isSelectable(), cbSelectableLayer.isSelected());
                feature.setSelectable(cbSelectableLayer.isSelected());
                undoSupport.postEdit(edit);
            }
        }
    }//GEN-LAST:event_cbSelectableLayerActionPerformed

    private void btnSquarePointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSquarePointActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSquarePointActionPerformed

    private void setPointStyle(PointSymbol symbol) {
        final Object[] selected = lstFeatures.getSelectedValues();
        if (selected.length == 1) {
            try {
                final RowsetFeatureDescriptor feature = (RowsetFeatureDescriptor) selected[0];
                final FeatureStyleDescriptor oldStyle = feature.getStyle();
                if (symbol == null && oldStyle.getPointSymbol() != null || symbol != null && !symbol.equals(oldStyle.getPointSymbol())) {
                    final FeatureStyleDescriptor newStyle = oldStyle.clone();
                    newStyle.setPointSymbol(symbol);
                    final ModifyStyleEdit edit = new ModifyStyleEdit(feature, oldStyle, newStyle);
                    feature.setStyle(newStyle);
                    undoSupport.postEdit(edit);
                }
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean setStyleOpacity(int opacity) {
        final Object[] selected = lstFeatures.getSelectedValues();
        if (selected.length == 1) {
            try {
                final RowsetFeatureDescriptor feature = (RowsetFeatureDescriptor) selected[0];
                final FeatureStyleDescriptor oldStyle = feature.getStyle();
                if (opacity != (oldStyle.getOpacity() == null ? DEFAULT_OPACITY_VALUE : oldStyle.getOpacity())) {
                    final FeatureStyleDescriptor newStyle = oldStyle.clone();
                    newStyle.setOpacity(opacity);
                    final ModifyStyleEdit edit = new ModifyStyleEdit(feature, oldStyle, newStyle);
                    feature.setStyle(newStyle);
                    undoSupport.postEdit(edit);
                    return true;
                }
            } catch (CloneNotSupportedException ex) {
                Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return false;
    }

    private boolean setFeatureTypeValue(Integer aNewValue) {
        final Object[] selected = lstFeatures.getSelectedValues();
        if (selected.length == 1) {
            final RowsetFeatureDescriptor feature = (RowsetFeatureDescriptor) selected[0];
            final Integer oldValue = feature.getTypeValue();
            if (oldValue != aNewValue) {
                final ModifyTypeValueEdit edit = new ModifyTypeValueEdit(feature, oldValue, aNewValue);
                feature.setTypeValue(aNewValue);
                undoSupport.postEdit(edit);
                return true;
            }
        }
        return false;
    }

    @Override
    public void setScriptHost(ScriptEvents aScriptEvents) {
        modifyMapEventAction.setScriptEvents(aScriptEvents);
        clearMapEventAction.setScriptEvents(aScriptEvents);
        super.setScriptHost(aScriptEvents);
    }

    @Override
    protected void updateView() {
        if (bean != null && bean instanceof DbMap) {
            try {
                updateZoomFactorField();
                updateGeoCrs();
                updateFeaturesList();
                updateSelectedFeature();
                updateMapEvent();
            } catch (Exception ex) {
                Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
            }
            checkActionMap();
        } else {
            featuresListModel.setDesignInfo(null);
        }
    }

    public void updateStyleView() throws Exception {
        if (bean != null && bean instanceof DbMap) {
            final Object[] selectedFeatures = lstFeatures.getSelectedValues();

            lblPoint.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            btnCirclePoint.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            btnSquarePoint.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            btnCrossPoint.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            btnXPoint.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            btnTrianglePoint.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            btnStarPoint.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            lblLabelFont.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            tfLabelFont.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            lblLabelField.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            tfLabelField.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            lblSize.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            lblLineColor.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            lblLineColorExample.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            lblFillColor.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            lblFillColorExample.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            lblHaloColor.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            lblHaloColorExample.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            lblOpacity.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);

            spnSize.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            slOpacity.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);
            spnOpacity.setEnabled(selectedFeatures != null && selectedFeatures.length == 1);

            final RowsetFeatureDescriptor feature = selectedFeatures != null && selectedFeatures.length == 1 ? (RowsetFeatureDescriptor) selectedFeatures[0] : null;

            if (btnCirclePoint.isEnabled() || btnSquarePoint.isEnabled() || btnCrossPoint.isEnabled()
                    || btnXPoint.isEnabled() || btnTrianglePoint.isEnabled() || btnStarPoint.isEnabled()) {
                if (feature.getStyle().getPointSymbol() == null) {
                    btgPointSymbol.clearSelection();
                } else {
                    switch (feature.getStyle().getPointSymbol()) {
                        case CIRCLE:
                            btnCirclePoint.setSelected(true);
                            break;
                        case SQUARE:
                            btnSquarePoint.setSelected(true);
                            break;
                        case CROSS:
                            btnCrossPoint.setSelected(true);
                            break;
                        case X:
                            btnXPoint.setSelected(true);
                            break;
                        case TRIANGLE:
                            btnTrianglePoint.setSelected(true);
                            break;
                        case STAR:
                            btnStarPoint.setSelected(true);
                            break;
                    }
                }
            }
            DbControlsDesignUtils.updateDmElement(getDatamodel(), feature == null ? null : feature.getStyle().getLabelField(), pnlLabelField, labelFieldRenderer, tfLabelField, fieldsFont);
            DbControlsDesignUtils.updateFont(feature == null ? null : feature.getStyle().getFont(), pnlLabelFont, tfLabelFont, fieldsFont);
            if (tfLabelFont.isEnabled()) {
                final Font labelFont = feature.getStyle().getFont();
                if (labelFont == null) {
                    tfLabelFont.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
                    tfLabelFont.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
                } else if (!labelFont.equals(tfLabelFont.getFont()) || !tfLabelFont.getText().equals(labelFont.toString())) {
                    tfLabelFont.setFont(labelFont);
                    tfLabelFont.setText(tfLabelFont.getFont().toString());
                }
            } else {
                tfLabelFont.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
            }
            if (lblLineColor.isEnabled()) {
                if (feature.getStyle().getLineColor() == null) {
                    lblLineColorExample.setBackground(pnlPointSymbol.getBackground());
                    lblLineColorExample.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
                } else if (!feature.getStyle().getLineColor().equals(lblLineColorExample.getBackground()) || !lblLineColorExample.getText().isEmpty()) {
                    lblLineColorExample.setBackground(feature.getStyle().getLineColor());
                    lblLineColorExample.setText(feature.getStyle().getLineColor().toString());
                }
            } else {
                lblLineColorExample.setBackground(pnlPointSymbol.getBackground());
                lblLineColorExample.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
            }
            if (lblFillColor.isEnabled()) {
                if (feature.getStyle().getFillColor() == null) {
                    lblFillColorExample.setBackground(pnlPointSymbol.getBackground());
                    lblFillColorExample.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
                } else if (!feature.getStyle().getFillColor().equals(lblFillColorExample.getBackground()) || !lblFillColorExample.getText().isEmpty()) {
                    lblFillColorExample.setBackground(feature.getStyle().getFillColor());
                    lblFillColorExample.setText(feature.getStyle().getFillColor().toString());
                }
            } else {
                lblFillColorExample.setBackground(pnlPointSymbol.getBackground());
                lblFillColorExample.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
            }
            if (lblHaloColor.isEnabled()) {
                if (feature.getStyle().getHaloColor() == null) {
                    lblHaloColorExample.setBackground(pnlPointSymbol.getBackground());
                    lblHaloColorExample.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
                } else if (!feature.getStyle().getHaloColor().equals(lblHaloColorExample.getBackground()) || !lblHaloColorExample.getText().isEmpty()) {
                    lblHaloColorExample.setBackground(feature.getStyle().getHaloColor());
                    lblHaloColorExample.setText(feature.getStyle().getHaloColor().toString());
                }
            } else {
                lblHaloColorExample.setBackground(pnlPointSymbol.getBackground());
                lblHaloColorExample.setText(DbControlsDesignUtils.getLocalizedString("notSelected"));
            }

            updatingView = true;
            try {
                if (spnOpacity.isEnabled()) {
                    if (feature.getStyle().getOpacity() == null) {
                        spnOpacity.setValue(DEFAULT_OPACITY_VALUE);
                        slOpacity.setValue(DEFAULT_OPACITY_VALUE);
                    } else if (!feature.getStyle().getOpacity().equals(spnOpacity.getValue()) || !feature.getStyle().getOpacity().equals(slOpacity.getValue())) {
                        spnOpacity.setValue(feature.getStyle().getOpacity());
                        slOpacity.setValue(feature.getStyle().getOpacity());
                    }
                } else {
                    spnOpacity.setValue(DEFAULT_OPACITY_VALUE);
                    slOpacity.setValue(DEFAULT_OPACITY_VALUE);
                }
                if (spnSize.isEnabled()) {
                    if (feature.getStyle().getSize() == null) {
                        spnSize.setValue(DEFAULT_SIZE_VALUE);
                    } else if (!feature.getStyle().getSize().equals(spnSize.getValue())) {
                        spnSize.setValue(feature.getStyle().getSize());
                    }
                } else {
                    spnSize.setValue(DEFAULT_SIZE_VALUE);
                }
            } finally {
                updatingView = false;
            }
        }
    }

    private void updateMapTitle() {
        tfTitle.setText(((DbMapDesignInfo) designInfo).getMapTitle());
    }

    private void updateMapProjection() {
        final Object projection = projectionsModel.getProjection(((DbMapDesignInfo) designInfo).getProjectionName());
        DbControlsDesignUtils.setSelectedItem(cbProjectionType, projection);
    }

    private void updateZoomFactorField() throws Exception {
        DbControlsDesignUtils.updateDmElement(getDatamodel(), ((DbMapDesignInfo) designInfo).getZoomFactorFieldRef(), pnlZoomFactorField, zoomFactorFieldRenderer, tfZoomFactorField, fieldsFont);
    }

    private void updateFeaturesList() {
        featuresListModel.setDesignInfo((DbMapDesignInfo) ((DbMap) bean).getDesignInfo());
        int[] selected = lstFeatures.getSelectedIndices();
        List<Integer> actuallySelected = new ArrayList<>();
        for (int i = 0; i < selected.length; i++) {
            if (selected[i] >= 0 && selected[i] < featuresListModel.getSize()) {
                actuallySelected.add(selected[i]);
            }
        }
        selected = new int[actuallySelected.size()];
        for (int i = 0; i < selected.length; i++) {
            selected[i] = actuallySelected.get(i);
        }
        if (selected.length > 0) {
            lstFeatures.setSelectedIndices(selected);
        } else {
            lstFeatures.clearSelection();
        }
    }

    private void updateFeatureTypeNameView() {
        final Object[] selectedFeatures = lstFeatures.getSelectedValues();
        lblTypeName.setEnabled(selectedFeatures.length == 1);
        tfTypeName.setEnabled(selectedFeatures.length == 1);
        if (tfTypeName.isEnabled()) {
            tfTypeName.setText(((RowsetFeatureDescriptor) selectedFeatures[0]).getTypeName());
        } else {
            tfTypeName.setText("");
        }
    }

    public void updateFeatureTypeView() throws Exception {
        final Object[] selectedFeatures = lstFeatures.getSelectedValues();
        final RowsetFeatureDescriptor feature = selectedFeatures.length == 1 ? (RowsetFeatureDescriptor) selectedFeatures[0] : null;
        Action selectAction = getActionMap().get(SelectTypeFieldAction.class);
        Action clearAction = getActionMap().get(ClearTypeFieldAction.class);
        lblTypeField.setEnabled(selectAction.isEnabled());
        tfTypeField.setEnabled(selectAction.isEnabled());
        spinTypeValue.setEnabled(clearAction.isEnabled());

        DbControlsDesignUtils.updateDmElement(getDatamodel(), feature == null ? null : feature.getTypeRef(), pnlTypeField, typeFieldRenderer, tfTypeField, fieldsFont);
        updatingView = true;
        try {
            if (feature != null) {
                if (feature.getTypeValue() != null) {
                    spinTypeValue.setValue(feature.getTypeValue());
                } else {
                    spinTypeValue.setValue(0);
                }
            }
        } finally {
            updatingView = false;
        }
    }

    private void updateGeoCrs() {
        tfGeoCrsWkt.setText(((DbMapDesignInfo) designInfo).getGeoCrsWkt());
    }

    private void updateFeatureCrsView() {
        final Object[] selectedFeatures = lstFeatures.getSelectedValues();
        lblCRS.setEnabled(selectedFeatures.length == 1);
        tfCRS.setEnabled(selectedFeatures.length == 1);
        if (tfCRS.isEnabled()) {
            final String crsWkt = ((RowsetFeatureDescriptor) selectedFeatures[0]).getCrsWkt();
            if (crsWkt == null && (tfCRS.getText() != null && !tfCRS.getText().isEmpty()) || crsWkt != null && !crsWkt.equals(tfCRS.getText())) {
                tfCRS.setText(crsWkt);
            }
        } else {
            tfCRS.setText("");
        }
    }

    private void updateFeatureActivenessView() {
        final Object[] selectedFeatures = lstFeatures.getSelectedValues();
        cbActiveLayer.setEnabled(selectedFeatures.length == 1);
        if (cbActiveLayer.isEnabled()) {
            cbActiveLayer.setSelected(((RowsetFeatureDescriptor) selectedFeatures[0]).isActive());
        } else {
            cbActiveLayer.setSelected(false);
        }
    }

    private void updateFeatureSelectableView() {
        final Object[] selectedFeatures = lstFeatures.getSelectedValues();
        cbSelectableLayer.setEnabled(selectedFeatures.length == 1);
        if (cbSelectableLayer.isEnabled()) {
            cbSelectableLayer.setSelected(((RowsetFeatureDescriptor) selectedFeatures[0]).isSelectable());
        } else {
            cbSelectableLayer.setSelected(false);
        }
    }

    private void updateFeatureGeometryBindingView() {
        final Object[] selectedFeatures = lstFeatures.getSelectedValues();
        lblGeometry.setEnabled(selectedFeatures.length == 1);
        cbGeometry.setEnabled(selectedFeatures.length == 1);
        if (cbGeometry.isEnabled()) {
            final RowsetFeatureDescriptor feature = (RowsetFeatureDescriptor) selectedFeatures[0];
            try {
                DbControlsDesignUtils.setSelectedItem(cbGeometry, feature.getGeometryBindingClass());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(DbMapCustomizer.this,
                        String.format("Unknown class for geometry binding %s,\nusing polygon instead.", feature.getGeometryBinding()),
                        "Bad geometry binding", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void updateProjectionParameters() {
        projectionParametersModel.setParameters(((DbMap) bean).getDesignInfo().getProjectionParameters());
    }

    private void updateMapEvent() {
        if (cbMapEventListener.isPopupVisible()) {
            cbMapEventListener.setPopupVisible(false);
        }
        DbControlsDesignUtils.updateScriptItem(cbMapEventListener, ((DbMap) bean).getDesignInfo().getMapEventListener());
    }

    @Override
    protected void updateHandlers() {
        if (bean != null && scriptHost != null && bean instanceof Component) {
            DbMapDesignInfo cInfo = (DbMapDesignInfo) designInfo;
            updateFunctions(((Component)bean).getName(), ModifyMapEventEdit.mapEventMethod, cInfo.getMapEventListener(), mapEventListenersModel);
        }
    }

    public JList getDataSourcesList() {
        return lstFeatures;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup btgPointSymbol;
    private javax.swing.JToggleButton btnCirclePoint;
    private javax.swing.JButton btnClearFillColor;
    private javax.swing.JButton btnClearHaloColor;
    private javax.swing.JButton btnClearLabelField;
    private javax.swing.JButton btnClearLabelFont;
    private javax.swing.JButton btnClearLineColor;
    private javax.swing.JButton btnClearMapEventListener;
    private javax.swing.JButton btnClearOpacity;
    private javax.swing.JButton btnClearProjection;
    private javax.swing.JButton btnClearSize;
    private javax.swing.JButton btnClearTypeField;
    private javax.swing.JButton btnClearZoomFactorField;
    private javax.swing.JToggleButton btnCrossPoint;
    private javax.swing.JButton btnSelectFillColor;
    private javax.swing.JButton btnSelectHaloColor;
    private javax.swing.JButton btnSelectLabelField;
    private javax.swing.JButton btnSelectLabelFont;
    private javax.swing.JButton btnSelectLineColor;
    private javax.swing.JButton btnSelectTypeField;
    private javax.swing.JButton btnSelectZoomFactorField;
    private javax.swing.JToggleButton btnSquarePoint;
    private javax.swing.JToggleButton btnStarPoint;
    private javax.swing.JToggleButton btnTrianglePoint;
    private javax.swing.JToggleButton btnXPoint;
    private javax.swing.JCheckBox cbActiveLayer;
    private javax.swing.JComboBox cbGeometry;
    private javax.swing.JComboBox cbMapEventListener;
    private javax.swing.JComboBox cbProjectionType;
    private javax.swing.JCheckBox cbSelectableLayer;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JLabel lblCRS;
    private javax.swing.JLabel lblFillColor;
    private javax.swing.JLabel lblFillColorExample;
    private javax.swing.JLabel lblGeoCrsWkt;
    private javax.swing.JLabel lblGeometry;
    private javax.swing.JLabel lblHaloColor;
    private javax.swing.JLabel lblHaloColorExample;
    private javax.swing.JLabel lblLabelField;
    private javax.swing.JLabel lblLabelFont;
    private javax.swing.JLabel lblLineColor;
    private javax.swing.JLabel lblLineColorExample;
    private javax.swing.JLabel lblMapEventListener;
    private javax.swing.JLabel lblOpacity;
    private javax.swing.JLabel lblPoint;
    private javax.swing.JLabel lblProjectionParameters;
    private javax.swing.JLabel lblProjectionType;
    private javax.swing.JLabel lblSize;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTypeField;
    private javax.swing.JLabel lblTypeName;
    private javax.swing.JLabel lblZoomFactorField;
    private javax.swing.JList lstFeatures;
    private javax.swing.JMenuItem miAddLayer;
    private javax.swing.JMenuItem miCopyLayer;
    private javax.swing.JMenuItem miCutLayer;
    private javax.swing.JMenuItem miMoveDownLayer;
    private javax.swing.JMenuItem miMoveUpLayer;
    private javax.swing.JMenuItem miPasteLayer;
    private javax.swing.JMenuItem miRemoveLayer;
    private javax.swing.JPopupMenu mnuLayers;
    private javax.swing.JPanel pnlFeatureType;
    private javax.swing.JPanel pnlLabelField;
    private javax.swing.JPanel pnlLabelFont;
    private javax.swing.JPanel pnlMapSettings;
    private javax.swing.JPanel pnlPointSymbol;
    private javax.swing.JPanel pnlProjection;
    private javax.swing.JPanel pnlTypeField;
    private javax.swing.JPanel pnlZoomFactorField;
    private javax.swing.JScrollPane scrollFeatures;
    private javax.swing.JSlider slOpacity;
    private javax.swing.JSplitPane spDataSources;
    private javax.swing.JScrollPane spProjectionParameters;
    private javax.swing.JSpinner spinTypeValue;
    private javax.swing.JSpinner spnOpacity;
    private javax.swing.JSpinner spnSize;
    private javax.swing.JTable tblProjectionParameters;
    private javax.swing.JTextField tfCRS;
    private javax.swing.JTextField tfGeoCrsWkt;
    private javax.swing.JTextField tfLabelField;
    private javax.swing.JTextField tfLabelFont;
    private javax.swing.JTextField tfTitle;
    private javax.swing.JTextField tfTypeField;
    private javax.swing.JTextField tfTypeName;
    private javax.swing.JTextField tfZoomFactorField;
    private javax.swing.JTabbedPane tpSettings;
    // End of variables declaration//GEN-END:variables

    private void installShortcut(JComponent component, KeyStroke keyStroke, Class<? extends Action> aClass) {
        component.getActionMap().put(aClass, getActionMap().get(aClass));
        component.getInputMap().put(keyStroke, aClass);
    }

    @Override
    protected void designInfoPropertyChanged(String aPropertyName, Object oldValue, Object newValue) {
        try {
            if (DbMapDesignInfo.PROP_PROJECTION_NAME.equals(aPropertyName)) {
                updateMapProjection();
            } else if (DbMapDesignInfo.PROP_MAP_TITLE.equals(aPropertyName)) {
                updateMapTitle();
            } else if (DbMapDesignInfo.PROP_ZOOM_FACTOR_FIELD_REF.equals(aPropertyName)) {
                updateZoomFactorField();
            } else if (DbMapDesignInfo.PROP_PROJECTION_PARAMETERS.equals(aPropertyName)) {
                updateProjectionParameters();
            } else if (DbMapDesignInfo.PROP_MAP_EVENT_LISTENER.equals(aPropertyName)) {
                updateMapEvent();
            } else if (DbMapDesignInfo.PROP_MAP_GEO_CRS_WKT.equals(aPropertyName)) {
                updateGeoCrs();
            } else if (DbMapDesignInfo.PROP_FEATURES.equals(aPropertyName)) {
                checkFeaturesSelection();
            }
            checkActionMap();
        } catch (Exception ex) {
            Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void duplicateSameParametersValues(ParameterValueGroup oldParameters, ParameterValueGroup oldDefaults, ParameterValueGroup newParameters) {
        for (final GeneralParameterValue gpv : newParameters.values()) {
            try {
                final ParameterValue<?> oldDefGpv = oldDefaults.parameter(gpv.getDescriptor().getName().getCode());
                final ParameterValue<?> oldGpv = oldParameters.parameter(gpv.getDescriptor().getName().getCode());


                if (gpv instanceof Parameter && oldDefGpv instanceof Parameter && oldGpv instanceof Parameter) {
                    final Parameter parameter = (Parameter) gpv,
                            oldDefault = (Parameter) oldDefGpv,
                            oldParameter = (Parameter) oldGpv;
                    final boolean unitEqual = parameter.getUnit().equals(oldParameter.getUnit()) && parameter.getUnit().equals(oldDefault.getUnit());
                    final boolean oldValueWasDefault = oldDefault.getValue() == null && oldParameter.getValue() == null || oldDefault.getValue() != null && oldDefault.getValue().equals(oldParameter.getValue());
                    if (unitEqual && !oldValueWasDefault) {
                        parameter.setValue(oldParameter.getValue());
                    }
                }
            } catch (ParameterNotFoundException ex) {
            }
        }
    }

    public void updateSelectedFeature() throws Exception {
        updateFeatureTypeNameView();
        updateFeatureTypeView();
        updateFeatureGeometryBindingView();
        updateFeatureCrsView();
        updateFeatureActivenessView();
        updateFeatureSelectableView();
        updateStyleView();
    }

    private void checkFeaturesSelection() {
        int[] selected = lstFeatures.getSelectedIndices();
        List<Integer> aSelected = new ArrayList<>();
        for (int i : selected) {
            if (i >= 0 && i < lstFeatures.getModel().getSize()) {
                aSelected.add(i);
            }
        }
        selected = new int[aSelected.size()];
        for (int i = 0; i
                < aSelected.size(); i++) {
            selected[i] = aSelected.get(i);
        }
        lstFeatures.setSelectedIndices(selected);
    }

    protected class RowsetFeatureDescriptorChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            try {
                updateSelectedFeature();
            } catch (Exception ex) {
                Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private class FeaturesListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                try {
                    if (selectedDescriptor != null) {
                        selectedDescriptor.getChangeSupport().removePropertyChangeListener(selectedDescriptorListener);
                    }
                    selectedDescriptor = (RowsetFeatureDescriptor) lstFeatures.getSelectedValue();
                    if (selectedDescriptor != null) {
                        selectedDescriptor.getChangeSupport().addPropertyChangeListener(selectedDescriptorListener);
                    }
                    updateSelectedFeature();
                    checkActionMap();
                } catch (Exception ex) {
                    Logger.getLogger(DbMapCustomizer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}
