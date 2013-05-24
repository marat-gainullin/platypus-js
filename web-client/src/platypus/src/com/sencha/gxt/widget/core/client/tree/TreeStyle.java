/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.tree;

import com.google.gwt.resources.client.ImageResource;

/**
 * Style information for Trees. There are two types for tree items: nodes and
 * leafs. Leafs are item's without children. Nodes are items with children or
 * items with their leaf flag set to false.
 */
public class TreeStyle {

  private ImageResource leafIcon;
  private ImageResource nodeCloseIcon;
  private ImageResource nodeOpenIcon;
  private ImageResource jointOpenIcon;
  private ImageResource jointCloseIcon;

  public ImageResource getJointCloseIcon() {
    return jointCloseIcon;
  }

  public ImageResource getJointOpenIcon() {
    return jointOpenIcon;
  }

  /**
   * Returns the icon for leaf items.
   * 
   * @return the leaf icon
   */
  public ImageResource getLeafIcon() {
    return leafIcon;
  }

  /**
   * Returns the global closed node icon.
   * 
   * @return the node closed icon
   */
  public ImageResource getNodeCloseIcon() {
    return nodeCloseIcon;
  }

  /**
   * Returns the global open node icon.
   * 
   * @return the node open icon
   */
  public ImageResource getNodeOpenIcon() {
    return nodeOpenIcon;
  }

  public void setJointCloseIcon(ImageResource jointCloseIcon) {
    this.jointCloseIcon = jointCloseIcon;
  }

  public void setJointOpenIcon(ImageResource jointOpenIcon) {
    this.jointOpenIcon = jointOpenIcon;
  }

  /**
   * Sets the global icon style for leaf tree items.
   * 
   * @param itemIcon the leaf icon
   */
  public void setLeafIcon(ImageResource itemIcon) {
    this.leafIcon = itemIcon;
  }

  /**
   * Sets the icon used for closed tree items.
   * 
   * @param folderCloseIcon the closed folder icon
   */
  public void setNodeCloseIcon(ImageResource folderCloseIcon) {
    this.nodeCloseIcon = folderCloseIcon;
  }

  /**
   * Sets the global icon for expanded tree items.
   * 
   * @param folderOpenIcon the open folder icon
   */
  public void setNodeOpenIcon(ImageResource folderOpenIcon) {
    this.nodeOpenIcon = folderOpenIcon;
  }

}
