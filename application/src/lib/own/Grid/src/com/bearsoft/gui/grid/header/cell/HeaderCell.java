/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.gui.grid.header.cell;

import com.bearsoft.gui.grid.IconCache;
import com.bearsoft.gui.grid.header.GridColumnsNode;
import com.bearsoft.gui.grid.header.MultiLevelHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.View;

/**
 *
 * @author mg
 */
public class HeaderCell extends JEditorPane {

    public static final Color defaultEdgeColor = new Color(118, 187, 246);
    public static final Color defaultBottomColor = new Color(248, 169, 0);
    public static final Color defaultBottomLightColor = new Color(252, 194, 71);
    private static final int SORTING_INDEX_TEXT_INSET = 2;
    private static final Font SORTING_ORDER_FONT = new Font(Font.SERIF, 0, 9);
    public static final String HTML_TEMPLATE = ""
            + "<html>"
            + "  <head>"
            + "  </head>"
            + "  <body>"
            + "    <p style=\"margin-top:0;font-family:%s;font-size:%dpt;font-weight:%s\" align=center>"
            + "%s"
            + "    </p>"
            + "  </body>"
            + "</html>";
    protected MultiLevelHeader header;
    protected GridColumnsNode colGroup;
    protected JPanel samplePanel = new JPanel();
    protected boolean rolledover = false;
    protected boolean leftRolledover = false;
    protected boolean rightRolledover = false;
    protected Color uiEdgeColor;
    protected Color rolledOverOutlineColor = new Color(168, 216, 235);
    protected Color rolledOverFillColor = new Color(231, 239, 243);
    protected PropertyChangeListener groupListener = (PropertyChangeEvent evt) -> {
        if ("background".equals(evt.getPropertyName())) {
            setBackground((Color) evt.getNewValue());
        } else if ("foreground".equals(evt.getPropertyName())) {
            setForeground((Color) evt.getNewValue());
        } else if ("font".equals(evt.getPropertyName())) {
            setFont((Font) evt.getNewValue());
        } else if ("title".equals(evt.getPropertyName())
                && (evt.getNewValue() == null || evt.getNewValue() instanceof String)) {
            applyTitle();
        } else if ("width".equals(evt.getPropertyName()) || "preferredWidth".equals(evt.getPropertyName())
                || "maxWidth".equals(evt.getPropertyName()) || "minWidth".equals(evt.getPropertyName())) {
            invalidate();
        }
    };

    public HeaderCell(GridColumnsNode aColGroup, MultiLevelHeader aHeader) {
        super();
        setEditable(false);
        setFocusable(false);
        setContentType("text/html");
        header = aHeader;
        setColGroup(aColGroup);
        setBorder(createCellRaisedBorder());
        setAutoscrolls(false);
        CellHighlighter highlighter = new CellHighlighter(this);
        addMouseListener(highlighter);
        addMouseMotionListener(highlighter);
        CellResizer resizer = new CellResizer(this, aHeader);
        addMouseListener(resizer);
        addMouseMotionListener(resizer);
        CellMover mover = new CellMover(this);
        addMouseListener(mover);
        addMouseMotionListener(mover);
        CellSortingToggler toggler = new CellSortingToggler(this);
        addMouseListener(toggler);
        setOpaque(false);
        updateUIData();
        setDoubleBuffered(true);
        setOpaque(false);
        putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
    }

    protected void applyTitle() {
        if (colGroup != null) {
            String title = colGroup.getTitle();
            setText(String.format(HTML_TEMPLATE, getFont().getFamily(), getFont().getSize(), getFont().isBold() ? "bold" : "normal", title != null ? title : ""));
        }
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        applyTitle();
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
    }
    /*
     public static java.awt.Font toNativeFont(com.eas.gui.Font aFont) {
     return new java.awt.Font(aFont.getFamily(), CascadedStyle.fontStyleToNativeFontStyle(aFont.getStyle()), aFont.getSize());
     }
     */

    protected final void updateUIData() {
        uiEdgeColor = UIManager.getColor("InternalFrame.activeTitleGradient");
    }

    public MultiLevelHeader getHeader() {
        return header;
    }

    @Override
    public Dimension getMinimumSize() {
        if (colGroup.isLeaf()) {
            Dimension d = super.getMinimumSize();
            View view = getUI().getRootView(this);
            if (view != null) {
                d.width = Math.round(view.getMinimumSpan(View.X_AXIS));
            }
            return new Dimension(Math.max(d.width, colGroup.getMinWidth()), d.height);
        } else {
            Dimension d = super.getPreferredSize();
            return new Dimension(0, d.height);
        }
    }

    @Override
    public Dimension getMaximumSize() {
        if (colGroup.isLeaf()) {
            Dimension d = super.getMaximumSize();
            return new Dimension(Math.min(d.width, colGroup.getMaxWidth()), d.height);
        } else {
            Dimension d = super.getPreferredSize();
            return new Dimension(Integer.MAX_VALUE, d.height);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (colGroup.isLeaf()) {
            int colGroupPWidth = colGroup.getWidth();
            setSize(colGroupPWidth, Integer.MAX_VALUE / 2);
            Dimension d = super.getPreferredSize();
            return new Dimension(colGroupPWidth, d.height);
        } else {
            Dimension d = super.getPreferredSize();
            return new Dimension(0, d.height);
        }
    }

    private int checkSortOrder() {
        if (colGroup.isLeaf() && colGroup.getTableColumn() != null) {
            int modelIndex = colGroup.getTableColumn().getModelIndex();
            List<? extends RowSorter.SortKey> sortKeys = header.getRowSorter().getSortKeys();
            for (int i = 0; i < sortKeys.size(); i++) {
                RowSorter.SortKey sk = sortKeys.get(i);
                if (sk.getColumn() == modelIndex) {
                    return i;
                }
            }
        }
        return -1;
    }

    private Border createCellRaisedBorder() {
        MatteBorder innerBorder = new MatteBorder(1, 1, 0, 0, getBackground().brighter());
        MatteBorder outerBorder = new MatteBorder(0, 0, 1, 1, getBackground().darker());
        CompoundBorder border = new CompoundBorder(innerBorder, outerBorder);
        return border;
    }

    public Border createCellLoweredBorder() {
        MatteBorder innerBorder = new MatteBorder(0, 0, 1, 1, getBackground().brighter());
        MatteBorder outerBorder = new MatteBorder(1, 1, 0, 0, getBackground().darker());
        CompoundBorder border = new CompoundBorder(innerBorder, outerBorder);
        return border;
    }

    @Override
    public void updateUI() {
        super.updateUI();
        if (samplePanel != null) {
            samplePanel.updateUI();
            setBackground(samplePanel.getBackground());
            updateUIData();
        }
    }

    public GridColumnsNode getColGroup() {
        return colGroup;
    }

    public void setColGroup(GridColumnsNode aValue) {
        if (colGroup != aValue) {
            if (colGroup != null) {
                colGroup.getChangeSupport().removePropertyChangeListener(groupListener);
            }
            colGroup = aValue;
            if (colGroup != null) {
                colGroup.getChangeSupport().addPropertyChangeListener(groupListener);
            }
            applyTitle();
        }
    }

    public boolean isRolledover() {
        return rolledover;
    }

    public void setRolledover(boolean aValue) {
        rolledover = aValue;
        repaint();
    }

    public boolean isLeftRolledover() {
        return leftRolledover;
    }

    public void setLeftRolledover(boolean aValue) {
        leftRolledover = aValue;
        repaint();
    }

    public boolean isRightRolledover() {
        return rightRolledover;
    }

    public void setRightRolledover(boolean aValue) {
        rightRolledover = aValue;
        repaint();
    }

    @Override
    protected void paintBorder(Graphics g) {
        super.paintBorder(g);
        if (leftRolledover || rightRolledover) {
            Dimension d = getSize();
            Color edgeColor = uiEdgeColor != null ? uiEdgeColor : defaultEdgeColor;
            Color oldEdgeColor = g.getColor();
            try {
                g.setColor(rolledOverOutlineColor);
                g.drawRect(0, 0, d.width, d.height);
                g.setColor(edgeColor.darker());
                if (leftRolledover) {
                    g.fillRect(1, 0, Math.round(MultiLevelHeader.PICK_MARGIN_SIZE), d.height);
                }
                if (rightRolledover) {
                    g.fillRect(d.width - Math.round(MultiLevelHeader.PICK_MARGIN_SIZE) - 2, 0, Math.round(MultiLevelHeader.PICK_MARGIN_SIZE), d.height);
                }
            } finally {
                g.setColor(oldEdgeColor);
            }
        }
        paintSorting(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g.create();
            Dimension d = getSize();
            if (leftRolledover || rightRolledover) {
                g2d.setColor(rolledOverFillColor);
                g2d.fillRect(0, 0, d.width, d.height);
            }
            if (getColGroup().isLeaf() && !rolledover && !leftRolledover && !rightRolledover) {
                Color bottomColor = getBackground();
                Paint gradient2 = new GradientPaint(new Point2D.Float(0, d.height - 5), bottomColor, new Point2D.Float(0, d.height + 10), bottomColor.darker());
                Paint oldPaint = g2d.getPaint();
                try {
                    g2d.setPaint(gradient2);
                    g2d.fillRect(0, d.height - 3, d.width, d.height);
                } finally {
                    g2d.setPaint(oldPaint);
                }
            }
            if (rolledover && !leftRolledover && !rightRolledover) {
                Color oldColor = g2d.getColor();
                try {
                    g2d.setColor(defaultBottomColor);
                    g2d.fillRect(0, d.height - 3, d.width, d.height);
                    g2d.setColor(defaultBottomLightColor);
                    g2d.fillRect(0, d.height - 2, d.width, d.height);
                } finally {
                    g2d.setColor(oldColor);
                }
            }
        }
        // The folowing line replaces super.paintComponenet(g) call
        // because some LAFs fill the background and despite opaque flag!
        getUI().paint(g, this);
    }

    private void paintSorting(Graphics g) {
        if (header.getRowSorter() != null) {
            int sortIndex = checkSortOrder();
            if (colGroup.isLeaf() && sortIndex != -1) {
                Rectangle bounds = getBounds();
                bounds.x = 0;
                bounds.y = 0;
                int sX = -1;
                int sH = -1;
                SortOrder sOrder = ((RowSorter.SortKey) header.getRowSorter().getSortKeys().get(sortIndex)).getSortOrder();
                if (sOrder != SortOrder.UNSORTED) {
                    String sIndex = String.valueOf(sortIndex + 1);
                    Rectangle2D sOrderBounds = g.getFontMetrics().getStringBounds(sIndex, g);
                    Font oldFont = g.getFont();
                    Color oldCol = g.getColor();
                    try {
                        g.setColor(Color.gray);
                        g.setFont(SORTING_ORDER_FONT);
                        sX = bounds.x + (int) (bounds.width - sOrderBounds.getWidth() - SORTING_INDEX_TEXT_INSET);
                        sH = (int) sOrderBounds.getHeight();
                        g.drawString(sIndex, sX, bounds.y + sH - SORTING_INDEX_TEXT_INSET);
                    } finally {
                        g.setFont(oldFont);
                        g.setColor(oldCol);
                    }
                }
                if (sOrder == SortOrder.ASCENDING) {
                    g.drawImage(IconCache.getImage("ascending.png"), sX - 12, bounds.y - SORTING_INDEX_TEXT_INSET + 2, null);
                } else if (sOrder == SortOrder.DESCENDING) {
                    g.drawImage(IconCache.getImage("descending.png"), sX - 12, bounds.y - SORTING_INDEX_TEXT_INSET + 2, null);
                }
            }
        }
    }

    public static JScrollPane getFirstScrollPane(Component aComp) {
        Component lParent = aComp;
        while (lParent != null && !(lParent instanceof JScrollPane)) {
            lParent = lParent.getParent();
        }
        if (lParent != null && lParent instanceof JScrollPane) {
            return (JScrollPane) lParent;
        }
        return null;
    }

    public static boolean isValidCellBoundary(Component aTarget) {
        JScrollPane scroll = getFirstScrollPane(aTarget);
        if (scroll != null && (scroll.getHorizontalScrollBar() == null || !scroll.getHorizontalScrollBar().isVisible())) {
            Point rightOnScroll = SwingUtilities.convertPoint(aTarget, new Point(aTarget.getWidth(), 1), scroll);
            int scrollRightBoundary = (scroll.getWidth() - (scroll.getInsets() != null ? scroll.getInsets().right : 0));
            return (scrollRightBoundary - rightOnScroll.x) > MultiLevelHeader.PICK_MARGIN_SIZE;
        }
        return true;
    }
}
