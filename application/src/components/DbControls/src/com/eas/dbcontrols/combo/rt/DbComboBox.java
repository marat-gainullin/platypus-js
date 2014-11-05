/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.dbcontrols.combo.rt;

import com.eas.client.model.application.ApplicationModel;
import com.eas.dbcontrols.DbControl;
import com.eas.dbcontrols.IconCache;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.Serializable;
import javax.accessibility.*;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.MutableComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.event.*;
import javax.swing.plaf.ComboBoxUI;

public class DbComboBox<T> extends JPanel
        implements ItemSelectable, ListDataListener, ActionListener, Accessible {

    /**
     * This protected field is implementation specific. Do not access directly
     * or override. Use the accessor methods instead.
     *
     * @see #getModel
     * @see #setModel
     */
    protected ComboBoxModel<T> dataModel;
    /**
     * This protected field is implementation specific. Do not access directly
     * or override. Use the accessor methods instead.
     *
     * @see #getRenderer
     * @see #setRenderer
     */
    protected ListCellRenderer<T> renderer;
    /**
     * This protected field is implementation specific. Do not access directly
     * or override. Use the accessor methods instead.
     *
     * @see #getEditor
     * @see #setEditor
     */
    protected ComboBoxEditor editor;
    /**
     * This protected field is implementation specific. Do not access directly
     * or override. Use the accessor methods instead.
     *
     * @see #getMaximumRowCount
     * @see #setMaximumRowCount
     */
    protected int maximumRowCount = 8;
    /**
     * This protected field is implementation specific. Do not access directly
     * or override. Use the accessor methods instead.
     *
     * @see #isEditable
     * @see #setEditable
     */
    protected boolean isEditable = false;
    /**
     * This protected field is implementation specific. Do not access directly
     * or override. Use the accessor methods instead.
     *
     * @see #setKeySelectionManager
     * @see #getKeySelectionManager
     */
    protected KeySelectionManager<T> keySelectionManager = null;
    /**
     * This protected field is implementation specific. Do not access directly
     * or override. Use the accessor methods instead.
     *
     * @see #setActionCommand
     * @see #getActionCommand
     */
    protected String actionCommand = "comboBoxChanged";
    /**
     * This protected field is implementation specific. Do not access directly
     * or override. Use the accessor methods instead.
     *
     * @see #setLightWeightPopupEnabled
     * @see #isLightWeightPopupEnabled
     */
    protected boolean lightWeightPopupEnabled = JPopupMenu.getDefaultLightWeightPopupEnabled();
    /**
     * This protected field is implementation specific. Do not access directly
     * or override.
     */
    protected Object selectedItemReminder = null;
    private Object prototypeDisplayValue;
    // Flag to ensure that infinite loops do not occur with ActionEvents.
    private boolean firingActionEvent = false;
    // Flag to ensure the we don't get multiple ActionEvents on item selection.
    private boolean selectingItem = false;
    protected JTextField txt4Selected = new JTextField();
    protected DbComboPopup<T> popup;
    protected JButton dropdownButton = new JButton();
    protected JToolBar tools = new JToolBar();
    protected ApplicationModel<?, ?> model;

    protected class ShowPopupAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            showPopup();
        }
    }

    protected class DropdownPopupMenuListener implements PopupMenuListener {

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            requestFocus();
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            requestFocus();
        }
    }

    protected class ComboBoxPopuper extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (!isEditable && isEnabled()) {
                showPopup();
            }
        }
    }

    public DbComboBox(ApplicationModel<?, ?> aModel, ComboBoxModel<T> aComboModel, boolean aBorderless) {
        super(new BorderLayout(), true);
        setModel(aComboModel);
        init();
        model = aModel;
        popup = new DbComboPopup<>(this);
        dropdownButton.setPreferredSize(new Dimension(DbControl.EXTRA_BUTTON_WIDTH, DbControl.EXTRA_BUTTON_WIDTH));
        dropdownButton.setAction(new ShowPopupAction());
        dropdownButton.setIcon(IconCache.getIcon("16x16/dropDown.png"));
        tools.setBorder(null);
        tools.add(dropdownButton);
        add(tools, BorderLayout.EAST);
        popup.addPopupMenuListener(new DropdownPopupMenuListener());
        addMouseListener(new ComboBoxPopuper());
    }

    @Override
    public Color getBackground() {
        if (isEnabled()) {
            return super.getBackground();
        } else {
            if (tools != null) {
                return tools.getBackground();
            } else {
                return super.getBackground();
            }
        }
    }

    public void removeEditor() {
        if (editor != null) {
            Component comp = editor.getEditorComponent();
            if (comp != null) {
                remove(comp);
            }
        }
    }

    public void addEditor() {
        if (isEditable && editor != null) {
            Component comp = editor.getEditorComponent();
            if (comp != null) {
                add(comp, BorderLayout.CENTER);
            }
        }
    }

    @Override
    public void setFont(Font font) {
        super.setFont(font);
        if (popup != null) {
            popup.setFont(font);
        }
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (popup != null) {
            popup.setForeground(fg);
        }
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (popup != null) {
            popup.setBackground(bg);
        }
    }

    /**
     * Sets the editor used to paint and edit the selected item in the
     * <code>JComboBox</code> field. The editor is used only if the receiving
     * <code>JComboBox</code> is editable. If not editable, the combo box uses
     * the renderer to paint the selected item.
     *
     * @param anEditor the <code>ComboBoxEditor</code> that displays the
     * selected item
     * @see #setRenderer
     * @beaninfo bound: true expert: true description: The editor that combo box
     * uses to edit the current value
     */
    public void setEditor(ComboBoxEditor anEditor) {
        ComboBoxEditor oldEditor = editor;

        removeEditor();
        if (editor != null) {
            editor.removeActionListener(this);
        }
        editor = anEditor;
        if (editor != null) {
            editor.addActionListener(this);
        }
        addEditor();
        firePropertyChange("editor", oldEditor, editor);
    }

    private void init() {
        installAncestorListener();
        updateUI();
    }

    protected void installAncestorListener() {
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                hidePopup();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                hidePopup();
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
                if (event.getSource() != DbComboBox.this) {
                    hidePopup();
                }
            }
        });
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui the <code>ComboBoxUI</code> L&F object
     * @see UIDefaults#getUI
     *
     * @beaninfo bound: true hidden: true attribute: visualUpdate true
     * description: The UI object that implements the Component's LookAndFeel.
     */
    public void setUI(ComboBoxUI ui) {
        super.setUI(ui);
    }

    /**
     * Resets the UI property to a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    @Override
    public void updateUI() {
        super.updateUI();

        ListCellRenderer<T> lrenderer = getRenderer();
        if (lrenderer instanceof Component) {
            SwingUtilities.updateComponentTreeUI((Component) renderer);
        }
        txt4Selected = new JTextField();
    }

    /**
     * Sets the data model that the
     * <code>JComboBox</code> uses to obtain the list of items.
     *
     * @param aModel the <code>ComboBoxModel</code> that provides the displayed
     * list of items
     *
     * @beaninfo bound: true description: Model that the combo box uses to get
     * data to display.
     */
    public void setModel(ComboBoxModel<T> aModel) {
        ComboBoxModel<T> oldModel = dataModel;
        if (oldModel != null) {
            oldModel.removeListDataListener(this);
        }
        dataModel = aModel;
        dataModel.addListDataListener(this);

        // set the current selected item.
        selectedItemReminder = dataModel.getSelectedItem();

        firePropertyChange("model", oldModel, dataModel);
    }

    /**
     * Returns the data model currently used by the
     * <code>JComboBox</code>.
     *
     * @return the <code>ComboBoxModel</code> that provides the displayed list
     * of items
     */
    public ComboBoxModel<T> getModel() {
        return dataModel;
    }

    /*
     * Properties
     */
    /**
     * Sets the
     * <code>lightWeightPopupEnabled</code> property, which provides a hint as
     * to whether or not a lightweight
     * <code>Component</code> should be used to contain the
     * <code>JComboBox</code>, versus a heavyweight
     * <code>Component</code> such as a
     * <code>Panel</code> or a
     * <code>Window</code>. The decision of lightweight versus heavyweight is
     * ultimately up to the
     * <code>JComboBox</code>. Lightweight windows are more efficient than
     * heavyweight windows, but lightweight and heavyweight components do not
     * mix well in a GUI. If your application mixes lightweight and heavyweight
     * components, you should disable lightweight popups. The default value for
     * the
     * <code>lightWeightPopupEnabled</code> property is
     * <code>true</code>, unless otherwise specified by the look and feel. Some
     * look and feels always use heavyweight popups, no matter what the value of
     * this property. <p> See the article <a
     * href="http://java.sun.com/products/jfc/tsc/articles/mixing/index.html">Mixing
     * Heavy and Light Components</a> on <a
     * href="http://java.sun.com/products/jfc/tsc"> <em>The Swing
     * Connection</em></a> This method fires a property changed event.
     *
     * @param aFlag if <code>true</code>, lightweight popups are desired
     *
     * @beaninfo bound: true expert: true description: Set to <code>false</code>
     * to require heavyweight popups.
     */
    public void setLightWeightPopupEnabled(boolean aFlag) {
        boolean oldFlag = lightWeightPopupEnabled;
        lightWeightPopupEnabled = aFlag;
        firePropertyChange("lightWeightPopupEnabled", oldFlag, lightWeightPopupEnabled);
    }

    /**
     * Gets the value of the
     * <code>lightWeightPopupEnabled</code> property.
     *
     * @return the value of the <code>lightWeightPopupEnabled</code> property
     * @see #setLightWeightPopupEnabled
     */
    public boolean isLightWeightPopupEnabled() {
        return lightWeightPopupEnabled;
    }

    /**
     * Determines whether the
     * <code>JComboBox</code> field is editable. An editable
     * <code>JComboBox</code> allows the user to type into the field or selected
     * an item from the list to initialize the field, after which it can be
     * edited. (The editing affects only the field, the list item remains
     * intact.) A non editable
     * <code>JComboBox</code> displays the selected item in the field, but the
     * selection cannot be modified.
     *
     * @param aFlag a boolean value, where true indicates that the field is
     * editable
     *
     * @beaninfo bound: true preferred: true description: If true, the user can
     * type a new value in the combo box.
     */
    public void setEditable(boolean aFlag) {
        boolean oldFlag = isEditable;
        isEditable = aFlag;
        firePropertyChange("editable", oldFlag, isEditable);
    }

    /**
     * Returns true if the
     * <code>JComboBox</code> is editable. By default, a combo box is not
     * editable.
     *
     * @return true if the <code>JComboBox</code> is editable, else false
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * Sets the maximum number of rows the
     * <code>JComboBox</code> displays. If the number of objects in the model is
     * greater than count, the combo box uses a scrollbar.
     *
     * @param count an integer specifying the maximum number of items to display
     * in the list before using a scrollbar
     * @beaninfo bound: true preferred: true description: The maximum number of
     * rows the popup should have
     */
    public void setMaximumRowCount(int count) {
        int oldCount = maximumRowCount;
        maximumRowCount = count;
        firePropertyChange("maximumRowCount", oldCount, maximumRowCount);
    }

    /**
     * Returns the maximum number of items the combo box can display without a
     * scrollbar
     *
     * @return an integer specifying the maximum number of items that are
     * displayed in the list before using a scrollbar
     */
    public int getMaximumRowCount() {
        return maximumRowCount;
    }

    /**
     * Sets the renderer that paints the list items and the item selected from
     * the list in the JComboBox field. The renderer is used if the JComboBox is
     * not editable. If it is editable, the editor is used to render and edit
     * the selected item. <p> The default renderer displays a string or an icon.
     * Other renderers can handle graphic images and composite items. <p> To
     * display the selected item,
     * <code>aRenderer.getListCellRendererComponent</code> is called, passing
     * the list object and an index of -1.
     *
     * @param aRenderer the <code>ListCellRenderer</code> that displays the
     * selected item
     * @see #setEditor
     * @beaninfo bound: true expert: true description: The renderer that paints
     * the item selected in the list.
     */
    public void setRenderer(ListCellRenderer<T> aRenderer) {
        ListCellRenderer<T> oldRenderer = renderer;
        renderer = aRenderer;
        firePropertyChange("renderer", oldRenderer, renderer);
        invalidate();
    }

    /**
     * Returns the renderer used to display the selected item in the
     * <code>JComboBox</code> field.
     *
     * @return the <code>ListCellRenderer</code> that displays the selected
     * item.
     */
    public ListCellRenderer<T> getRenderer() {
        return renderer;
    }

    /**
     * Returns the editor used to paint and edit the selected item in the
     * <code>JComboBox</code> field.
     *
     * @return the <code>ComboBoxEditor</code> that displays the selected item
     */
    public ComboBoxEditor getEditor() {
        return editor;
    }

    //
    // Selection
    //
    /**
     * Sets the selected item in the combo box display area to the object in the
     * argument. If
     * <code>anObject</code> is in the list, the display area shows
     * <code>anObject</code> selected. <p> If
     * <code>anObject</code> is <i>not</i> in the list and the combo box is
     * uneditable, it will not change the current selection. For editable combo
     * boxes, the selection will change to
     * <code>anObject</code>. <p> If this constitutes a change in the selected
     * item,
     * <code>ItemListener</code>s added to the combo box will be notified with
     * one or two
     * <code>ItemEvent</code>s. If there is a current selected item, an
     * <code>ItemEvent</code> will be fired and the state change will be
     * <code>ItemEvent.DESELECTED</code>. If
     * <code>anObject</code> is in the list and is not currently selected then
     * an
     * <code>ItemEvent</code> will be fired and the state change will be
     * <code>ItemEvent.SELECTED</code>. <p>
     * <code>ActionListener</code>s added to the combo box will be notified with
     * an
     * <code>ActionEvent</code> when this method is called.
     *
     * @param anObject the list object to select; use <code>null</code> to clear
     * the selection
     * @beaninfo preferred: true description: Sets the selected item in the
     * JComboBox.
     */
    public void setSelectedItem(Object anObject) {
        Object oldSelection = selectedItemReminder;
        Object objectToSelect = anObject;
        if (oldSelection == null || !oldSelection.equals(anObject)) {

            if (anObject != null && !isEditable()) {
                // For non editable combo boxes, an invalid selection
                // will be rejected.
                boolean found = false;
                for (int i = 0; i < dataModel.getSize(); i++) {
                    Object element = dataModel.getElementAt(i);
                    if (anObject.equals(element)) {
                        found = true;
                        objectToSelect = element;
                        break;
                    }
                }
                if (!found) {
                    return;
                }
            }

            // Must toggle the state of this flag since this method
            // call may result in ListDataEvents being fired.
            selectingItem = true;
            dataModel.setSelectedItem(objectToSelect);
            selectingItem = false;

            if (selectedItemReminder != dataModel.getSelectedItem()) {
                // in case a users implementation of ComboBoxModel
                // doesn't fire a ListDataEvent when the selection
                // changes.
                selectedItemChanged();
            }
        }
        fireActionEvent();
    }

    /**
     * Returns the current selected item. <p> If the combo box is editable, then
     * this value may not have been added to the combo box with
     * <code>addItem</code>,
     * <code>insertItemAt</code> or the data constructors.
     *
     * @return the current selected Object
     * @see #setSelectedItem
     */
    public Object getSelectedItem() {
        return dataModel.getSelectedItem();
    }

    /**
     * Selects the item at index
     * <code>anIndex</code>.
     *
     * @param anIndex an integer specifying the list item to select, where 0
     * specifies the first item in the list and -1 indicates no selection
     * @exception IllegalArgumentException if <code>anIndex</code> < -1 or
     * <code>anIndex</code> is greater than or equal to size
     * @beaninfo preferred: true description: The item at index is selected.
     */
    public void setSelectedIndex(int anIndex) {
        int size = dataModel.getSize();

        if (anIndex == -1) {
            setSelectedItem(null);
        } else if (anIndex < -1 || anIndex >= size) {
            throw new IllegalArgumentException("setSelectedIndex: " + anIndex + " out of bounds");
        } else {
            setSelectedItem(dataModel.getElementAt(anIndex));
        }
    }

    /**
     * Returns the first item in the list that matches the given item. The
     * result is not always defined if the
     * <code>JComboBox</code> allows selected items that are not in the list.
     * Returns -1 if there is no selected item or if the user specified an item
     * which is not in the list.
     *
     * @return an integer specifying the currently selected list item, where 0
     * specifies the first item in the list; or -1 if no item is selected or if
     * the currently selected item is not in the list
     */
    public int getSelectedIndex() {
        Object sObject = dataModel.getSelectedItem();
        int i, c;
        Object obj;

        for (i = 0, c = dataModel.getSize(); i < c; i++) {
            obj = dataModel.getElementAt(i);
            if (obj == sObject) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the "prototypical display" value - an Object used for the
     * calculation of the display height and width.
     *
     * @return the value of the <code>prototypeDisplayValue</code> property
     * @see #setPrototypeDisplayValue
     * @since 1.4
     */
    public Object getPrototypeDisplayValue() {
        return prototypeDisplayValue;
    }

    /**
     * Sets the prototype display value used to calculate the size of the
     * display for the UI portion. <p> If a prototype display value is
     * specified, the preferred size of the combo box is calculated by
     * configuring the renderer with the prototype display value and obtaining
     * its preferred size. Specifying the preferred display value is often
     * useful when the combo box will be displaying large amounts of data. If no
     * prototype display value has been specified, the renderer must be
     * configured for each value from the model and its preferred size obtained,
     * which can be relatively expensive.
     *
     * @param prototypeDisplayValue
     * @see #getPrototypeDisplayValue
     * @since 1.4
     * @beaninfo bound: true attribute: visualUpdate true description: The
     * display prototype value, used to compute display width and height.
     */
    public void setPrototypeDisplayValue(Object prototypeDisplayValue) {
        Object oldValue = this.prototypeDisplayValue;
        this.prototypeDisplayValue = prototypeDisplayValue;
        firePropertyChange("prototypeDisplayValue", oldValue, prototypeDisplayValue);
    }

    /**
     * Adds an item to the item list. This method works only if the
     * <code>JComboBox</code> uses a mutable data model. <p>
     * <strong>Warning:</strong> Focus and keyboard navigation problems may
     * arise if you add duplicate String objects. A workaround is to add new
     * objects instead of String objects and make sure that the toString()
     * method is defined. For example:
     * <pre>
     *   comboBox.addItem(makeObj("Item 1"));
     *   comboBox.addItem(makeObj("Item 1"));
     *   ...
     *   private Object makeObj(final String item)  {
     *     return new Object() { public String toString() { return item; } };
     *   }
     * </pre>
     *
     * @param anObject the Object to add to the list
     * @see MutableComboBoxModel
     */
    public void addItem(T anObject) {
        checkMutableComboBoxModel();
        ((MutableComboBoxModel<T>) dataModel).addElement(anObject);
    }

    /**
     * Inserts an item into the item list at a given index. This method works
     * only if the
     * <code>JComboBox</code> uses a mutable data model.
     *
     * @param anObject the <code>Object</code> to add to the list
     * @param index an integer specifying the position at which to add the item
     * @see MutableComboBoxModel
     */
    public void insertItemAt(T anObject, int index) {
        checkMutableComboBoxModel();
        ((MutableComboBoxModel<T>) dataModel).insertElementAt(anObject, index);
    }

    /**
     * Removes an item from the item list. This method works only if the
     * <code>JComboBox</code> uses a mutable data model.
     *
     * @param anObject the object to remove from the item list
     * @see MutableComboBoxModel
     */
    public void removeItem(Object anObject) {
        checkMutableComboBoxModel();
        ((MutableComboBoxModel<T>) dataModel).removeElement(anObject);
    }

    /**
     * Removes the item at
     * <code>anIndex</code> This method works only if the
     * <code>JComboBox</code> uses a mutable data model.
     *
     * @param anIndex an int specifying the index of the item to remove, where 0
     * indicates the first item in the list
     * @see MutableComboBoxModel
     */
    public void removeItemAt(int anIndex) {
        checkMutableComboBoxModel();
        ((MutableComboBoxModel<T>) dataModel).removeElementAt(anIndex);
    }

    /**
     * Removes all items from the item list.
     */
    public void removeAllItems() {
        checkMutableComboBoxModel();
        MutableComboBoxModel<T> model = (MutableComboBoxModel<T>) dataModel;
        int size = model.getSize();

        if (model instanceof DefaultComboBoxModel) {
            ((DefaultComboBoxModel) model).removeAllElements();
        } else {
            for (int i = 0; i < size; ++i) {
                Object element = model.getElementAt(0);
                model.removeElement(element);
            }
        }
        selectedItemReminder = null;
        if (isEditable()) {
            editor.setItem(null);
        }
    }

    /**
     * Checks that the
     * <code>dataModel</code> is an instance of
     * <code>MutableComboBoxModel</code>. If not, it throws an exception.
     *
     * @exception RuntimeException if <code>dataModel</code> is not an instance
     * of <code>MutableComboBoxModel</code>.
     */
    void checkMutableComboBoxModel() {
        if (!(dataModel instanceof MutableComboBoxModel<?>)) {
            throw new RuntimeException("Cannot use this method with a non-Mutable data model.");
        }
    }

    /**
     * Causes the combo box to display its popup window.
     *
     * @see #setPopupVisible
     */
    public void showPopup() {
        setPopupVisible(true);
    }

    /**
     * Causes the combo box to close its popup window.
     *
     * @see #setPopupVisible
     */
    public void hidePopup() {
        setPopupVisible(false);
    }

    /**
     * Sets the visibility of the popup.
     */
    public void setPopupVisible(boolean v) {
        if (v) {
            popup.show();
        } else {
            popup.hide();
        }
        repaint();
    }

    /**
     * Determines the visibility of the popup.
     *
     * @return true if the popup is visible, otherwise returns false
     */
    public boolean isPopupVisible() {
        return popup.isVisible();
    }

    /**
     * Selection *
     */
    /**
     * Adds an
     * <code>ItemListener</code>. <p>
     * <code>aListener</code> will receive one or two
     * <code>ItemEvent</code>s when the selected item changes.
     *
     * @param aListener the <code>ItemListener</code> that is to be notified
     * @see #setSelectedItem
     */
    @Override
    public void addItemListener(ItemListener aListener) {
        listenerList.add(ItemListener.class, aListener);
    }

    /**
     * Removes an
     * <code>ItemListener</code>.
     *
     * @param aListener the <code>ItemListener</code> to remove
     */
    @Override
    public void removeItemListener(ItemListener aListener) {
        listenerList.remove(ItemListener.class, aListener);
    }

    /**
     * Returns an array of all the
     * <code>ItemListener</code>s added to this JComboBox with
     * addItemListener().
     *
     * @return all of the <code>ItemListener</code>s added or an empty array if
     * no listeners have been added
     * @since 1.4
     */
    public ItemListener[] getItemListeners() {
        return (ItemListener[]) listenerList.getListeners(ItemListener.class);
    }

    /**
     * Adds an
     * <code>ActionListener</code>. <p> The
     * <code>ActionListener</code> will receive an
     * <code>ActionEvent</code> when a selection has been made. If the combo box
     * is editable, then an
     * <code>ActionEvent</code> will be fired when editing has stopped.
     *
     * @param l the <code>ActionListener</code> that is to be notified
     * @see #setSelectedItem
     */
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }

    /**
     * Removes an
     * <code>ActionListener</code>.
     *
     * @param l the <code>ActionListener</code> to remove
     */
    public void removeActionListener(ActionListener l) {
        if ((l != null) && (getAction() == l)) {
            setAction(null);
        } else {
            listenerList.remove(ActionListener.class, l);
        }
    }

    /**
     * Returns an array of all the
     * <code>ActionListener</code>s added to this JComboBox with
     * addActionListener().
     *
     * @return all of the <code>ActionListener</code>s added or an empty array
     * if no listeners have been added
     * @since 1.4
     */
    public ActionListener[] getActionListeners() {
        return (ActionListener[]) listenerList.getListeners(
                ActionListener.class);
    }

    /**
     * Adds a
     * <code>PopupMenu</code> listener which will listen to notification
     * messages from the popup portion of the combo box. <p> For all standard
     * look and feels shipped with Java, the popup list portion of combo box is
     * implemented as a
     * <code>JPopupMenu</code>. A custom look and feel may not implement it this
     * way and will therefore not receive the notification.
     *
     * @param l the <code>PopupMenuListener</code> to add
     * @since 1.4
     */
    public void addPopupMenuListener(PopupMenuListener l) {
        listenerList.add(PopupMenuListener.class, l);
    }

    /**
     * Removes a
     * <code>PopupMenuListener</code>.
     *
     * @param l the <code>PopupMenuListener</code> to remove
     * @see #addPopupMenuListener
     * @since 1.4
     */
    public void removePopupMenuListener(PopupMenuListener l) {
        listenerList.remove(PopupMenuListener.class, l);
    }

    /**
     * Returns an array of all the
     * <code>PopupMenuListener</code>s added to this JComboBox with
     * addPopupMenuListener().
     *
     * @return all of the <code>PopupMenuListener</code>s added or an empty
     * array if no listeners have been added
     * @since 1.4
     */
    public PopupMenuListener[] getPopupMenuListeners() {
        return (PopupMenuListener[]) listenerList.getListeners(
                PopupMenuListener.class);
    }

    /**
     * Notifies
     * <code>PopupMenuListener</code>s that the popup portion of the combo box
     * will become visible. <p> This method is public but should not be called
     * by anything other than the UI delegate.
     *
     * @see #addPopupMenuListener
     * @since 1.4
     */
    public void firePopupMenuWillBecomeVisible() {
        Object[] listeners = listenerList.getListenerList();
        PopupMenuEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PopupMenuListener.class) {
                if (e == null) {
                    e = new PopupMenuEvent(this);
                }
                ((PopupMenuListener) listeners[i + 1]).popupMenuWillBecomeVisible(e);
            }
        }
    }

    /**
     * Notifies
     * <code>PopupMenuListener</code>s that the popup portion of the combo box
     * has become invisible. <p> This method is public but should not be called
     * by anything other than the UI delegate.
     *
     * @see #addPopupMenuListener
     * @since 1.4
     */
    public void firePopupMenuWillBecomeInvisible() {
        Object[] listeners = listenerList.getListenerList();
        PopupMenuEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PopupMenuListener.class) {
                if (e == null) {
                    e = new PopupMenuEvent(this);
                }
                ((PopupMenuListener) listeners[i + 1]).popupMenuWillBecomeInvisible(e);
            }
        }
    }

    /**
     * Notifies
     * <code>PopupMenuListener</code>s that the popup portion of the combo box
     * has been canceled. <p> This method is public but should not be called by
     * anything other than the UI delegate.
     *
     * @see #addPopupMenuListener
     * @since 1.4
     */
    public void firePopupMenuCanceled() {
        Object[] listeners = listenerList.getListenerList();
        PopupMenuEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == PopupMenuListener.class) {
                if (e == null) {
                    e = new PopupMenuEvent(this);
                }
                ((PopupMenuListener) listeners[i + 1]).popupMenuCanceled(e);
            }
        }
    }

    /**
     * Sets the action command that should be included in the event sent to
     * action listeners.
     *
     * @param aCommand a string containing the "command" that is sent to action
     * listeners; the same listener can then do different things depending on
     * the command it receives
     */
    public void setActionCommand(String aCommand) {
        actionCommand = aCommand;
    }

    /**
     * Returns the action command that is included in the event sent to action
     * listeners.
     *
     * @return the string containing the "command" that is sent to action
     * listeners.
     */
    public String getActionCommand() {
        return actionCommand;
    }
    private Action action;
    private PropertyChangeListener actionPropertyChangeListener;

    /**
     * Sets the
     * <code>Action</code> for the
     * <code>ActionEvent</code> source. The new
     * <code>Action</code> replaces any previously set
     * <code>Action</code> but does not affect
     * <code>ActionListeners</code> independently added with
     * <code>addActionListener</code>. If the
     * <code>Action</code> is already a registered
     * <code>ActionListener</code> for the
     * <code>ActionEvent</code> source, it is not re-registered. <p> Setting the
     * <code>Action</code> results in immediately changing all the properties
     * described in <a href="Action.html#buttonActions"> Swing Components
     * Supporting
     * <code>Action</code></a>. Subsequently, the combobox's properties are
     * automatically updated as the
     * <code>Action</code>'s properties change. <p> This method uses three other
     * methods to set and help track the
     * <code>Action</code>'s property values. It uses the
     * <code>configurePropertiesFromAction</code> method to immediately change
     * the combobox's properties. To track changes in the
     * <code>Action</code>'s property values, this method registers the
     * <code>PropertyChangeListener</code> returned by
     * <code>createActionPropertyChangeListener</code>. The default
     * {@code PropertyChangeListener} invokes the {@code actionPropertyChanged}
     * method when a property in the {@code Action} changes.
     *
     * @param a the <code>Action</code> for the <code>JComboBox</code>, *	or <code>null</code>.
     * @since 1.3
     * @see Action
     * @see #getAction
     * @see #configurePropertiesFromAction
     * @see #createActionPropertyChangeListener
     * @see #actionPropertyChanged
     * @beaninfo bound: true attribute: visualUpdate true description: the
     * Action instance connected with this ActionEvent source
     */
    public void setAction(Action a) {
        Action oldValue = getAction();
        if (action == null || !action.equals(a)) {
            action = a;
            if (oldValue != null) {
                removeActionListener(oldValue);
                oldValue.removePropertyChangeListener(actionPropertyChangeListener);
                actionPropertyChangeListener = null;
            }
            configurePropertiesFromAction(action);
            if (action != null) {
                // Don't add if it is already a listener
                if (!isListener(ActionListener.class, action)) {
                    addActionListener(action);
                }
                // Reverse linkage:
                actionPropertyChangeListener = createActionPropertyChangeListener(action);
                action.addPropertyChangeListener(actionPropertyChangeListener);
            }
            firePropertyChange("action", oldValue, action);
        }
    }

    private boolean isListener(Class<?> c, ActionListener a) {
        boolean isListener = false;
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == c && listeners[i + 1] == a) {
                isListener = true;
            }
        }
        return isListener;
    }

    /**
     * Returns the currently set
     * <code>Action</code> for this
     * <code>ActionEvent</code> source, or
     * <code>null</code> if no
     * <code>Action</code> is set.
     *
     * @return the <code>Action</code> for this <code>ActionEvent</code> source;
     * or <code>null</code>
     * @since 1.3
     * @see Action
     * @see #setAction
     */
    public Action getAction() {
        return action;
    }

    /**
     * Sets the properties on this combobox to match those in the specified
     * <code>Action</code>. Refer to <a href="Action.html#buttonActions"> Swing
     * Components Supporting
     * <code>Action</code></a> for more details as to which properties this
     * sets.
     *
     * @param a the <code>Action</code> from which to get the properties, or <code>null</code>
     * @since 1.3
     * @see Action
     * @see #setAction
     */
    protected void configurePropertiesFromAction(Action a) {
        if (a != null) {
            setEnabled(a.isEnabled());
            Object oTooltip = action.getValue(Action.SHORT_DESCRIPTION);
            if (oTooltip != null && oTooltip instanceof String) {
                setToolTipText((String) oTooltip);
            } else {
                setToolTipText(null);
            }
            setActionCommandFromAction(a);
        }
    }

    /**
     * Creates and returns a
     * <code>PropertyChangeListener</code> that is responsible for listening for
     * changes from the specified
     * <code>Action</code> and updating the appropriate properties. <p>
     * <b>Warning:</b> If you subclass this do not create an anonymous inner
     * class. If you do the lifetime of the combobox will be tied to that of the
     * <code>Action</code>.
     *
     * @param a the combobox's action
     * @since 1.3
     * @see Action
     * @see #setAction
     */
    protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
        return new ComboBoxActionPropertyChangeListener();
    }

    protected void actionPropertyChanged(Action action, String propertyName) {
        if (propertyName != null) {
            switch (propertyName) {
                case Action.ACTION_COMMAND_KEY:
                    setActionCommandFromAction(action);
                    break;
                case "enabled":
                    setEnabled(action.isEnabled());
                    break;
                case Action.SHORT_DESCRIPTION:
                    Object oTooltip = action.getValue(Action.SHORT_DESCRIPTION);
                    if (oTooltip != null && oTooltip instanceof String) {
                        setToolTipText((String) oTooltip);
                    } else {
                        setToolTipText(null);
                    }
                    break;
            }
        }
    }

    private void setActionCommandFromAction(Action a) {
        setActionCommand((a != null) ? (String) a.getValue(Action.ACTION_COMMAND_KEY) : null);
    }

    private class ComboBoxActionPropertyChangeListener
            implements PropertyChangeListener {

        ComboBoxActionPropertyChangeListener() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            configurePropertiesFromAction(action);
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on
     * this event type.
     *
     * @param e the event of interest
     *
     * @see EventListenerList
     */
    protected void fireItemStateChanged(ItemEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ItemListener.class) {
                // Lazily create the event:
                // if (changeEvent == null)
                // changeEvent = new ChangeEvent(this);
                ((ItemListener) listeners[i + 1]).itemStateChanged(e);
            }
        }
    }

    /**
     * Notifies all listeners that have registered interest for notification on
     * this event type.
     *
     * @see EventListenerList
     */
    protected void fireActionEvent() {
        if (!firingActionEvent) {
            // Set flag to ensure that an infinite loop is not created
            firingActionEvent = true;
            ActionEvent e = null;
            // Guaranteed to return a non-null array
            Object[] listeners = listenerList.getListenerList();
            long mostRecentEventTime = EventQueue.getMostRecentEventTime();
            int modifiers = 0;
            AWTEvent currentEvent = EventQueue.getCurrentEvent();
            if (currentEvent instanceof InputEvent) {
                modifiers = ((InputEvent) currentEvent).getModifiers();
            } else if (currentEvent instanceof ActionEvent) {
                modifiers = ((ActionEvent) currentEvent).getModifiers();
            }
            // Process the listeners last to first, notifying
            // those that are interested in this event
            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ActionListener.class) {
                    // Lazily create the event:
                    if (e == null) {
                        e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                getActionCommand(),
                                mostRecentEventTime, modifiers);
                    }
                    ((ActionListener) listeners[i + 1]).actionPerformed(e);
                }
            }
            firingActionEvent = false;
        }
    }

    /**
     * This protected method is implementation specific. Do not access directly
     * or override.
     */
    protected void selectedItemChanged() {
        if (selectedItemReminder != null) {
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED,
                    selectedItemReminder,
                    ItemEvent.DESELECTED));
        }

        // set the new selected item.
        selectedItemReminder = dataModel.getSelectedItem();

        if (selectedItemReminder != null) {
            fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED,
                    selectedItemReminder,
                    ItemEvent.SELECTED));
        }
    }

    /**
     * Returns an array containing the selected item. This method is implemented
     * for compatibility with
     * <code>ItemSelectable</code>.
     *
     * @return an array of <code>Objects</code> containing one element -- the
     * selected item
     */
    @Override
    public Object[] getSelectedObjects() {
        Object selectedObject = getSelectedItem();
        if (selectedObject == null) {
            return new Object[0];
        } else {
            Object result[] = new Object[1];
            result[0] = selectedObject;
            return result;
        }
    }

    /**
     * This method is public as an implementation side effect. do not call or
     * override.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object newItem = getEditor().getItem();
        setPopupVisible(false);
        getModel().setSelectedItem(newItem);
        String oldCommand = getActionCommand();
        setActionCommand("comboBoxEdited");
        fireActionEvent();
        setActionCommand(oldCommand);
    }

    /**
     * This method is public as an implementation side effect. do not call or
     * override.
     */
    @Override
    public void contentsChanged(ListDataEvent e) {
        Object oldSelection = selectedItemReminder;
        Object newSelection = dataModel.getSelectedItem();
        if (oldSelection == null || !oldSelection.equals(newSelection)) {
            selectedItemChanged();
            if (!selectingItem) {
                fireActionEvent();
            }
        }
    }

    /**
     * This method is public as an implementation side effect. do not call or
     * override.
     */
    @Override
    public void intervalAdded(ListDataEvent e) {
        if (selectedItemReminder != dataModel.getSelectedItem()) {
            selectedItemChanged();
        }
    }

    /**
     * This method is public as an implementation side effect. do not call or
     * override.
     */
    @Override
    public void intervalRemoved(ListDataEvent e) {
        contentsChanged(e);
    }

    /**
     * Selects the list item that corresponds to the specified keyboard
     * character and returns true, if there is an item corresponding to that
     * character. Otherwise, returns false.
     *
     * @param keyChar a char, typically this is a keyboard key typed by the user
     */
    public boolean selectWithKeyChar(char keyChar) {
        int index;

        if (keySelectionManager == null) {
            keySelectionManager = createDefaultKeySelectionManager();
        }

        index = keySelectionManager.selectionForKey(keyChar, getModel());
        if (index != -1) {
            setSelectedIndex(index);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Enables the combo box so that items can be selected. When the combo box
     * is disabled, items cannot be selected and values cannot be typed into its
     * field (if it is editable).
     *
     * @param b a boolean value, where true enables the component and false
     * disables it
     * @beaninfo bound: true preferred: true description: Whether the combo box
     * is enabled.
     */
    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        if (dropdownButton != null) {
            dropdownButton.setEnabled(b);
        }
        if (editor != null && editor.getEditorComponent() != null) {
            editor.getEditorComponent().setEnabled(b);
        }
        firePropertyChange("enabled", !isEnabled(), isEnabled());
    }

    /**
     * Initializes the editor with the specified item.
     *
     * @param anEditor the <code>ComboBoxEditor</code> that displays the list
     * item in the combo box field and allows it to be edited
     * @param anItem the object to display and edit in the field
     */
    public void configureEditor(ComboBoxEditor anEditor, Object anItem) {
        anEditor.setItem(anItem);
    }

    /**
     * Handles
     * <code>KeyEvent</code>s, looking for the Tab key. If the Tab key is found,
     * the popup window is closed.
     *
     * @param e the <code>KeyEvent</code> containing the keyboard key that was
     * pressed
     */
    @Override
    public void processKeyEvent(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            hidePopup();
        }
        super.processKeyEvent(e);
    }

    /**
     * Sets the object that translates a keyboard character into a list
     * selection. Typically, the first selection with a matching first character
     * becomes the selected item.
     *
     * @beaninfo expert: true description: The objects that changes the
     * selection when a key is pressed.
     */
    public void setKeySelectionManager(KeySelectionManager<T> aManager) {
        keySelectionManager = aManager;
    }

    /**
     * Returns the list's key-selection manager.
     *
     * @return the <code>KeySelectionManager</code> currently in use
     */
    public KeySelectionManager<T> getKeySelectionManager() {
        return keySelectionManager;
    }

    /* Accessing the model */
    /**
     * Returns the number of items in the list.
     *
     * @return an integer equal to the number of items in the list
     */
    public int getItemCount() {
        return dataModel.getSize();
    }

    /**
     * Returns the list item at the specified index. If
     * <code>index</code> is out of range (less than zero or greater than or
     * equal to size) it will return
     * <code>null</code>.
     *
     * @param index an integer indicating the list position, where the first
     * item starts at zero
     * @return the <code>Object</code> at that list position; or
     * <code>null</code> if out of range
     */
    public Object getItemAt(int index) {
        return dataModel.getElementAt(index);
    }

    /**
     * Returns an instance of the default key-selection manager.
     *
     * @return the <code>KeySelectionManager</code> currently used by the list
     * @see #setKeySelectionManager
     */
    protected KeySelectionManager<T> createDefaultKeySelectionManager() {
        return new DefaultKeySelectionManager();
    }

    /**
     * The interface that defines a
     * <code>KeySelectionManager</code>. To qualify as a
     * <code>KeySelectionManager</code>, the class needs to implement the method
     * that identifies the list index given a character and the combo box data
     * model.
     */
    public interface KeySelectionManager<T> {

        /**
         * Given
         * <code>aKey</code> and the model, returns the row that should become
         * selected. Return -1 if no match was found.
         *
         * @param aKey a char value, usually indicating a keyboard key that was
         * pressed
         * @param aModel a ComboBoxModel -- the component's data model,
         * containing the list of selectable items
         * @return an int equal to the selected row, where 0 is the first item
         * and -1 is none.
         */
        int selectionForKey(char aKey, ComboBoxModel<T> aModel);
    }

    class DefaultKeySelectionManager implements KeySelectionManager<T>, Serializable {

        @Override
        public int selectionForKey(char aKey, ComboBoxModel<T> aModel) {
            int i, c;
            int currentSelection = -1;
            Object selectedItem = aModel.getSelectedItem();
            String v;
            String pattern;

            if (selectedItem != null) {
                for (i = 0, c = aModel.getSize(); i < c; i++) {
                    if (selectedItem == aModel.getElementAt(i)) {
                        currentSelection = i;
                        break;
                    }
                }
            }

            pattern = ("" + aKey).toLowerCase();
            aKey = pattern.charAt(0);

            for (i = ++currentSelection, c = aModel.getSize(); i < c; i++) {
                Object elem = aModel.getElementAt(i);
                if (elem != null && elem.toString() != null) {
                    v = elem.toString().toLowerCase();
                    if (v.length() > 0 && v.charAt(0) == aKey) {
                        return i;
                    }
                }
            }

            for (i = 0; i < currentSelection; i++) {
                Object elem = aModel.getElementAt(i);
                if (elem != null && elem.toString() != null) {
                    v = elem.toString().toLowerCase();
                    if (v.length() > 0 && v.charAt(0) == aKey) {
                        return i;
                    }
                }
            }
            return -1;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!isEditable && renderer != null) {
            Component crComp = renderer.getListCellRendererComponent(popup.getList(), (T) getSelectedItem(), getSelectedIndex(), false, false);
            if (crComp != null && crComp instanceof JComponent) {
                JComponent rComp = (JComponent) crComp;
                Rectangle bounds = getBounds();
                bounds.x = 0;
                bounds.y = 0;
                bounds.width -= tools.getWidth();
                rComp.setBounds(bounds);
                rComp.setFont(getFont());
                rComp.paint(g);
            }
        }
    }

    /**
     * Returns a string representation of this
     * <code>JComboBox</code>. This method is intended to be used only for
     * debugging purposes, and the content and format of the returned string may
     * vary between implementations. The returned string may be empty but may
     * not be
     * <code>null</code>.
     *
     * @return a string representation of this <code>JComboBox</code>
     */
    @Override
    protected String paramString() {
        String selectedItemReminderString = (selectedItemReminder != null ? selectedItemReminder.toString() : "");
        String isEditableString = (isEditable ? "true" : "false");
        String lightWeightPopupEnabledString = (lightWeightPopupEnabled ? "true" : "false");

        return super.paramString()
                + ",isEditable=" + isEditableString
                + ",lightWeightPopupEnabled=" + lightWeightPopupEnabledString
                + ",maximumRowCount=" + maximumRowCount
                + ",selectedItemReminder=" + selectedItemReminderString;
    }
}
