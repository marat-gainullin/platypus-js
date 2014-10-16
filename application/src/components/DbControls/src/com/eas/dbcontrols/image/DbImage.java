/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.image;

import com.bearsoft.rowset.compacts.CompactBlob;
import com.eas.client.utils.scalableui.JScalableScrollPane;
import com.eas.dbcontrols.DbControl;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.dbcontrols.DbControlsUtils;
import com.eas.dbcontrols.IconCache;
import com.eas.dbcontrols.InitializingMethod;
import com.eas.dbcontrols.image.rt.AllwaysOpaqueLabel;
import com.eas.dbcontrols.image.rt.ImagePanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

/**
 *
 * @author mg
 */
public class DbImage extends DbControlPanel implements DbControl {

    protected boolean plain;
    protected JLabel designLabel;
    protected CardLayout cards = new CardLayout();
    protected JPanel cardsPanel;
    protected JLabel isFieldFilled = new AllwaysOpaqueLabel();
    protected ImagePanel imagePanel = new ImagePanel();
    protected JPanel conentPanel;
    protected JScrollPane imageScroll;
    protected Map<CompactBlob, Image> imagesCache = new HashMap<>();

    public DbImage() {
        super();
        cardsPanel = new JPanel(cards);
        cardsPanel.setInheritsPopupMenu(true);
        isFieldFilled.setInheritsPopupMenu(true);
        imagePanel.setInheritsPopupMenu(true);
    }

    public boolean isPlain() {
        return plain;
    }

    public void setPlain(boolean aValue) {
        if (plain != aValue) {
            plain = aValue;
            setupControls();
        }
    }

    @Override
    protected void initializeDesign() {
        if (kind == InitializingMethod.UNDEFINED
                && getComponentCount() == 0) {
            super.initializeDesign();
            designLabel = new JLabel(this.getClass().getSimpleName().replace("Db", "Model"), IconCache.getIcon("16x16/image.png"), SwingConstants.LEADING);
            designLabel.setOpaque(false);
            add(designLabel, BorderLayout.CENTER);
        }
    }

    @Override
    public Object getCellEditorValue() {
        return editingValue;
    }

    @Override
    public void setEditingValue(Object aValue) {
        if (aValue == null || aValue instanceof CompactBlob) {
            imagePanel.setImage(null);
            editingValue = aValue;
            setupControls();
        }
    }

    @Override
    protected void initializeRenderer() {
        if (kind != InitializingMethod.RENDERER) {
            kind = InitializingMethod.RENDERER;
            removeAll();
            setLayout(new BorderLayout());
            add(cardsPanel, BorderLayout.CENTER);
            cardsPanel.add(imagePanel, imagePanel.getClass().getSimpleName());
            cardsPanel.add(isFieldFilled, isFieldFilled.getClass().getSimpleName());
            addIconLabel();
            applyAlign();
            applyFont();
            applyBackground();
            applyForeground();
            applyEnabled();
            applyTooltip(getToolTipText());
        }
    }

    @Override
    protected void initializeEditor() {
        if (kind != InitializingMethod.EDITOR) {
            kind = InitializingMethod.EDITOR;
            removeAll();
            setLayout(new BorderLayout());
            add(cardsPanel, BorderLayout.CENTER);
            cardsPanel.add(isFieldFilled, isFieldFilled.getClass().getSimpleName());
            super.initializeEditor();
        }
    }

    @Override
    protected void setupRenderer(JTable table, int row, int column, boolean isSelected) {
        setupControls();
    }

    @Override
    protected void setupEditor(JTable table) {
        setupControls();
        super.setupEditor(table);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        imagePanel.setImage(null);
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        imagePanel.setImage(null);
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }

    @Override
    protected void applyFont() {
        if (isFieldFilled != null) {
            isFieldFilled.setFont(getFont());
        }
        if (imagePanel != null) {
            imagePanel.setFont(getFont());
        }
        if (designLabel != null) {
            designLabel.setFont(getFont());
        }
    }

    @Override
    public JComponent getFocusTargetComponent() {
        if (isFieldFilled != null) {
            return isFieldFilled;
        } else if (imagePanel != null) {
            return imagePanel;
        }
        return null;
    }

    @Override
    protected void applyAlign() {
        if (isFieldFilled != null) {
            isFieldFilled.setHorizontalAlignment(align);
        }
        if (imagePanel != null) {
            imagePanel.setHorizontalAlign(align);
        }
        super.applyAlign();
    }

    @Override
    protected void applyTooltip(String aText) {
        if (isFieldFilled != null) {
            isFieldFilled.setToolTipText(aText);
        }
        if (imagePanel != null) {
            imagePanel.setToolTipText(aText);
        }
    }

    @Override
    protected void applyEnabled() {
        if (isFieldFilled != null) {
            isFieldFilled.setEnabled(isEnabled());
        }
        if (imagePanel != null) {
            imagePanel.setEnabled(isEnabled());
        }
        super.applyEnabled();
    }

    @Override
    protected void applyBackground() {
        if (isFieldFilled != null) {
            isFieldFilled.setBackground(getBackground());
        }
        if (imagePanel != null) {
            imagePanel.setBackground(getBackground());
        }
        if (conentPanel != null) {
            conentPanel.setBackground(getBackground());
        }
        if (imageScroll != null) {
            imageScroll.setBackground(getBackground());
        }
    }

    @Override
    protected void applyCursor() {
        if (isFieldFilled != null) {
            isFieldFilled.setCursor(getCursor());
        }
        if (imagePanel != null) {
            imagePanel.setCursor(getCursor());
        }
        if (conentPanel != null) {
            conentPanel.setCursor(getCursor());
        }
        if (imageScroll != null) {
            imageScroll.setCursor(getCursor());
        }
    }

    @Override
    protected void applyOpaque() {
        if (isFieldFilled != null) {
            isFieldFilled.setOpaque(isOpaque());
        }
        if (imagePanel != null) {
            imagePanel.setOpaque(isOpaque());
        }
        if (conentPanel != null) {
            conentPanel.setOpaque(isOpaque());
        }
        if (imageScroll != null) {
            imageScroll.setOpaque(isOpaque());
        }
    }

    @Override
    protected void applyForeground() {
        if (isFieldFilled != null) {
            isFieldFilled.setForeground(getForeground());
        }
        if (imagePanel != null) {
            imagePanel.setForeground(getForeground());
        }
    }

    @Override
    protected void applyEditable2Field() {
    }

    private void setupControls() {
        if (editingValue == null) {
            isFieldFilled.setText(DbControlsUtils.getLocalizedString("notFilled"));
            isFieldFilled.setForeground(Color.lightGray);
            cards.show(cardsPanel, isFieldFilled.getClass().getSimpleName());
        } else {
            try2ParseImage();
            if (imagePanel.getImage() != null
                    && imagePanel.getImage().getHeight(null) != -1
                    && imagePanel.getImage().getWidth(null) != -1) {
                if (kind == InitializingMethod.EDITOR) {
                    EventQueue.invokeLater(() -> {
                        Dimension d = getSize();
                        if (extraTools.isVisible()) {
                            Dimension sd = extraTools.getSize();
                            d.width -= sd.width * 2;
                        }
                        setupScalable(d);
                        cards.show(cardsPanel, imagePanel.getClass().getSimpleName());
                        applyBackground();
                    });
                } else {
                    cards.show(cardsPanel, imagePanel.getClass().getSimpleName());
                }

            } else {
                isFieldFilled.setText(DbControlsUtils.getLocalizedString("Filled"));
                isFieldFilled.setForeground(Color.lightGray);
                cards.show(cardsPanel, isFieldFilled.getClass().getSimpleName());
            }
        }
    }

    private void setupScalable(Dimension aSize) {
        if (imageScroll != null) {
            cardsPanel.remove(imageScroll);
        }
        if (isPlain()) {
            imageScroll = new JScrollPane();
            if (imagePanel.getImage() != null) {
                aSize.height = imagePanel.getImage().getHeight(null);
                aSize.width = imagePanel.getImage().getWidth(null);
            }
        } else {
            imageScroll = new JScalableScrollPane();
        }
        conentPanel = new JPanel(null);
        conentPanel.add(imagePanel);
        imagePanel.setLocation(0, 0);
        imagePanel.setSize(aSize);
        conentPanel.setPreferredSize(aSize);
        imageScroll.setViewportView(conentPanel);
        cardsPanel.add(imageScroll, imagePanel.getClass().getSimpleName());
    }

    public void fit() {
        if (imageScroll instanceof JScalableScrollPane && ((JScalableScrollPane) imageScroll).getScalablePanel() != null) {
            ((JScalableScrollPane) imageScroll).getScalablePanel().fit();
        }
    }
    /*
     public static final long serialVersionUID = 6587354L;
     private void writeObject(java.io.ObjectOutputStream out) throws IOException {
     writeExternal(out);
     }

     private void readObject(java.io.ObjectInputStream in) throws Exception {
     readExternal(in);
     }

     private void readObjectNoData() throws ObjectStreamException {
     }

     public void writeExternal(ObjectOutput out) throws IOException {
     DbControlsUtils.writeControl(this, out);
     }

     public void readExternal(ObjectInput in) throws IOException {
     try {
     DbControlsUtils.readControl(this, in);
     } catch (Exception ex) {
     throw new IOException(ex);
     }
     }
     */

    private void try2ParseImage() {
        if (editingValue != null && editingValue instanceof CompactBlob && imagePanel.getImage() == null) {
            CompactBlob bValue = (CompactBlob) editingValue;
            Image image = imagesCache.get(bValue);
            if (image == null) {
                ImageIcon lIcon = new ImageIcon(bValue.getData());
                image = lIcon.getImage();
                imagesCache.put(bValue, image);
            }
            imagePanel.setImage(image);
        }
    }
}
